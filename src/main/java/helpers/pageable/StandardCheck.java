package helpers.pageable;

import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static helpers.StringsUtils.collectionToString;
import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

public class StandardCheck<E, PAGE> implements Check<PAGE> {
    private String checkDescr;
    private boolean failed = false;
    private int checkedElementCount;
    private final List<AssertionError> errorList = new ArrayList<>();
    List<E> failedElementList;
    private final Predicate<E> condition;
    private final Function<PAGE, Collection<E>> getElements;

    public StandardCheck(Function<PAGE, Collection<E>> getElements, Predicate<E> condition) {
        this.condition = condition;
        this.getElements = getElements;
    }

    public void perform(int currentPageNumber, PAGE target, WebDriver driver) {
        final String uuid = UUID.randomUUID().toString();
        getLifecycle().startStep(uuid, new StepResult().setName("step"));

        Collection<E> elementCollection = getElements.apply(target);
        checkedElementCount = elementCollection.size();
        failedElementList = elementCollection.stream()
                .filter(condition.negate())
                .collect(Collectors.toList());
        failed = !failedElementList.isEmpty();

        if (failed) {
            AssertionError error = new AssertionError(this.toString(currentPageNumber));
            errorList.add(error);
            addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
            addAttachment("Failed elements", "text/plain", collectionToString(failedElementList),".txt");
            getLifecycle().updateStep(s -> s
                    .setName(this.toString())
                    .setStatus(failed ? Status.FAILED : Status.PASSED)
                    .setStatusDetails(getStatusDetails(error).orElse(null))
            );
        } else {
            getLifecycle().updateStep(s -> s.setName(this.toString()).setStatus(Status.PASSED));
        }
        getLifecycle().stopStep();
    }

    @Override
    public Collection<AssertionError> getErrorList() {
        return errorList;
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
