package pages.ru.yandex.market;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MarketHeader {
    protected final WebDriver driver;
    private WebDriverWait wait;

    private WebElement catalogButton;
    private WebElement searchField;
    private WebElement searchSubmitButton;

    public MarketHeader(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        catalogButton = driver.findElement(By.xpath("//*[text()='Каталог']"));
        searchField = driver.findElement(By.xpath("//header//input[@type='text' and @id='header-search']"));
        searchSubmitButton = driver.findElement(By.xpath("//header//button[@type='submit']"));
    }

    public void toCategoryProductsPage(String sectionTitle, String categoryTitle) {
        catalogButton.click();
        WebElement section = driver.findElement(By.xpath(
                "//*[@data-zone-name='catalog-content']//*[@role='tablist']//li[.//span[text()='"+sectionTitle+"']]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(section).perform();
        wait.until(ExpectedConditions.attributeToBe(section, "aria-selected", "true"));
        driver.findElement(By.xpath("//*[@role='tabpanel']//a[text()='" + categoryTitle + "']")).click();
    }

    public void findProduct(String text) {
        searchField.click();
        searchField.clear();
        searchField.sendKeys(text);
        searchSubmitButton.click();
    }
}
