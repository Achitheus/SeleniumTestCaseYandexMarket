package helpers.pageable;

import java.util.Collection;

public interface ElementsCheckWithErrorCollector<T> {

    ElementsCheckResult perform(T target);

    Collection<? extends AssertionError> getCollectedErrors();
}
