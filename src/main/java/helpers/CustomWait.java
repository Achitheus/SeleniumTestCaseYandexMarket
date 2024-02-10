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
     * То же, что и {@link WebDriver#findElement(By)}, но с возможностью изменить значение неявного ожидания
     * на время действия метода и с той разницей, что данный метод не выбрасывает исключение, если элемент не был найден.
     *
     * @param by       механизм поиска элемента.
     * @param driver   веб-драйвер.
     * @param tempWait неявное ожидание, устанавливаемое на время действия метода.
     * @param permWait неявное ожидание, устанавливаемое перед завершением метода.
     * @return искомый веб-элемент.
     * @author Achitheus (Yury Yurchenko)
     */
    public static Optional<WebElement> findElementSoftly(By by, WebDriver driver, Duration tempWait, Duration permWait) {
        return findElementSoftly(driver, by, driver, tempWait, permWait);
    }

    /**
     * То же, что и {@link SearchContext#findElement(By)}, но с возможностью изменить значение неявного ожидания
     * на время действия метода и с той разницей, что данный метод не выбрасывает исключение, если элемент не был найден.
     *
     * @param searchContext контекст поиска веб-элемента.
     * @param by            механизм поиска элемента.
     * @param driver        веб-драйвер.
     * @param tempWait      неявное ожидание, устанавливаемое на время действия метода.
     * @param permWait      неявное ожидание, устанавливаемое перед завершением метода.
     * @return искомый веб-элемент.
     * @author Achitheus (Yury Yurchenko)
     */
    public static Optional<WebElement> findElementSoftly(SearchContext searchContext, By by, WebDriver driver, Duration tempWait, Duration permWait) {
        driver.manage().timeouts().implicitlyWait(tempWait);
        List<WebElement> results = searchContext.findElements(by);
        driver.manage().timeouts().implicitlyWait(permWait);

        return Optional.ofNullable(results.isEmpty() ? null : results.get(0));
    }
}
