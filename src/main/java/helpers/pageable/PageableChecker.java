package helpers.pageable;

import io.qameta.allure.model.Status;
import org.openqa.selenium.WebDriver;
import org.opentest4j.MultipleFailuresError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
    private final List<ElementsCheckWithErrorCollector<PAGE_OBJ>> checkList;
    private int pageCount = 1_000;

    public PageableChecker(PAGE_OBJ target, WebDriver driver) {
        this.driver = driver;
        this.target = target;
        checkList = new ArrayList<>();
    }

    public PageableChecker<PAGE_OBJ> beLazy(boolean value) {
        this.lazyMode = value;
        return this;
    }

    public PageableChecker<PAGE_OBJ> setPageCount(int pageCount) {
        this.pageCount = pageCount;
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

    public PageableChecker<PAGE_OBJ> addCheck(ElementsCheckWithErrorCollector<PAGE_OBJ> check) {
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
                    List<ElementsCheckWithErrorCollector<PAGE_OBJ>> activeChecks = new ArrayList<>(checkList);
                    int currentPageNumber = 0;
                    do {
                        currentPageNumber++;
                        stepWithoutRewriting("Страница " + currentPageNumber + (lazyMode ? ". Активных проверок: " + activeChecks.size() : ""),
                                () -> {
                                    if (!processPageCheck(activeChecks)) {
                                        pageableCheckFailed = true;
                                        getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
                                        addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
                                    }
                                }
                        );
                    } while (!activeChecks.isEmpty() && currentPageNumber < pageCount && target.nextPage());
                    if (pageableCheckFailed) {
                        getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
                    }
                }
        );
        return this;
    }

    private boolean processPageCheck(List<ElementsCheckWithErrorCollector<PAGE_OBJ>> mutableCheckList) {
        boolean pagePassed = true;
        ListIterator<ElementsCheckWithErrorCollector<PAGE_OBJ>> checksIter = mutableCheckList.listIterator();
        while (checksIter.hasNext()) {
            ElementsCheckWithErrorCollector<PAGE_OBJ> check = checksIter.next();
            ElementsCheckResult elementsCheckResult = stepWithoutRewriting("step", () -> {
                ElementsCheckResult checkResultInner = check.perform(target);
                getLifecycle().updateStep(step -> step.setName(checkResultInner.toString()));

                if (checkResultInner.isFailed()) {
                    getLifecycle().updateStep(s -> s
                            .setStatus(Status.FAILED)
                            .setStatusDetails(getStatusDetails(checkResultInner.getError().orElse(null)).orElse(null))
                    );
                }
                return checkResultInner;
            });
            if (elementsCheckResult.isFailed()) {
                pagePassed = false;
                if (lazyMode) {
                    checksIter.remove();
                }
            }
        }
        return pagePassed;
    }

}
