package helpers.pageable;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AssertionPageCheck<PAGE_OBJ, E> implements PageCheck<PAGE_OBJ> {
    private final List<AssertionError> collectedErrors = new ArrayList<>();
    private final Function<PAGE_OBJ, Collection<E>> elementsProvider;
    private final BiConsumer<E, String> assertion;
    private String checkDescr;

    public AssertionPageCheck(Function<PAGE_OBJ, Collection<E>> elementsProvider, BiConsumer<E, String> assertion) {
        this.assertion = assertion;
        this.elementsProvider = elementsProvider;
    }

    @Override
    public PageCheckResult perform(int pageNumber, PAGE_OBJ target, WebDriver driver) {
        Collection<E> elementCollection = elementsProvider.apply(target);
        List<E> failedElementList = new ArrayList<>();
        List<AssertionError> errorList = new ArrayList<>();
        for (E el : elementCollection) {
            try {
                assertion.accept(el, "Стр. " + pageNumber + ". Элемент \"" + el + "\" не " + checkDescr);
            } catch (AssertionError error) {
                failedElementList.add(el);
                errorList.add(error);
            }
        }

        PageCheckResult pageCheckResult = new PageCheckResult(checkDescr, errorList, failedElementList, pageNumber, elementCollection.size());
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
