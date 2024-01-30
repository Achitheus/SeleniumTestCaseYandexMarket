package pages.ru.yandex.market;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MarketHeader {
    protected final WebDriver driver;
    protected WebDriverWait wait;

    private final String catalogButtonLocator = "//*[text()='Каталог']";
    private final String searchFieldLocator = "//header//input[@type='text' and @id='header-search']";
    private final String searchSubmitButtonLocator = "//header//button[@type='submit']";

    public MarketHeader(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Step("Переход в секцию \"{sectionTitle}\", категория - \"{categoryTitle}\"")
    public void toCategoryProductsPage(String sectionTitle, String categoryTitle) {
        driver.findElement(By.xpath(catalogButtonLocator)).click();
        WebElement section = driver.findElement(By.xpath(
                "//*[@data-zone-name='catalog-content']//*[@role='tablist']//li[.//span[text()='"+sectionTitle+"']]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(section).perform();
        wait.until(ExpectedConditions.attributeToBe(section, "aria-selected", "true"));
        driver.findElement(By.xpath("//*[@role='tabpanel']//a[text()='" + categoryTitle + "']")).click();
    }

    @Step("Поиск по запросу \"{text}\"")
    public void findProduct(String text) {
        WebElement searchField = driver.findElement(By.xpath(searchFieldLocator));
        searchField.click();
        searchField.clear();
        searchField.sendKeys(text);
        driver.findElement(By.xpath(searchSubmitButtonLocator)).click();
    }
}
