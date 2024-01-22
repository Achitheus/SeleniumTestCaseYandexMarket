package helpers.pageable;

import org.openqa.selenium.WebDriver;
import org.opentest4j.MultipleFailuresError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static helpers.AllureCustom.markOuterStepAsFailedAndStop;
import static io.qameta.allure.Allure.step;

public class PageableChecker<PAGE_OBJ extends Pageable> {
    private final WebDriver driver;
    private final List<AssertionError> errorList = new ArrayList<>();
    private boolean pageableCheckFailed = false;
    private boolean checkAllPages = false;
    private final PAGE_OBJ target;
    private final List<Check<PAGE_OBJ>> checkList;

    public PageableChecker(PAGE_OBJ target, WebDriver driver) {
        this.driver = driver;
        this.target = target;
        checkList = new ArrayList<>();
    }

    public PageableChecker<PAGE_OBJ> checkAllPages(boolean value) {
        this.checkAllPages = value;
        return this;
    }

    public PAGE_OBJ run() {
        runWithoutThrowing();
        assertAll();
        return target;
    }

    public PageableChecker<PAGE_OBJ> runWithoutThrowing() {
        step("Постраничная проверка. Режим " + (checkAllPages
                        ? "eager (чеки не скипаются, проверяются все страницы)"
                        : "lazy (упавший чек на последующих стр. пропускается)"),
                () -> {
                    if (checkList.isEmpty()) {
                        throw new RuntimeException("Checklist is empty");
                    }
                    int currentPageNumber = 0;
                    do {
                        currentPageNumber++;
                        final int pageNum = currentPageNumber;
                        step("Страница " + currentPageNumber + (checkAllPages ? "" : ". Активных проверок: " + checkList.size()),
                                () -> processPageCheck(pageNum));
                    } while (!checkList.isEmpty() && currentPageNumber < 3 && target.nextPage());
                    if (pageableCheckFailed) {
                        markOuterStepAsFailedAndStop();
                    }
                }
        );
        checkList.forEach(check -> errorList.addAll(check.getErrorList()));
        return this;
    }

    public void assertAll() {
        if (!errorList.isEmpty())
            throw new MultipleFailuresError("Pageable check assertion failures:", errorList);
    }

    public <E> PageableChecker<PAGE_OBJ> addCheckThatEachElement(String continueMethodName, Check<PAGE_OBJ> target) {
        target.setDescription(continueMethodName);
        checkList.add(target);
        return this;
    }

    private void processPageCheck(int currentPageNumber) {
        boolean pageCheckFailed = false;

        ListIterator<Check<PAGE_OBJ>> checksIter = checkList.listIterator();
        while (checksIter.hasNext()) {
            Check<PAGE_OBJ> check = checksIter.next();
            check.perform(currentPageNumber, target, driver);
            if (!check.isFailed()) {
                continue;
            }
            pageCheckFailed = true;
            pageableCheckFailed = true;
            if (checkAllPages) {
                check.setFailed(false);
            } else {
                checksIter.remove();
            }
        }
        if (pageCheckFailed) {
            markOuterStepAsFailedAndStop();
        }
    }

}
