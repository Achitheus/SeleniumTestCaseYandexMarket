package helpers.pageable;

import io.qameta.allure.model.Status;
import org.openqa.selenium.WebDriver;
import org.opentest4j.MultipleFailuresError;

import java.util.*;

import static helpers.AllureCustom.stepWithoutRewriting;
import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

public class PageableChecker<PAGE_OBJ extends Pageable> {
    private final WebDriver driver;
    private final List<AssertionError> errorList = new ArrayList<>();
    private boolean pageableCheckFailed;
    private boolean lazyMode = true;
    private final PAGE_OBJ target;
    private final List<PageCheck<PAGE_OBJ>> checkList;

    public PageableChecker(PAGE_OBJ target, WebDriver driver) {
        this.driver = driver;
        this.target = target;
        checkList = new ArrayList<>();
    }

    public PageableChecker<PAGE_OBJ> beLazy(boolean value) {
        this.lazyMode = value;
        return this;
    }

    public PAGE_OBJ run() {
        runWithoutThrowing();
        assertAll();
        return target;
    }

    public void assertAll() {
        checkList.forEach(check -> errorList.addAll(check.getCollectedErrors()));
        if (!errorList.isEmpty())
            throw new MultipleFailuresError("Pageable check assertion failures:", errorList);
    }

    public PageableChecker<PAGE_OBJ> addCheckThatEachElement(String continueMethodName, PageCheck<PAGE_OBJ> check) {
        check.setDescription(continueMethodName);
        checkList.add(check);
        return this;
    }

    public PageableChecker<PAGE_OBJ> runWithoutThrowing() {
        stepWithoutRewriting("Постраничная проверка. Режим " + (lazyMode
                        ? "lazy (упавший чек на последующих стр. пропускается)"
                        : "eager (чеки не скипаются, проверяются все страницы)"),
                () -> {
                    if (checkList.isEmpty()) {
                        throw new RuntimeException("Checklist is empty");
                    }
                    List<PageCheck<PAGE_OBJ>> activeChecks = new ArrayList<>(checkList);
                    int currentPageNumber = 0;
                    do {
                        currentPageNumber++;
                        final int pageNum = currentPageNumber;
                        stepWithoutRewriting("Страница " + currentPageNumber + (lazyMode ? ". Активных проверок: " + activeChecks.size() : ""),
                                () -> {
                                    if (!processPageCheck(pageNum, activeChecks)) {
                                        pageableCheckFailed = true;
                                        getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
                                        addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
                                    }
                                }
                        );
                    } while (!activeChecks.isEmpty() && currentPageNumber < 1_000 && target.nextPage());
                    if (pageableCheckFailed) {
                        getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
                    }
                }
        );
        return this;
    }

    private boolean processPageCheck(int currentPageNumber, List<PageCheck<PAGE_OBJ>> checkList) {
        boolean pagePassed = true;
        ListIterator<PageCheck<PAGE_OBJ>> checksIter = checkList.listIterator();
        while (checksIter.hasNext()) {
            PageCheck<PAGE_OBJ> check = checksIter.next();
            PageCheckResult pageCheckResult = stepWithoutRewriting("step", () -> {
                PageCheckResult checkResultInner = check.perform(currentPageNumber, target, driver);
                getLifecycle().updateStep(step -> step.setName(checkResultInner.toString()));

                if (checkResultInner.isFailed()) {
                    getLifecycle().updateStep(s -> s
                            .setStatus(Status.FAILED)
                            .setStatusDetails(getStatusDetails(checkResultInner.getError().orElse(null)).orElse(null))
                    );
                }
                return checkResultInner;
            });
            if (pageCheckResult.isFailed()) {
                pagePassed = false;
                if (lazyMode) {
                    checksIter.remove();
                }
            }
        }
        return pagePassed;
    }

    private static String collectionToString(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        if (!it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (true) {
            Object e = it.next();
            sb.append(e);
            if (!it.hasNext())
                return sb.append(']').toString();
            sb.append('\n');
        }
    }

}
