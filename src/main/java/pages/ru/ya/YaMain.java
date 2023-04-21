package pages.ru.ya;

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
    private WebDriver chromeDriver;
    /**
     * кнопка "открыть окно сервисов"
     */
    private WebElement servicesButton;
    /**
     * Кнопка "Маркета" в окне сервисов
     */
    private WebElement marketButton;

    /**
     * Создает объект страницы с инициализированными полями {@code serviceButton}, {@code marketButton}
     *
     * @param chromeDriver вебдрайвер для обращения к браузеру
     */
    public YaMain(WebDriver chromeDriver) {
        this.chromeDriver = chromeDriver;
        servicesButton = chromeDriver.findElement(By.xpath("//*[@title='Все сервисы']"));
        marketButton = chromeDriver.findElement(By.xpath("//*[text()='Маркет']/parent::*"));
    }

    /**
     * Открывает окно сервисов и переходит в первый, содержащий в названии {@code partialTitle}.
     * Переключается на открывшуюся вкладку сервиса
     *
     * @param partialTitle название или часть названия сервиса,
     *                     на который нужно перейти
     * @author Юрий Юрченко
     */
    public void goToService(String partialTitle) {
        servicesButton.click();
        chromeDriver.findElement(By.xpath("//*[contains(text(),'" + partialTitle + "')]/parent::*")).click();
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
