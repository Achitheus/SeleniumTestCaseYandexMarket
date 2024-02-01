package helpers.pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ElementsCheck<T> implements ElementsCheckWithErrorCollector {
    private boolean initializationCompleted;
    protected T target;
    protected String passedElementDescription;
    protected final List<AssertionError> collectedErrors = new ArrayList<>();
    protected int checkNumber = 0;

    @Override
    final public ElementsCheckResult perform() {
        if (!initializationCompleted) {
            throw new RuntimeException("ElementsCheck object isn't correctly initialized. " +
                    "Use ElementsCheck.completeInitialization()");
        }
        checkNumber++;
        return performWithoutNumberIncrement();
    }

    protected abstract ElementsCheckResult performWithoutNumberIncrement();

    protected void completeInitialization(String checkDescr, T pageObj) {
        initializationCompleted = true;
        this.passedElementDescription = checkDescr;
        this.target = pageObj;
    }

    @Override
    final public Collection<? extends AssertionError> getCollectedErrors() {
        return collectedErrors;
    }
}
