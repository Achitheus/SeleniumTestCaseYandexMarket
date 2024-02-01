package helpers.pageable;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateElementsCheck<PAGE_OBJ, E> extends ElementsCheck<PAGE_OBJ> {
    private final Predicate<E> condition;
    private final Function<PAGE_OBJ, Collection<E>> elementsProvider;

    public PredicateElementsCheck(Function<PAGE_OBJ, Collection<E>> elementsProvider, Predicate<E> passCondition) {
        this.condition = passCondition;
        this.elementsProvider = elementsProvider;
    }

    public ElementsCheckResult performWithoutNumberIncrement() {
        Collection<E> elementCollection = elementsProvider.apply(target);
        List<E> failedElementList = elementCollection.stream()
                .filter(condition.negate())
                .collect(Collectors.toList());
        ElementsCheckResult elementsCheckResult = new ElementsCheckResult(passedElementDescription, failedElementList, checkNumber, elementCollection.size());
        elementsCheckResult.getError().ifPresent(collectedErrors::add);
        return elementsCheckResult;
    }
}
