package pages.ru.ya;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;

/**
 * Класс для доступа к главной странице яндекса (PageObject)
 *
 * @author Юрий Юрченко
 */
public class YaMain {
    /**
     * вебдрайвер для обращения к браузеру
     */
    private final WebDriver chromeDriver;
    /**
     * кнопка "открыть окно сервисов"
     */
    private final WebElement searchField;

    /**
     * Создает объект страницы с инициализированными полями {@code serviceButton}, {@code marketButton}
     *
     * @param chromeDriver вебдрайвер для обращения к браузеру
     */
    public YaMain(WebDriver chromeDriver) {
        this.chromeDriver = chromeDriver;
        searchField = chromeDriver.findElement(By.id("text"));
    }

    /**
     * Открывает окно сервисов и переходит в первый, содержащий в названии {@code serviceTitle}.
     * Переключается на открывшуюся вкладку сервиса
     *
     * @param serviceTitle название или часть названия сервиса,
     *                     на который нужно перейти
     * @author Юрий Юрченко
     */
    @Step("Переход в сервис \"{serviceTitle}\"")
    public void goToService(String serviceTitle) {
        searchField.click();
        WebElement serviceButton = chromeDriver.findElement(By.xpath("//ul[@class='services-suggest__list']//a[contains(., '" + serviceTitle + "')]"));
        serviceButton.click();
        Set<String> windowHandles = chromeDriver.getWindowHandles();
        String currentWindow = chromeDriver.getWindowHandle();
        for (String window :
                windowHandles) {
            if (!window.equals(currentWindow)) {
                currentWindow = window;
                chromeDriver.switchTo().window(currentWindow);
                break;
            }
        }
    }
}
