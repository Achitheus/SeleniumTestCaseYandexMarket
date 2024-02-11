package pages.ru.yandex.market;

import helpers.NamedRange;
import helpers.pageable.Pageable;
import helpers.pageable.PageableChecker;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf;

/**
 * Класс дря работы со страницей товаров категории Маркета.
 * В классе производится поиск элементов, чье отсутствие - норма, поэтому временно
 * изменяется значение неявного ожидания на меньшее значение.
 * {@code IMPLICITLY_WAIT} нужно для возвращения исходного значения
 * неявного ожидания.
 *
 * @author Achitheus (Yury Yurchenko)
 */
public class CategoryGoods extends MarketHeader implements Pageable {
    public static final Logger logger = LoggerFactory.getLogger(CategoryGoods.class);

    /**
     * Константа, хранящая значение неявного ожидания (в секундах), используемое в тесте.
     * Нужна для возвращения исходного значения неявного ожидания, поскольку
     * оно меняется в методах проверки отсутствия элементов.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    private final int IMPLICITLY_WAIT;
    /**
     * Селектор товаров.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    protected final String selectorProducts = "//main[@id='searchResults']//*[@data-autotest-id='product-snippet']";
    /**
     * Селектор наименований товаров.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    protected final String selectorProductNames = selectorProducts + "//*[@data-auto='snippet-title-header']";
    /**
     * Селектор цен товаров.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    protected final String selectorProductPrices = selectorProducts + "//*[@data-auto='price-value' or @data-auto='snippet-price-current']";

    /**
     * Создает объект для взаимодействия со страницей категории товаров. Запоминает
     * переданное значение {@code implicitlyWait} неявного ожидания теста
     * для его восстановления (после поиска элементов, чье отсутствие - норма).
     *
     * @param driver         веб-драйвер для обращения к браузеру.
     * @param implicitlyWait неявное ожидание, установленное для теста.
     * @author Achitheus (Yury Yurchenko)
     */
    public CategoryGoods(WebDriver driver, int implicitlyWait) {
        super(driver);
        IMPLICITLY_WAIT = implicitlyWait;
    }

    /**
     * Устанавливает все переданные диапазон-фильтры товаров.
     *
     * @param filterList диапазон-фильтры товаров.
     * @author Achitheus (Yury Yurchenko)
     */
    public void setRangeFilters(List<NamedRange> filterList) {
        filterList.forEach(this::setRangeFilterWithoutWait);
        waitUntilGoodsLoaded();
    }

    /**
     * Устанавливает диапазон-фильтр товаров.
     *
     * @param namedRange диапазон-фильтр товаров.
     * @author Achitheus (Yury Yurchenko)
     */
    public void setRangeFilter(NamedRange namedRange) {
        setRangeFilterWithoutWait(namedRange);
        waitUntilGoodsLoaded();
    }

    /**
     * @author Achitheus (Yury Yurchenko)
     */
    @Step("Установка диапазон фильтра {namedRange}")
    private void setRangeFilterWithoutWait(NamedRange namedRange) {
        WebElement filter = getFilterByTextInTitle(namedRange.NAME);

        WebElement minField = filter.findElement(By.xpath(".//input[contains(@id, 'min')]"));
        minField.click();
        minField.clear();
        minField.sendKeys(namedRange.MIN);

        WebElement maxField = filter.findElement(By.xpath(".//input[contains(@id, 'max')]"));
        maxField.click();
        maxField.clear();
        maxField.sendKeys(namedRange.MAX);
    }

    /**
     * Предоставляет список веб элементов, представляющих собой названия
     * всех товаров, представленных на странице. Предварительно скроллит вниз
     * для получения полного списка элементов.
     *
     * @return Список веб элементов (всех названий товаров).
     * @author Achitheus (Yury Yurchenko)
     */
    public List<WebElement> getClickableProductNames() {
        scrollToBottom();
        return driver.findElements(By.xpath(selectorProductNames));
    }

    /**
     * Скроллит страницу вниз до одного из элементов, расположенных ниже товаров.
     * Метод нужен для доступа ко всем товарам страницы, поскольку товары добавляются
     * в DOM по мере скролла вниз (сверху товары при этом из DOM не исчезают).
     *
     * @author Achitheus (Yury Yurchenko)
     */
    protected void scrollToBottom() {
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.xpath("//*[@data-grabber='SearchLegalInfo']")))
                .perform();
    }

    /**
     * Переходит на указанную страницу товаров используя url.
     *
     * @param pageNumber номер страницы, на которую нужно перейти.
     * @author Achitheus (Yury Yurchenko)
     */
    @Step("Переход на стр. {pageNumber}")
    public void toPage(int pageNumber) {
        String currentUrl = driver.getCurrentUrl();
        String targetPageUrl;
        if (currentUrl.contains("page=")) {
            targetPageUrl = currentUrl.replaceFirst("page=\\d+", "page=" + pageNumber);
        } else {
            targetPageUrl = currentUrl + "&page=" + pageNumber;
        }
        driver.get(targetPageUrl);
    }

    /**
     * Переходит на предыдущую по счету страницу товаров, если найдена
     * соответствующая кнопка навигации "previous page".
     *
     * @return {@code true} в случае успеха, иначе - {@code false}.
     * @author Achitheus (Yury Yurchenko)
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
     * Переходит на следующую по счету страницу товаров, если
     * найдена соответствующая кнопка навигации "next page".
     *
     * @return {@code true} в случае успеха, иначе - {@code false}.
     * @author Achitheus (Yury Yurchenko)
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
     * То же, что и {@link #setEnumFilter(String, List)}, но для нескольких фильтров-перечислений.
     *
     * @param enumFilters фильтры перечислений в формате: <br> ключ -
     *                    название фильтра, <br> значение - список названий чекбоксов.
     * @author Achitheus (Yury Yurchenko)
     */
    public void setEnumFilters(Map<String, List<String>> enumFilters) {
        for (Map.Entry<String, List<String>> enumFilter : enumFilters.entrySet()) {
            setEnumFilterWithoutWait(enumFilter.getKey(), enumFilter.getValue(), OptionProcessType.MARK);
        }
        waitUntilGoodsLoaded();
    }

    /**
     * Устанавливает переданный фильтр перечислений, отмечает, либо
     * снимает отметки с указанных чекбоксов в зависимости от указанного
     * режима {@code processType}. Учитывает состояние чекбокса, т.е. если
     * нужно отметить чекбокс, а галочка уже стоит, метод ее не снимает.
     *
     * @param titleSubstring текст, содержащийся в названии фильтра (регистро-зависимый).
     * @param processType    режим обработки чекбоксов.
     * @param targets        названия чекбоксов (регистро-независимые), которые следует отметить или,
     *                       наоборот, снять отметки.
     * @author Achitheus (Yury Yurchenko)
     */
    public void setEnumFilter(String titleSubstring, OptionProcessType processType, List<String> targets) {
        setEnumFilterWithoutWait(titleSubstring, targets, processType);
        waitUntilGoodsLoaded();
    }

    /**
     * Отмечает чекбоксы указанного фильтра-перечисления {@code titleSubstring}, если они еще не отмечены.
     *
     * @param titleSubstring текст, содержащийся в названии фильтра (регистро-чувствительный).
     * @param options регистро-независимые названия чекбоксов, которые следует отметить.
     * @author Achitheus (Yury Yurchenko)
     */
    public void setEnumFilter(String titleSubstring, List<String> options) {
        setEnumFilterWithoutWait(titleSubstring, options, OptionProcessType.MARK);
        waitUntilGoodsLoaded();
    }

    /**
     *
     * @param titleSubstring часть названия. Регистро-зависимая.
     * @param options полные названия, регистро-независимые.
     * @author Achitheus (Yury Yurchenko)
     */
    @Step("Установка фильтра перечислений \"{titleSubstring}\" значениями: {options}")
    private void setEnumFilterWithoutWait(String titleSubstring, List<String> options, OptionProcessType processType) {
        WebElement filter = getFilterByTextInTitle(titleSubstring);
        Set<String> mutableTargetSet = new HashSet<>(options);
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
     * перечислений вне зависимости от состояния списка чекбоксов (развернут/свернут).
     * Не воздействует на чекбокс, если он уже находится в нужном состоянии.
     * Если список реализован с асинхронным скроллом, обрабатывает с помощью поля поиска.
     *
     * @param filter           фильтр, который следует обработать
     * @param mutableTargetSet {@code mutable} множество полных регистро-независимых названий чекбоксов,
     *                         которые нужно обработать.
     * @param processType      режим обработки чекбоксов.
     * @author Achitheus (Yury Yurchenko)
     */
    private void processAvailableCheckBoxes(WebElement filter, Set<String> mutableTargetSet, OptionProcessType processType) {
        if (soCalledDataVirtuosoScrollerIsDetected(filter)) {
            processEnumFilterWithSearchField(filter, mutableTargetSet, processType);
            return;
        }
        List<WebElement> optionList = filter.findElements(By.xpath(".//*[@data-zone-name = 'FilterValue']//label"));
        for (WebElement option : optionList) {
            String optionTitle = option.getText();
            Optional<String> target = mutableTargetSet.stream().filter(optionTitle::equalsIgnoreCase).findFirst();
            if (target.isPresent() && checkBoxShouldBeToggled(option, processType)) {
                option.click();
            }
            target.ifPresent(mutableTargetSet::remove);
            if (mutableTargetSet.isEmpty()) {
                break;
            }
        }
    }

    /**
     * Проверяет, требуется ли изменить состояние чекбокса.
     *
     * @param option      проверяемый чекбокс.
     * @param processType режим обработки чекбокса.
     * @return {@code false}, если чекбокс уже находится в нужном состоянии, иначе - {@code true}.
     * @author Achitheus (Yury Yurchenko)
     */
    private boolean checkBoxShouldBeToggled(WebElement option, OptionProcessType processType) {
        if (processType.equals(OptionProcessType.UNMARK))
            return checkBoxIsMarked(option);
        else
            return !checkBoxIsMarked(option);
    }

    /**
     * Обрабатывает (отмечая, либо снимая отметки) чекбоксы фильтра
     * перечислений с помощью поля поиска.
     *
     * @param filter           фильтр с обрабатываемыми чекбоксами.
     * @param mutableTargetSet {@code mutable} множество полных регистро-независимых названий чекбоксов,
     *                         которые нужно обработать.
     * @param processType      режим обработки чекбоксов.
     * @author Achitheus (Yury Yurchenko)
     */
    private void processEnumFilterWithSearchField(WebElement filter, Set<String> mutableTargetSet, OptionProcessType processType) {
        WebElement filterSearchField = filter.findElement(By.xpath(".//input[@type='text']"));
        for (Iterator<String> iterator = mutableTargetSet.iterator(); iterator.hasNext(); ) {
            String targetName = iterator.next();
            filterSearchField.click();
            filterSearchField.clear();
            filterSearchField.sendKeys(targetName);
            WebElement foundCheckbox = (WebElement) wait.until((driver) -> {
                WebElement currentCheckbox = filter.findElement(By.xpath(".//*[@data-zone-name = 'FilterValue']//label[1]"));
                if (currentCheckbox.getText().equalsIgnoreCase(targetName)) {
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
     * <i>Мягко</i> ожидает появления спиннера загрузки товаров и, в случае его появления,
     * дожидается его исчезновения.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    protected void waitUntilGoodsLoaded() {
        Optional<WebElement> spinner = findElementSoftly(By.xpath("//*[@data-grabber='SearchSerp']//*[@data-auto='spinner']"),
                driver, Duration.ofSeconds(1), Duration.ofSeconds(IMPLICITLY_WAIT));
        spinner.ifPresent(webElement -> wait.until(invisibilityOf(webElement)));
    }

    /**
     * Проверяет, отмечен ли данный чекбокс.
     *
     * @param option чекбокс, который следует проверить.
     * @return {@code true}, если чекбокс отмечен, иначе - {@code false}.
     * @author Achitheus (Yury Yurchenko)
     */
    private boolean checkBoxIsMarked(WebElement option) {
        return Boolean.parseBoolean(option.getAttribute("aria-checked"));
    }

    /**
     * Проверяет фильтр на наличие асинхронного скролла.
     *
     * @param filter фильтр, который следует проверить.
     * @return {@code true} если асинхронный скролл обнаружен, иначе - {@code false}.
     * @author Achitheus (Yury Yurchenko)
     */
    private boolean soCalledDataVirtuosoScrollerIsDetected(WebElement filter) {
        return findElementSoftly(filter, By.xpath(".//*[@data-virtuoso-scroller='true']"),
                driver, Duration.ofSeconds(2), Duration.ofSeconds(IMPLICITLY_WAIT))
                .isPresent();
    }

    /**
     * Разворачивает, если он еще не развернут, список фильтра нажатием кнопки "показать всё", если такая кнопка существует.
     *
     * @param filter фильтр, который следует развернуть.
     * @return {@code true}, если список удалось развернуть, иначе - {@code false}.
     * @author Achitheus (Yury Yurchenko)
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
     * Находит фильтр по тексту в названии (регистро-чувствительно).
     *
     * @param titleSubstring регистро-зависимый текст, содержащийся в названии фильтра.
     * @return фильтр.
     * @author Achitheus (Yury Yurchenko)
     */
    private WebElement getFilterByTextInTitle(String titleSubstring) {
       return driver.findElement(By.xpath("//*[@id='searchFilters']//fieldset[ .//legend[contains(., '" + titleSubstring + "')]]"));
    }

    /**
     * Возвращает список наименований всех представленных на странице товаров,
     * предварительно проскроллив страницу вниз для получения полного списка товаров.
     *
     * @return список наименований всех товаров на странице.
     * @author Achitheus (Yury Yurchenko)
     */
    public List<String> getProductNames() {
        scrollToBottom();
        return driver.findElements(By.xpath(selectorProductNames))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список цен всех представленных на странице товаров,
     * предварительно проскроллив страницу вниз для получения полного
     * списка цен.
     *
     * @return список цен всех товаров на странице.
     * @author Achitheus (Yury Yurchenko)
     */
    public List<Double> getProductPrices() {
        scrollToBottom();
        return driver.findElements(By.xpath(selectorProductPrices))
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
     * {@code UNMARK} следует указывать, если с чекбоксов отметки нужно снять.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    public enum OptionProcessType {
        /**
         * Экземпляр перечисления, означающий, что чекбоксы нужно отметить.
         */
        MARK,
        /**
         * Экземпляр перечисления, означающий, что с чекбоксов отметки нужно снять.
         */
        UNMARK
    }
}
