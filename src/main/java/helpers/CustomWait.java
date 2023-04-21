package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class CustomWait {

    /**
     * Находит все элементы на странице используя предоставленный механизм. По сути является методом-оберткой
     * над <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#findElements(org.openqa.selenium.By)">WebDriver.findElements()</a>.
     * Предполагается использование в целях сокращения времени ожидания для
     * элементов страницы, чье отсутствие является нормой или предсказуемо. <br>
     * Устанавливает малое временное значение неявного ожидания {@code tempDuration}, ищет элементы, возвращает
     * постоянное большее значение неявного ожидания {@code permDuration} перед выходом
     *
     * @param tempDuration  временно устанавливаемое малое значение неявного ожидания
     * @param permDuration  постоянное значение неявного ожидания устанавливаемое перед выходом из метода
     * @param driver        веб драйвер
     * @param searchContext веб драйвер, если {@code by} указан относительно страницы,
     *                      веб элемент - если поиск следует осуществлять относительно веб элемента
     * @param by            механизм локатора элементов
     * @return список найденных элементов либо пустой список, если элементы не найдены
     * @author Юрий Юрченко
     * @see <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#findElements(org.openqa.selenium.By)">WebDriver.findElements()</a>
     */
    public static List<WebElement> findElementsCustomizable(
            int tempDuration, int permDuration, WebDriver driver, SearchContext searchContext, By by) {
        driver.manage().timeouts().implicitlyWait(tempDuration, TimeUnit.SECONDS);
        List<WebElement> results = searchContext.findElements(by);
        driver.manage().timeouts().implicitlyWait(permDuration, TimeUnit.SECONDS);
        return results;
    }

    /**
     * Находит все элементы на странице используя предоставленный механизм. По сути является методом-оберткой над
     * <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#findElements(org.openqa.selenium.By)">WebDriver.findElements()</a>
     * . Предполагается использование в целях сокращения времени ожидания для
     * элементов страницы, чье отсутствие является нормой или предсказуемо.
     * Устанавливает малое временное значение неявного ожидания {@code tempDuration}, ищет элементы, возвращает
     * постоянное большее значение неявного ожидания {@code permDuration}
     *
     * @param tempDuration временно устанавливаемое малое значение неявного ожидания
     * @param permDuration постоянное значение неявного ожидания устанавливаемое перед выходом из метода
     * @param driver       веб драйвер
     * @param by           механизм локатора элементов
     * @return список найденных элементов либо пустой список, если элементы не найдены
     * @author Юрий Юрченко
     * @see <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#findElements(org.openqa.selenium.By)">WebDriver.findElements()</a>
     */
    public static List<WebElement> findElementsCustomizable(int tempDuration, int permDuration, WebDriver driver, By by) {
        return findElementsCustomizable(tempDuration, permDuration, driver, driver, by);
    }

}
