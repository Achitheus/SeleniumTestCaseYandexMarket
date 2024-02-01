package helpers.pageable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateCheckThatEachElement<PAGE_OBJ, E> extends ElementsCheck<PAGE_OBJ> {
    private final Predicate<E> condition;
    private final Function<PAGE_OBJ, Collection<E>> elementsProvider;

    public PredicateCheckThatEachElement(String continueConstructorName, Function<PAGE_OBJ, Collection<E>> elementsProvider, Predicate<E> passCondition) {
        super(continueConstructorName);
        this.condition = passCondition;
        this.elementsProvider = elementsProvider;
    }

    public ElementsCheckResult performWithoutNumberIncrement(PAGE_OBJ target) {
        Collection<E> elementCollection = elementsProvider.apply(target);
        List<E> failedElementList = elementCollection.stream()
                .filter(condition.negate())
                .collect(Collectors.toList());
        ElementsCheckResult elementsCheckResult = new ElementsCheckResult(passedElementDescription, failedElementList, checkNumber, elementCollection.size());
        elementsCheckResult.getError().ifPresent(collectedErrors::add);
        return elementsCheckResult;
    }
}
