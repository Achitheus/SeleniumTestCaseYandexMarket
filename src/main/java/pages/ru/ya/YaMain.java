package pages.ru.ya;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;

/**
 * Класс для взаимодействия с главной страницей яндекса.
 *
 * @author Юрий Юрченко
 */
public class YaMain {

    private final WebDriver driver;
    /**
     * Поле поиска.
     */
    private final WebElement searchField;

    /**
     * Создает объект главной страницы яндекса.
     *
     * @param driver веб-драйвер для обращения к браузеру.
     */
    public YaMain(WebDriver driver) {
        this.driver = driver;
        searchField = driver.findElement(By.id("text"));
    }

    /**
     * Открывает сервис с регистро-чувствительным названием {@code serviceTitle} и переключается на его вкладку.
     *
     * @param serviceTitle название сервиса (чувствительно к регистру).
     * @author Юрий Юрченко
     */
    @Step("Переход в сервис \"{serviceTitle}\"")
    public void goToService(String serviceTitle) {
        searchField.click();
        WebElement serviceButton = driver.findElement(By.xpath(
                "//ul[@class='services-suggest__list']//a[.//div[text()='" + serviceTitle + "']]"));
        serviceButton.click();
        Set<String> windowHandles = driver.getWindowHandles();
        String currentWindow = driver.getWindowHandle();
        for (String window : windowHandles) {
            if (!window.equals(currentWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }
    }
}
