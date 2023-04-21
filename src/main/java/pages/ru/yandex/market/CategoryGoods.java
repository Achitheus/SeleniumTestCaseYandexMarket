package pages.ru.yandex.market;

import helpers.CustomWait;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static helpers.CustomWait.findElementsCustomizable;

/**
 * Класс дря работы со страницей товаров категории Маркета.
 * В классе используются проверки на отсутствие, поэтому временно
 * изменяется значение неявного ожидания на меньшее значение.
 * {@code IMPLICITLY_WAIT} нужно для возвращения исходного значения
 * неявного ожидания.
 *
 * @author Юрий Юрченко
 */
public class CategoryGoods {
    /**
     * вебдрайвер для обращения к браузеру
     *
     * @author Юрий Юрченко
     */
    private final WebDriver chromeDriver;
    /**
     * Явные ожидания вебдрайвера
     *
     * @author Юрий Юрченко
     */
    private final WebDriverWait wait;
    /**
     * Константа, хранящая значение неявного ожидания, используемое в тесте.
     * Нужна для возвращения исходного значения неявного ожидания, поскольку
     * оно меняется в методах использующих проверки отсутствия элементов
     *
     * @author Юрий Юрченко
     */
    private final int IMPLICITLY_WAIT;
    /**
     * Селектор кнопки поиска
     *
     * @author Юрий Юрченко
     */
    private final String selectorSearchButton = "//header//button[@type='submit']";
    /**
     * Селектор поля поиска
     *
     * @author Юрий Юрченко
     */
    private final String selectorSearchField = "//header//input[@type='text' and @id='header-search']";
    /**
     * Селектор товаров
     *
     * @author Юрий Юрченко
     */
    private final String selectorProducts = "//main[@id='searchResults']//*[@data-autotest-id='product-snippet']";
    /**
     * Селектор наименований товаров
     *
     * @author Юрий Юрченко
     */
    private final String selectorProductNames = ".//a[@data-baobab-name='title']";
    /**
     * Селектор цен товаров
     *
     * @author Юрий Юрченко
     */
    private final String selectorProductPrices = ".//*[@data-zone-name='price']//*[@data-auto='mainPrice']";
    /**
     * Поле отражающее состояние страницы с точки зрения того, была ли
     * она проскроллена вниз для отображения в DOM всех товаров.
     *
     * @author Юрий Юрченко
     */
    private boolean pageIsScrolledToBottom;

    /**
     * Создает объект для взаимодействия со страницей товаров категории.
     * Устанавливает явные ожидания длительностью десять секунд. Запоминает
     * переданное значение {@code implicitlyWait} неявного ожидания теста
     * для его восстановления после использования ожиданий отсутствия
     *
     * @param chromeDriver   вебдрайвер для обращения к браузеру
     * @param implicitlyWait неявное ожидание, установленное для теста
     * @author Юрий Юрченко
     */
    public CategoryGoods(WebDriver chromeDriver, int implicitlyWait) {
        this.chromeDriver = chromeDriver;
        wait = new WebDriverWait(chromeDriver, 10);
        IMPLICITLY_WAIT = implicitlyWait;
        pageIsScrolledToBottom = false;
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
            setRangeFilter(rangeFilter.getKey()
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
    @Step("Установка диапазон фильтра: {textInTitle} значениями от: {min} до: {max}")
    public void setRangeFilter(String textInTitle, String min, String max) {
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
        return chromeDriver.findElements(By.xpath(selectorProductNames));
    }

    /**
     * Скроллит страницу вниз до одного из элементов, расположенных внизу страницы.
     * Сохраняет значение в поле {@code pageIsScrolledToBottom}: если страница
     * уже была проскроллена вниз, ничего не делает.
     *
     * @return {@code true}, если метод проскроллил страницу вниз, иначе {@code false}
     * @author Юрий Юрченко
     */
    private boolean scrollToBottom() {
        if (pageIsScrolledToBottom) {
            return false;
        }
        Actions actions = new Actions(chromeDriver);
        actions.moveToElement(chromeDriver.findElement(By.xpath("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@class='cia-cs']")))
                .perform();
        /*JavascriptExecutor js = (JavascriptExecutor) chromeDriver;
        js.executeScript("arguments[0].scrollIntoView(true);",
                chromeDriver.findElement(By.xpath("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@class='cia-cs']")));*/
        pageIsScrolledToBottom = true;
        return true;
    }

    /**
     * Переходит на указанную страницу товаров используя url.
     *
     * @param toPage номер страницы, на которую нужно перейти
     * @author Юрий Юрченко
     */
    public void toPage(int toPage) {
        String currentUrl = chromeDriver.getCurrentUrl();
        String newURL;
        if (currentUrl.contains("page=")) {
            newURL = currentUrl.replaceFirst("page=\\d+", "page=" + toPage);
        } else {
            newURL = currentUrl + "&page=" + toPage;
        }
        chromeDriver.get(newURL);
        pageIsScrolledToBottom = false;
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
        List<WebElement> nextButton = chromeDriver.findElements(By.xpath("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@class='cia-cs' and @data-baobab-name='prev']"));
        if (nextButton.isEmpty()) {
            return false;
        } else {
            nextButton.get(0).click();
            pageIsScrolledToBottom = false;
            return true;
        }
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
        List<WebElement> nextButton = findElementsCustomizable(2, IMPLICITLY_WAIT, chromeDriver, By.xpath("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@data-baobab-name='next']//span"));
        if (nextButton.isEmpty()) {
            return false;
        }
        nextButton.get(0).click();
        waitUntilGoodsLoaded();
        pageIsScrolledToBottom = false;
        return true;
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
            setEnumFilter(enumFilter.getKey(), CheckBoxProcessType.MARK, enumFilter.getValue());
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
    @Step("Установка фильтра перечислений: {textInFilterTitle} значениями: {targets}")
    public void setEnumFilter(String textInFilterTitle, CheckBoxProcessType processType, List<String> targets) {
        WebElement filter = getFilterByTextInTitle(textInFilterTitle);
        Set<String> targetSet = new HashSet<>(targets);

        processAvailableCheckBoxes(filter, targetSet, processType);
        if (targetSet.isEmpty()) {
            return;
        }
        //список может неожиданно свернуться, поэтому требуется
        // несколько попыток
        for (int i = 0; i < 10; i++) {
            if (expand(filter))
                if (processAvailableCheckBoxes(filter, targetSet, processType)) break;
        }

        if (!targetSet.isEmpty()) throw new InvalidArgumentException("There are not found options: " + targetSet);
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
    private boolean processAvailableCheckBoxes(WebElement filter, Set<String> targetSet, CheckBoxProcessType processType) {
        if (soCalledVirtuosoDataScrollerIsDetected(filter)) {
            return processEnumFilterWithSearchField(filter, targetSet, processType);
        }
        List<WebElement> optionList = filter.findElements(By.xpath(".//*[@data-zone-name = 'FilterValue']"));
        for (WebElement option : optionList) {
            String optionTitle = option.findElement(By.xpath(".//span[text()]")).getText().toLowerCase();
            String targ = targetSet.stream().filter(target -> optionTitle.contains(target.toLowerCase())).findFirst().orElse("");
            if (!targ.isEmpty()
                    && checkBoxShouldBeToggled(option, processType)) {
                option.findElement(By.xpath(".//label")).click();
                targetSet.remove(targ);
                if (targetSet.isEmpty()) {
                    break;
                }
            }
        }
        return true;
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
    private boolean processEnumFilterWithSearchField(WebElement filter, Set<String> targetSet, CheckBoxProcessType processType) {
        WebElement filterSearchField = filter.findElement(By.xpath(".//input[@type='text']"));
        for (Iterator<String> iterator = targetSet.iterator(); iterator.hasNext(); ) {
            String target = iterator.next();
            filterSearchField.click();
            filterSearchField.clear();
            filterSearchField.sendKeys(target);
            wait.withTimeout(Duration.of(6, ChronoUnit.SECONDS));
            //Плавающий баг. При некоторых запусках ожидание падает по таймауту,
            //при этом можно заметить, что список разворачивается, текст в поле
            // поиска вводится, но позже список по какой-то причине сворачивается,
            // при том, что кнопки "свернуть" даже нет на странице
            wait.until(ExpectedConditions.or(
                    //пока первый чекбокс - не искомый элемент
                    (dr) -> {
                        List<WebElement> targetCheckBox = findElementsCustomizable(
                                2, IMPLICITLY_WAIT, chromeDriver, filter, By.xpath(".//*[@data-zone-name = 'FilterValue'][1]"));
                        return targetCheckBox.get(0)
                                .getText().toLowerCase().contains(target.toLowerCase());
                    },
                    // или пока список вдруг не свернулся
                    (dr) -> {
                        List<WebElement> expandButton =
                                findElementsCustomizable(2, IMPLICITLY_WAIT, chromeDriver, filter, By.xpath(".//*[@aria-expanded]"));
                        return expandButton.size() != 0 &&
                                expandButton.get(0).getAttribute("aria-expanded").equals("false");
                    }
            ));
            List<WebElement> expandButton = findElementsCustomizable(2, IMPLICITLY_WAIT, chromeDriver, filter, By.xpath(".//*[@aria-expanded]"));
            if (expandButton.size() != 0 &&
                    expandButton.get(0).getAttribute("aria-expanded").equals("false"))
                return false;
            WebElement option = filter.findElement(By.xpath(".//*[@data-zone-name = 'FilterValue'][1]"));
            if (checkBoxShouldBeToggled(option, processType)) {
                option.findElement(By.xpath(".//label/span")).click();
            }
            iterator.remove();
        }
        return true;
    }

    /**
     * Ожидает исчезновения текущих товаров, представленных на странице.
     * Ожидание загрузки новых товаров произойдет автоматически через
     * механизм неявного ожидания. Т.е. внешне работает как ожидание
     * загрузки новых товаров
     *
     * @author Юрий Юрченко
     */
    private void waitUntilGoodsLoaded() {
        wait.withTimeout(Duration.of(10, ChronoUnit.SECONDS)).until(ExpectedConditions
                .invisibilityOfAllElements(chromeDriver.findElements(By.xpath(selectorProductNames))));
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
        return Double.parseDouble(option.findElement(By.xpath("./label/span/span[not(text())]/span"))
                .getCssValue("opacity")) > 0.000000001;
    }

    /**
     * Проверяет фильтр на наличие асинхронных элементов (асинхронного скролла),
     *
     * @param filter фильтр, который следует проверить
     * @return {@code true} если асинхронный скролл обнаружен, иначе {@code false}
     * @author Юрий Юрченко
     */
    private boolean soCalledVirtuosoDataScrollerIsDetected(WebElement filter) {
        return !CustomWait.findElementsCustomizable(2, IMPLICITLY_WAIT, chromeDriver, filter, By.xpath(".//*[@data-virtuoso-scroller='true']"))
                .isEmpty();
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
        List<WebElement> expandButtons = filter.findElements(By.tagName("button"));
        if (expandButtons.size() < 1) return false;
        else if (expandButtons.size() > 1) throw new InvalidSelectorException("more than one expand button was found");
        if (!Boolean.parseBoolean(expandButtons.get(0).getAttribute("aria-expanded"))) {
            expandButtons.get(0).click();
            return true;
        } else {
            return false;
        }
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
        List<WebElement> parameters = chromeDriver.findElements(By.xpath("//*[@id='searchFilters']//fieldset[ .//legend[contains(., '" + textInTitle + "')]]"));
        if (parameters.size() > 1)
            throw new InvalidArgumentException("too many matches for request: \"" + textInTitle + "\"");
        else if (parameters.size() < 1) {
            throw new InvalidArgumentException("no matches for request: \"" + textInTitle + "\"");
        } else
            return parameters.get(0);
    }

    /**
     * Выполняет поиск товара по тексту с помощью поля поиска Маркета.
     *
     * @param text текстовый запрос, по которому производится поиск
     * @author Юрий Юрченко
     */
    public void find(String text) {
        WebElement searchField = chromeDriver.findElement(By.xpath(this.selectorSearchField));
        searchField.click();
        searchField.clear();
        searchField.sendKeys(text);
        chromeDriver.findElement(By.xpath(selectorSearchButton)).click();
        waitUntilGoodsLoaded();
        pageIsScrolledToBottom = false;
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
        return chromeDriver.findElements(By.xpath(selectorProductNames)).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список описаний всех представленных на странице товаров,
     * предварительно проскроллив страницу вниз для получения полного списка.
     *
     * @return список описаний всех товаров на странице
     * @author Юрий Юрченко
     */
    public List<String> getProductDescriptions() {
        scrollToBottom();
        return chromeDriver.findElements(By.xpath(selectorProducts)).stream()
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
        return chromeDriver.findElements(By.xpath(selectorProductPrices)).stream()
                .mapToDouble(price -> Double.parseDouble(price.getText().replaceAll(",", ".").replaceAll("[^\\d.]", "")))
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
