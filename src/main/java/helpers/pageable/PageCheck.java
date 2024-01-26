package helpers.pageable;

import org.openqa.selenium.WebDriver;

import java.util.Collection;

public interface PageCheck<PAGE_OBJ> {

    PageCheckResult perform(int pageNumber, PAGE_OBJ target, WebDriver driver);

    Collection<? extends AssertionError> getCollectedErrors();

    void setDescription(String description);
}
