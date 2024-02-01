package helpers.pageable;

import java.util.Collection;

public interface ElementsCheckWithErrorCollector {

    ElementsCheckResult perform();

    Collection<? extends AssertionError> getCollectedErrors();
}
