package helpers.pageable;

import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.WebDriver;
import org.opentest4j.MultipleFailuresError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static helpers.StringsUtils.collectionToString;
import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

public class AssertionCheck<PAGE_OBJ, E> implements Check<PAGE_OBJ>{
    private final List<MultipleFailuresError> multipleFailuresErrorList = new ArrayList<>();
    private final Function<PAGE_OBJ, Collection<E>> getElements;
    private final Consumer<E> condition;
    private String checkDescr;

    private boolean failed = false;
    private int checkedElementCount;
    private List<E> failedElementList;

    public AssertionCheck(Function<PAGE_OBJ, Collection<E>> getElements, Consumer<E> condition) {
        this.condition = condition;
        this.getElements = getElements;
    }

    @Override
    public void perform(int pageNumber, PAGE_OBJ target, WebDriver driver) {
        final String uuid = UUID.randomUUID().toString();
        getLifecycle().startStep(uuid, new StepResult().setName("step"));

        Collection<E> elementCollection = getElements.apply(target);
        checkedElementCount = elementCollection.size();
        failedElementList = new ArrayList<>();
        List<AssertionError> errorList = new ArrayList<>();

        elementCollection.forEach(el -> {
            try {
                condition.accept(el);
            } catch (AssertionError error) {
                failed = true;
                failedElementList.add(el);
                errorList.add(error);
            }
        });
        if (failed) {
            MultipleFailuresError multipleError = new MultipleFailuresError(toString(pageNumber), errorList);
            multipleFailuresErrorList.add(multipleError);
            addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
            addAttachment("Failed elements", "text/plain", collectionToString(failedElementList), ".txt");
            getLifecycle().updateStep(s -> s
                    .setStatus(failed ? Status.FAILED : Status.PASSED)
                    .setStatusDetails(getStatusDetails(multipleError).orElse(null))
                    .setName(this.toString(pageNumber))
            );
        } else {
            getLifecycle().updateStep(s -> s.setName(this.toString(pageNumber)).setStatus(Status.PASSED));
        }
        getLifecycle().stopStep();
    }

    @Override
    public Collection<MultipleFailuresError> getErrorList() {
        return multipleFailuresErrorList;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public void setFailed(boolean isFailed) {
        failed = isFailed;
    }

    @Override
    public void setDescription(String description) {
        this.checkDescr = description;
    }

    public String toString(int currentPageNumber) {
        if (failed) {
            return "Стр. " + currentPageNumber + ". Элемент (всего таких: "
                    + failedElementList.size() + ") \"" + failedElementList.get(0)
                    + "\" не " + checkDescr;
        } else {
            return "Стр. " + currentPageNumber + ". Каждый элемент ("
                    + checkedElementCount + "шт.)" + checkDescr;
        }
    }
}
