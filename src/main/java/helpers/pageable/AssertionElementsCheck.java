package helpers.pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AssertionElementsCheck<PAGE_OBJ, E> extends ElementsCheck<PAGE_OBJ> {
    private final Function<PAGE_OBJ, Collection<E>> elementsProvider;
    private final BiConsumer<E, String> assertion;

    public AssertionElementsCheck(Function<PAGE_OBJ, Collection<E>> elementsProvider, BiConsumer<E, String> assertion) {
        this.assertion = assertion;
        this.elementsProvider = elementsProvider;
    }

    @Override
    public ElementsCheckResult performWithoutNumberIncrement() {
        Collection<E> elementCollection = elementsProvider.apply(target);
        List<AssertionError> errorList = new ArrayList<>();
        for (E el : elementCollection) {
            try {
                assertion.accept(el, "Элемент \"" + el + "\" не " + passedElementDescription);
            } catch (AssertionError error) {
                errorList.add(error);
            }
        }

        ElementsCheckResult elementsCheckResult = new ElementsCheckResult(passedElementDescription, checkNumber, errorList, elementCollection.size());
        elementsCheckResult.getError().ifPresent(collectedErrors::add);
        return elementsCheckResult;
    }
}
