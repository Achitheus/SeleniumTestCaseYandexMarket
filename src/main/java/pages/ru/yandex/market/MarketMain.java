package pages.ru.yandex.market;

import org.openqa.selenium.WebDriver;

/**
 * Класс взаимодействия с главной страницей яндекс маркета.
 */
public class MarketMain extends MarketHeader {
    /**
     * Создает объект для взаимодействия с главной страницей яндекс маркета.
     *
     * @param driver веб-драйвер для взаимодействия с браузером.
     */
    public MarketMain(WebDriver driver) {
        super(driver);
    }
}
