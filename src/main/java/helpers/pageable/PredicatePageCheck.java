package helpers.pageable;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicatePageCheck<E, PAGE_OBJ> implements PageCheck<PAGE_OBJ> {
    private String checkDescr;
    private final List<AssertionError> collectedErrors = new ArrayList<>();
    private final Predicate<E> condition;
    private final Function<PAGE_OBJ, Collection<E>> elementsProvider;

    public PredicatePageCheck(Function<PAGE_OBJ, Collection<E>> elementsProvider, Predicate<E> passCondition) {
        this.condition = passCondition;
        this.elementsProvider = elementsProvider;
    }

    public PageCheckResult perform(int currentPageNumber, PAGE_OBJ target, WebDriver driver) {
        Collection<E> elementCollection = elementsProvider.apply(target);
        List<E> failedElementList = elementCollection.stream()
                .filter(condition.negate())
                .collect(Collectors.toList());
        PageCheckResult pageCheckResult = new PageCheckResult(checkDescr, failedElementList, currentPageNumber, elementCollection.size());
        pageCheckResult.getError().ifPresent(collectedErrors::add);
        return pageCheckResult;
    }

    @Override
    public Collection<AssertionError> getCollectedErrors() {
        return collectedErrors;
    }

    @Override
    public void setDescription(String description) {
        this.checkDescr = description;
    }

}
