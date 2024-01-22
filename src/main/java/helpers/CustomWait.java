package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Optional;


public class CustomWait {

    /**
     * Находит все элементы на странице используя предоставленный механизм. По сути является методом-оберткой
     * над <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#findElements(org.openqa.selenium.By)">WebDriver.findElements()</a>.
     * Предполагается использование в целях сокращения времени ожидания для
     * элементов страницы, чье отсутствие является нормой или предсказуемо. <br>
     * Устанавливает малое временное значение неявного ожидания {@code tempDuration}, ищет элементы, возвращает
     * постоянное большее значение неявного ожидания {@code permDuration} перед завершением.
     *
     * @param searchContext веб драйвер, если {@code by} указан относительно страницы,
     *                      веб элемент - если поиск следует осуществлять относительно веб элемента
     * @param by            механизм локатора элементов
     * @param tempDuration  временно устанавливаемое малое значение неявного ожидания в секундах
     * @param permDuration  постоянное значение неявного ожидания устанавливаемое перед выходом из метода в секундах
     * @param driver        веб драйвер
     * @return список найденных элементов либо пустой список, если элементы не найдены
     * @author Юрий Юрченко
     * @see <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#findElements(org.openqa.selenium.By)">WebDriver.findElements()</a>
     */
    public static List<WebElement> findElementsCustomWait(SearchContext searchContext, By by, int tempDuration, int permDuration, WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(tempDuration));
        List<WebElement> results = searchContext.findElements(by);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(permDuration));
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
     * @param tempDuration временно устанавливаемое малое значение неявного ожидания в секундах.
     * @param permDuration постоянное значение неявного ожидания в секундах, устанавливаемое перед выходом из метода.
     * @param driver       веб драйвер
     * @param by           механизм локатора элементов
     * @return список найденных элементов либо пустой список, если элементы не найдены
     * @author Юрий Юрченко
     * @see <a href="https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#findElements(org.openqa.selenium.By)">WebDriver.findElements()</a>
     */
    public static List<WebElement> findElementsCustomWait(int tempDuration, int permDuration, WebDriver driver, By by) {
        return findElementsCustomWait(driver, by, tempDuration, permDuration, driver);
    }

    public static Optional<WebElement> findElementSoftly(By by, WebDriver driver, Duration tempWait, Duration permWait) {
        return findElementSoftly(driver, by, driver, tempWait, permWait);
    }

    public static Optional<WebElement> findElementSoftly(SearchContext searchContext, By by, WebDriver driver, Duration tempWait, Duration permWait) {
        driver.manage().timeouts().implicitlyWait(tempWait);
        List<WebElement> results = searchContext.findElements(by);
        driver.manage().timeouts().implicitlyWait(permWait);

        if(results.size() > 1) {
            throw new RuntimeException("More than one element were found");
        }
        return Optional.ofNullable(results.isEmpty() ? null : results.get(0));
    }
}
