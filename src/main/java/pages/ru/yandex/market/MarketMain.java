package pages.ru.yandex.market;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MarketMain {
    /**
     * вебдрайвер для обращения к браузеру
     */
    private final WebDriver chromeDriver;
    /**
     * Кнопка раскрытия/закрытия окна каталога
     */
    private WebElement catalogButton;

    /**
     * Создает объект для взаимодействия с главной страницей Маркета.
     * Инициализирует поле {@code catalogButton}, ожидает появления товаров.
     *
     * @param chromeDriver вебдрайвер для обращения к браузеру
     * @author Юрий Юрченко
     */
    public MarketMain(WebDriver chromeDriver) {
        this.chromeDriver = chromeDriver;
        catalogButton = chromeDriver.findElement(By.xpath("//*[text()='Каталог']"));
    }

    /**
     * Раскрывает окно каталога обходя баг, при котором элементы каталога
     * не отображаются при любых значениях ожидания.<br>
     * Достигается следующей последовательностью действий:<br>
     * 1) открываем каталог <br>
     * 2) перезагружаем страницу <br>
     * 3) снова открываем каталог <br>
     * Если в этом нет необходимости, следует использовать {@link MarketMain#openCatalog()}
     *
     * @see MarketMain#openCatalog()
     * @author Юрий Юрченко
     */
    public void openCatalogAvoidingBug() {
        catalogButton.click();
        chromeDriver.navigate().refresh();
        catalogButton = chromeDriver.findElement(By.xpath("//*[text()='Каталог']"));
        catalogButton.click();
    }

    /**
     * Раскрывает окно каталога. <br>
     * Примечание: при проявлении бага, при котором элементы каталога не отображаются при любых значениях ожидания,
     * данный метод будет работать, если запускать селениум из-под непустого профиля хрома (с некоторым количеством
     * набранного кэша и cookies на сайте Маркета).
     * При стандартной работе селениума (каждый запуск - с чистой сессии) при проявлении бага вместо данного
     * метода рекомендуется использовать {@link MarketMain#openCatalogAvoidingBug()}
     *
     * @see MarketMain#openCatalogAvoidingBug()
     * @author Юрий Юрченко
     */
    public void openCatalog() {
        catalogButton.click();
    }

}
