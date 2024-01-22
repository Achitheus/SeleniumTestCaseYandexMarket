package pages.ru.yandex.market;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Класс для работы с развернутым каталогом на яндекс маркете
 */
public class ExpandedCatalog {
    /**
     * вебдрайвер для обращения к браузеру
     */
    WebDriver chromeDriver;
    /**
     * Явные ожидания вебдрайвера
     */
    WebDriverWait wait;

    /**
     * Создает объект для взаимодействия с раскрытым окном каталога Маркета.
     * Устанавливает явное ожидание {@code wait} с длительностью 5 секунд
     *
     * @param driver вебдрайвер для обращения к браузеру
     * @author Юрий Юрченко
     */
    public ExpandedCatalog(WebDriver driver) {
        chromeDriver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /**
     * Наводит курсор на раздел с названием {@code sectionTitle}, кликает по категории в разделе
     * с названием {@code categoryTitle}
     *
     * @param sectionTitle раздел, на который наводится курсор
     * @param categoryTitle категория раздела, в которую осуществляется переход
     * @author Юрий Юрченко
     */
    public void toCategoryProductsPage(String sectionTitle, String categoryTitle) {
        WebElement section = chromeDriver.findElement(By.xpath("//*[@data-zone-name='catalog-content']//*[@role='tablist']//li[.//span[text()='"+sectionTitle+"']]"));
        Actions actions = new Actions(chromeDriver);
        actions.moveToElement(section).perform();
        wait.until(ExpectedConditions.attributeToBe(section, "aria-selected", "true"));
        chromeDriver.findElement(By.xpath("//*[@role='tabpanel']//a[text()='" + categoryTitle + "']")).click();
    }

}
