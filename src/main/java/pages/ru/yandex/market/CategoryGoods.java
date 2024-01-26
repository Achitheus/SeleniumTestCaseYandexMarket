package pages.ru.yandex.market;

import helpers.pageable.Pageable;
import helpers.pageable.PageableChecker;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static helpers.CustomWait.findElementSoftly;
import static helpers.CustomWait.findElementsCustomWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf;

/**
 * Класс дря работы со страницей товаров категории Маркета.
 * В классе используются проверки на отсутствие, поэтому временно
 * изменяется значение неявного ожидания на меньшее значение.
 * {@code IMPLICITLY_WAIT} нужно для возвращения исходного значения
 * неявного ожидания.
 *
 * @author Юрий Юрченко
 */
public class CategoryGoods extends MarketHeader implements Pageable {
    public static final Logger logger = LoggerFactory.getLogger(CategoryGoods.class);
    /**
     * Явные ожидания вебдрайвера
     *
     * @author Юрий Юрченко
     */
    private final WebDriverWait wait;
    /**
     * Константа, хранящая значение неявного ожидания (в секундах), используемое в тесте.
     * Нужна для возвращения исходного значения неявного ожидания, поскольку
     * оно меняется в методах использующих проверки отсутствия элементов.
     *
     * @author Юрий Юрченко
     */
    private final int IMPLICITLY_WAIT;
    /**
     * Селектор товаров
     *
     * @author Юрий Юрченко
     */
    protected final String selectorProducts = "//main[@id='searchResults']//*[@data-autotest-id='product-snippet']";
    /**
     * Селектор наименований товаров
     *
     * @author Юрий Юрченко
     */
    protected final String selectorProductNames = selectorProducts + "//*[@data-auto='snippet-title-header']";
    /**
     * Селектор цен товаров
     *
     * @author Юрий Юрченко
     */
    protected final String selectorProductPrices = selectorProducts + "//*[@data-auto='price-value' or @data-auto='snippet-price-current']";

    /**
     * Создает объект для взаимодействия со страницей товаров категории.
     * Устанавливает явные ожидания длительностью десять секунд. Запоминает
     * переданное значение {@code implicitlyWait} неявного ожидания теста
     * для его восстановления после использования ожиданий отсутствия
     *
     * @param driver         вебдрайвер для обращения к браузеру
     * @param implicitlyWait неявное ожидание, установленное для теста
     * @author Юрий Юрченко
     */
    public CategoryGoods(WebDriver driver, int implicitlyWait) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        IMPLICITLY_WAIT = implicitlyWait;
    }

    /**
     * Устанавливает все переданные диапазон-фильтры.
     *
     * @param rangeFilters диапазон фильтры в формате: <br> ключ - название, <br>
     *                     значение - список, содержащий пару {min, max}
     * @author Юрий Юрченко
     */
    public void setRangeFilters(Map<String, List<String>> rangeFilters) {
        for (Map.Entry<String, List<String>> rangeFilter : rangeFilters.entrySet()) {
            setRangeFilterWithoutWait(rangeFilter.getKey()
                    , rangeFilter.getValue().get(0), rangeFilter.getValue().get(1));
        }
        waitUntilGoodsLoaded();
    }

    /**
     * Устанавливает переданный диапазон-фильтр.
     *
     * @param textInTitle название фильтра
     * @param min         значение "от"
     * @param max         значение "до"
     * @author Юрий Юрченко
     */
    public void setRangeFilter(String textInTitle, String min, String max) {
        setRangeFilterWithoutWait(textInTitle, min, max);
        waitUntilGoodsLoaded();
    }

    @Step("Установка диапазон фильтра: {textInTitle} значениями от: {min} до: {max}")
    private void setRangeFilterWithoutWait(String textInTitle, String min, String max) {
        WebElement filter = getFilterByTextInTitle(textInTitle);

        WebElement minField = filter.findElement(By.xpath(".//input[contains(@id, 'min')]"));
        minField.click();
        minField.clear();
        minField.sendKeys(min);

        WebElement maxField = filter.findElement(By.xpath(".//input[contains(@id, 'max')]"));
        maxField.click();
        maxField.clear();
        maxField.sendKeys(max);
    }

    /**
     * Предоставляет список веб элементов, представляющих собой названия
     * всех товаров, представленных на странице.
     *
     * @return Список веб элементов (названий товаров), либо пустой список,
     * если элементы не были найдены
     * @author Юрий Юрченко
     */
    public List<WebElement> getClickableProductNames() {
        scrollToBottom();
        return driver.findElements(By.xpath(selectorProductNames));
    }

    /**
     * Скроллит страницу вниз до одного из элементов, расположенных внизу страницы.
     * Сохраняет значение в поле {@code pageIsScrolledToBottom}: если страница
     * уже была проскроллена вниз, ничего не делает.
     *
     * @author Юрий Юрченко
     */
    protected void scrollToBottom() {
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.xpath("//*[@data-grabber='SearchLegalInfo']")))
                .perform();
    }

    /**
     * Переходит на указанную страницу товаров используя url.
     *
     * @param toPage номер страницы, на которую нужно перейти
     * @author Юрий Юрченко
     */
    public void toPage(int toPage) {
        String currentUrl = driver.getCurrentUrl();
        String newURL;
        if (currentUrl.contains("page=")) {
            newURL = currentUrl.replaceFirst("page=\\d+", "page=" + toPage);
        } else {
            newURL = currentUrl + "&page=" + toPage;
        }
        driver.get(newURL);
    }

    /**
     * Переходит на предыдущую по счету страницу товаров, если
     * на странице найдена соответствующая кнопка навигации "previous"
     *
     * @return {@code true}, если переход осуществлен, либо {@code false}, если
     * кнопка перехода не была найдена
     * @author Юрий Юрченко
     */
    public boolean previousPage() {
        Optional<WebElement> prevButton = findElementSoftly(By.xpath("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@class='cia-cs' and @data-baobab-name='prev']"),
                driver, Duration.ofSeconds(2), Duration.ofSeconds(IMPLICITLY_WAIT));
        prevButton.ifPresent(button -> {
            prevButton.get().click();
            waitUntilGoodsLoaded();
        });
        return prevButton.isPresent();
    }

    /**
     * Переходит на следующую по счету страницу товаров, если на странице
     * найдена соответствующая кнопка навигации "next"
     *
     * @return {@code true}, если переход осуществлен, либо {@code false}, если
     * кнопка перехода не была найдена
     * @author Юрий Юрченко
     */
    public boolean nextPage() {
        Optional<WebElement> nextButton = findElementSoftly(By.xpath("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@data-baobab-name='next']//span"),
                driver, Duration.ofSeconds(2), Duration.ofSeconds(IMPLICITLY_WAIT));
        nextButton.ifPresent(button -> {
            nextButton.get().click();
            waitUntilGoodsLoaded();
        });
        return nextButton.isPresent();
    }

    /**
     * Устанавливает все переданные фильтры перечислений.
     *
     * @param enumFilters фильтры перечислений в формате: <br> ключ -
     *                    название фильтра, <br> значение - список,
     *                    содержащий названия чекбоксов
     * @author Юрий Юрченко
     */
    public void setEnumFilters(Map<String, List<String>> enumFilters) {
        for (Map.Entry<String, List<String>> enumFilter : enumFilters.entrySet()) {
            setEnumFilterWithoutWait(enumFilter.getKey(), CheckBoxProcessType.MARK, enumFilter.getValue());
        }
        waitUntilGoodsLoaded();
    }

    /**
     * Устанавливает переданный фильтр перечислений, отмечает, либо
     * снимает отметки с указанных чекбоксов в зависимости от указанного
     * режима {@code processType}. Учитывает состояние чекбокса, т.е. если
     * нужно отметить чекбокс, а галочка уже стоит, метод ее не снимает.
     *
     * @param textInFilterTitle текст, содержащийся в названии фильтра, для поиска
     * @param processType       режим работы метода: MARK отмечает чекбоксы, UNMARK
     *                          снимает с чекбоксов отметки
     * @param targets           названия чекбоксов, которые следует отметить или,
     *                          наоборот, снять отметки
     * @author Юрий Юрченко
     */
    public void setEnumFilter(String textInFilterTitle, CheckBoxProcessType processType, List<String> targets) {
        setEnumFilterWithoutWait(textInFilterTitle, processType, targets);
        waitUntilGoodsLoaded();
    }

    @Step("Установка фильтра перечислений: {textInFilterTitle} значениями: {targets}")
    private void setEnumFilterWithoutWait(String textInFilterTitle, CheckBoxProcessType processType, List<String> targets) {
        WebElement filter = getFilterByTextInTitle(textInFilterTitle);
        Set<String> mutableTargetSet = new HashSet<>(targets);
        processAvailableCheckBoxes(filter, mutableTargetSet, processType);
        if (mutableTargetSet.isEmpty()) {
            return;
        }
        if(expand(filter)) {
            processAvailableCheckBoxes(filter, mutableTargetSet, processType);
        }

        if (!mutableTargetSet.isEmpty()) throw new InvalidArgumentException("There are not found options: " + mutableTargetSet);
    }

    public PageableChecker<CategoryGoods> schedulePageableCheck() {
        return new PageableChecker<>(this, driver);
    }

    /**
     * Обрабатывает (отмечает, либо снимает отметки) доступные чекбоксы фильтра
     * перечислений вне зависимости от состояния развернутости списка чекбоксов.
     * Если список реализован с асинхронным скроллом, обрабатывает с помощью поля
     * поиска.
     *
     * @param filter      фильтр, который следует обработать
     * @param targetSet   множество названий чекбоксов, которые нужно отметить
     *                    или снять отметки
     * @param processType режим работы метода: MARK отмечает чекбоксы, UNMARK
     *                    снимает с чекбоксов отметки
     * @return {@code true}, если указанные чекбоксы успешно обработаны, иначе {@code false}
     * @author Юрий Юрченко
     */
    private void processAvailableCheckBoxes(WebElement filter, Set<String> targetSet, CheckBoxProcessType processType) {
        if (soCalledVirtuosoDataScrollerIsDetected(filter)) {
            processEnumFilterWithSearchField(filter, targetSet, processType);
            return;
        }
        List<WebElement> optionList = filter.findElements(By.xpath(".//*[@data-zone-name = 'FilterValue']"));
        for (WebElement option : optionList) {
            String optionTitle = option.getText();
            Optional<String> target = targetSet.stream().filter(optionTitle::equalsIgnoreCase).findFirst();
            if (target.isPresent() && checkBoxShouldBeToggled(option, processType)) {
                option.click();
            }
            target.ifPresent(targetSet::remove);
            if (targetSet.isEmpty()) {
                break;
            }
        }
    }

    /**
     * Проверяет, требуется ли изменить состояние чекбокса.
     *
     * @param option      проверяемый чекбокс
     * @param processType режим работы вызвавшего метода:
     *                    {@code MARK} отмечает чекбоксы, {@code UNMARK} снимает
     *                    с чекбоксов отметки
     * @return {@code false}, если чекбокс уже находится в желаемом состоянии,
     * {@code true} в ином случае
     * @author Юрий Юрченко
     */
    private boolean checkBoxShouldBeToggled(WebElement option, CheckBoxProcessType processType) {
        if (processType.equals(CheckBoxProcessType.UNMARK))
            return checkBoxIsMarked(option);
        else
            return !checkBoxIsMarked(option);
    }

    /**
     * Обрабатывает (отмечая, либо снимая отметки) чекбоксы фильтра
     * перечислений с помощью поля поиска.
     *
     * @param filter      фильтр, который следует обработать
     * @param targetSet   множество названий чекбоксов, которые нужно отметить
     *                    или снять отметки
     * @param processType режим работы метода: MARK отмечает чекбоксы, UNMARK
     *                    снимает с чекбоксов отметки
     * @return {@code true}, если указанные чекбоксы успешно обработаны, иначе {@code false}
     * @author Юрий Юрченко
     */
    private void processEnumFilterWithSearchField(WebElement filter, Set<String> targetSet, CheckBoxProcessType processType) {
        WebElement filterSearchField = filter.findElement(By.xpath(".//input[@type='text']"));
        for (Iterator<String> iterator = targetSet.iterator(); iterator.hasNext(); ) {
            String targetName = iterator.next();
            filterSearchField.click();
            filterSearchField.clear();
            filterSearchField.sendKeys(targetName);
            WebElement foundCheckbox = (WebElement) wait.until((driver) -> {
                WebElement currentCheckbox = filter.findElement(By.xpath(".//*[@data-zone-name = 'FilterValue'][1]"));
                if (currentCheckbox.getText().toLowerCase().contains(targetName.toLowerCase())) {
                    return currentCheckbox;
                } else {
                    return false;
                }
            });
            if (checkBoxShouldBeToggled(foundCheckbox, processType)) {
                foundCheckbox.click();
            }
            iterator.remove();
        }
    }

    /**
     * Ожидает исчезновения текущих товаров, представленных на странице.
     * Ожидание загрузки новых товаров произойдет автоматически через
     * механизм неявного ожидания. Т.е. внешне работает как ожидание
     * загрузки новых товаров
     *
     * @author Юрий Юрченко
     */
    protected void waitUntilGoodsLoaded() {
        Optional<WebElement> spinner = findElementSoftly(By.xpath("//*[@data-grabber='SearchSerp']//*[@data-auto='spinner']"),
                driver, Duration.ofSeconds(1), Duration.ofSeconds(IMPLICITLY_WAIT));
        spinner.ifPresent(webElement -> wait.until(invisibilityOf(webElement)));
    }

    /**
     * Проверяет, отмечен ли данный чекбокс.
     *
     * @param option чекбокс, который следует проверить
     * @return {@code true}, если чекбокс отмечен галочкой, {@code false}
     * в ином случае
     * @author Юрий Юрченко
     */
    private boolean checkBoxIsMarked(WebElement option) {
        return Boolean.parseBoolean(option.findElement(By.xpath(".//input")).getAttribute("checked"));
    }

    /**
     * Проверяет фильтр на наличие асинхронных элементов (асинхронного скролла),
     *
     * @param filter фильтр, который следует проверить
     * @return {@code true} если асинхронный скролл обнаружен, иначе {@code false}
     * @author Юрий Юрченко
     */
    private boolean soCalledVirtuosoDataScrollerIsDetected(WebElement filter) {
        return findElementSoftly(filter, By.xpath(".//*[@data-virtuoso-scroller='true']"),
                driver, Duration.ofSeconds(2), Duration.ofSeconds(IMPLICITLY_WAIT))
                .isPresent();
    }

    /**
     * Разворачивает список фильтра нажатием кнопки "показать всё".
     *
     * @param filter фильтр, который следует развернуть
     * @return {@code true} если список удалось развернуть, иначе {@code false}
     * @throws InvalidSelectorException если найдено больше одной кнопки
     *                                  развертывания списка
     * @author Юрий Юрченко
     */
    private boolean expand(WebElement filter) {
        waitUntilGoodsLoaded();
        Optional<WebElement> expandButton = findElementSoftly(filter, By.tagName("button"), driver, Duration.ZERO, Duration.ofSeconds(IMPLICITLY_WAIT));
        if(expandButton.isEmpty() || Boolean.parseBoolean(expandButton.get().getAttribute("aria-expanded"))) {
            return false;
        }
        expandButton.get().click();
        return true;
    }

    /**
     * Находит фильтр по тексту в названии. Поиск регистрозависим.
     *
     * @param textInTitle текст, который должен содержаться в названии
     *                    фильтра
     * @return фильтр
     * @throws InvalidArgumentException если фильтр не найден или
     *                                  найдено более одного
     * @author Юрий Юрченко
     */
    private WebElement getFilterByTextInTitle(String textInTitle) {
       return driver.findElement(By.xpath("//*[@id='searchFilters']//fieldset[ .//legend[contains(., '" + textInTitle + "')]]"));
    }

    /**
     * Возвращает количество представленных на странице товаров,
     * предварительно проскроллив страницу вниз для получения
     * полного списка.
     *
     * @return количество товаров на странице
     * @author Юрий Юрченко
     */
    public int productListSize() {
        scrollToBottom();
        return getClickableProductNames().size();
    }

    /**
     * Возвращает список наименований всех представленных на странице товаров,
     * предварительно проскроллив страницу вниз для получения полного списка.
     *
     * @return список наименований всех товаров на странице
     * @author Юрий Юрченко
     */
    public List<String> getProductNames() {
        scrollToBottom();
        return findElementsCustomWait(By.xpath(selectorProductNames), 5, IMPLICITLY_WAIT, driver)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список цен всех представленных на странице товаров,
     * предварительно проскроллив страницу вниз для получения полного
     * списка.
     *
     * @return список цен всех товаров на странице
     * @author Юрий Юрченко
     */
    public List<Double> getProductPrices() {
        scrollToBottom();
        return findElementsCustomWait(By.xpath(selectorProductPrices), 5, IMPLICITLY_WAIT, driver)
                .stream()
                .mapToDouble(priceElement -> Double.parseDouble(
                        priceElement.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""))
                )
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Перечисление типов обработки чекбоксов. <br>
     * {@code MARK} следует указывать, если чекбоксы нужно отметить галочкой.
     * {@code UNMARK} следует указывать, если галочки с чекбоксов нужно снять.
     *
     * @author Юрий Юрченко
     */
    public enum CheckBoxProcessType {
        /**
         * Экземпляр перечисления, означающий, что галочки на чекбоксы нужно поставить, а не снять
         */
        MARK,
        /**
         * Экземпляр перечисления, означающий, что с чекбоксов галочки нужно снять, а не поставить
         */
        UNMARK
    }
}
