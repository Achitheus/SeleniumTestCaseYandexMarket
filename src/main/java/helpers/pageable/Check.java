package helpers.pageable;

import org.openqa.selenium.WebDriver;

import java.util.Collection;

public interface Check<PAGE_OBJ> {

    void perform(int pageNumber, PAGE_OBJ target, WebDriver driver);

    Collection<? extends AssertionError> getErrorList();

    boolean isFailed();

    void setFailed(boolean isFailed);

    void setDescription(String description);
}
