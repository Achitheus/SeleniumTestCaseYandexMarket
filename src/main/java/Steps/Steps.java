package Steps;

import helpers.Assertions;
import io.qameta.allure.Step;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ru.ya.YaMain;
import pages.ru.yandex.market.CategoryGoods;
import pages.ru.yandex.market.ExpandedCatalog;
import pages.ru.yandex.market.MarketMain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static helpers.StringChecker.eachStringContainAnyStringOfEachList;

public class Steps {

    private static WebDriver driver;
    private static WebDriverWait wait;


    /**
     * Осуществляет переход на сайт по указанному {@code url}, затем ожидает
     * максимум 30 секунд пока название сайта не станет {@code title}
     *
     * @param url           адрес, по которому нужно перейти
     * @param title         ожидаемое название сайта
     * @param currentDriver драйвер для обращений к браузеру
     * @author Юрий Юрченко
     */
    @Step("Переходим на сайт: {url}")
    public static void openSite(String url, String title, WebDriver currentDriver) {
        driver = currentDriver;
        driver.get(url);
        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs(title));
    }

    /**
     * Переходит с главной страницы Яндекса на указанный сервис Яндекса.
     *
     * @param serviceTitle название или часть названия сервиса,
     *                     на который нужно перейти
     * @author Юрий Юрченко
     */
    @Step("Нажать слева внизу на значок \"все сервисы\" -> Кликнуть по: {serviceTitle}")
    public static void openService(String serviceTitle) {
        YaMain yaMain = new YaMain(driver);
        yaMain.goToService(serviceTitle);
    }

    /**
     * Открывает каталог Маркета, выбирает раздел {@code section}
     * и переходит в указанную категорию {@code category}
     *
     * @param expandedCatalog Page Object для взаимодействия с раскрытым
     *                        окном каталога товаров
     * @param section         раздел, на который нужно навести курсор
     * @param category        секция раздела, в которую нужно осуществить переход
     * @author Юрий Юрченко
     */
    @Step("Перейти в Каталог -> Навести курсор на раздел: {section}, выбрать категорию: {category}")
    public static void openCatalogAndGoToSectionCategory(ExpandedCatalog expandedCatalog, String section, String category) {
        MarketMain marketMain = new MarketMain(driver);
        // or
        // marketMain.openCatalogAvoidingBug();
        marketMain.openCatalog();
        expandedCatalog.toCategoryProductsPage(section, category);
    }

    /**
     * Устанавливает на странице товаров категории все фильтры диапазонов
     * из {@code rangeFilters}
     *
     * @param categoryGoods Page Object для взаимодействия со страницей
     *                      товаров категории
     * @param rangeFilters  диапазон фильтры в формате: <br> ключ - название,
     *                      <br> значение - список, содержащий пару {min, max}
     * @author Юрий Юрченко
     */
    @Step("Установка значений всех диапазон-фильтров. Затем ожидание результатов")
    public static void setRangeFilters(CategoryGoods categoryGoods, Map<String, List<String>> rangeFilters) {
        categoryGoods.setRangeFilters(rangeFilters);
    }

    /**
     * Устанавливает на странице товаров категории все фильтры перечислений
     * из {@code enumFilters}
     *
     * @param categoryGoods Page Object для взаимодействия со страницей
     *                      товаров категории
     * @param enumFilters   фильтры перечислений в формате: <br> ключ -
     *                      название фильтра, <br> значение - список,
     *                      содержащий названия чекбоксов
     * @author Юрий Юрченко
     */
    @Step("Установка значений всех фильтров перечислений. Затем ожидание результатов")
    public static void setEnumFilters(CategoryGoods categoryGoods, Map<String, List<String>> enumFilters) {
        categoryGoods.setEnumFilters(enumFilters);
    }

    /**
     * Проверяет, соответствует ли количество товаров на странице
     * условию {@code condition}
     *
     * @param categoryGoods Page Object для взаимодействия со страницей
     *                      товаров категории
     * @param condition     условие, которому должно соответствовать
     *                      значение количества товаров на странице
     * @author Юрий Юрченко
     */
    @Step("Проверка количества товаров на странице по условию")
    public static void comparePageProductsCountToValue(CategoryGoods categoryGoods, IntPredicate condition) {
        int countOfProducts = categoryGoods.productListSize();
        Assertions.assertTrue(condition.test(countOfProducts)
                , "Количество элементов на странице равное " + countOfProducts + " не соответствует условию");
    }

    /**
     * Производит поиск описания товара, не соответствующего
     * фильтрам перечислений {@code enumValues}.
     *
     * @param categoryGoods Page Object для взаимодействия со страницей
     *                      товаров категории
     * @param enumValues    набор списков названий чекбоксов, которым
     *                      должны соответствовать описания всех товаров
     * @return первое не соответствующее хотя бы одному фильтру перечислений
     * описание товара
     * @author Юрий Юрченко
     */
    private static String findBadPageProductDescriptionByEnumFilters(CategoryGoods categoryGoods, Collection<List<String>> enumValues) {
        List<String> descriptions = categoryGoods.getProductDescriptions();
        return eachStringContainAnyStringOfEachList(descriptions, enumValues);
    }

    /**
     * Производит поиск описания товара, не соответствующего
     * диапазону фильтра цен {@code rangePair}.
     *
     * @param categoryGoods Page Object для взаимодействия со страницей
     *                      товаров категории
     * @param rangePair     диапазон цен, которому должны соответствовать
     *                      стоимости всех товаров
     * @return первое не соответствующее диапазону фильтра цен значение
     * стоимости товара
     * @author Юрий Юрченко
     */
    private static Double findBadPageProductPrice(CategoryGoods categoryGoods, List<String> rangePair) {
        Double min = rangePair.get(0).isEmpty() ? null : Double.parseDouble(rangePair.get(0).replaceAll(",", ".").replaceAll(" ", ""));
        Double max = rangePair.get(1).isEmpty() ? null : Double.parseDouble(rangePair.get(1).replaceAll(",", ".").replaceAll(" ", ""));
        List<Double> costs = categoryGoods.getProductPrices();
        Predicate<Double> condition;
        if (min == null) {
            condition = price -> !(price < max);
        } else if (max == null) {
            condition = price -> !(price > min);
        } else {
            condition = price -> !(min <= price && price < max);
        }
        return costs.stream().filter(condition).findAny().orElse(null);
    }

    /**
     * Проходит по всем доступным страницам товаров проверяя соответствует
     * ли цена и описание товаров диапазону фильтра цены {@code rangePair}
     * и всем фильтрам перечислений {@code enumValues}. Метод прекращает
     * работу, если найдены товары не соответствующие обоим параметрам по
     * отдельности. Т.е., например, если найден товар с неподходящей ценой
     * метод продолжит работу, но уже проверяя только соответствие фильтрам
     * перечислений. <br> Во избежание бесконечного цикла, выставлено ограничение
     * в тысячу итераций (метод проверяет максимум тысячу страниц).
     *
     * @param categoryGoods Page Object для взаимодействия со страницей
     *                      товаров категории
     * @param enumValues    набор списков названий чекбоксов, которым должны
     *                      соответствовать описания всех товаров
     * @param rangePair     диапазон, которому должны соответствовать цены
     *                      всех товаров
     * @author Юрий Юрченко
     */
    @Step("Проверить что на всех страницах предложения соответствуют фильтрам")
    public static void checkPricesAndDescriptionsAllProductsOfAllPages(CategoryGoods categoryGoods, Collection<List<String>> enumValues, List<String> rangePair) {
        int counter = 0;
        String badDescription = null;
        String badDescriptionMessage = null;
        Double badPrice = null;
        String badPriceMessage = null;
        do {
            counter++;
            if (badDescription == null) {
                badDescription = findBadPageProductDescriptionByEnumFilters(categoryGoods, enumValues);
                badDescriptionMessage = "На странице: " + counter + " найден товар, описание которого не соответствует по крайней мере одному фильтру-перечислению:\n" + badDescription;
            }
            if (badPrice == null) {
                badPrice = findBadPageProductPrice(categoryGoods, rangePair);
                badPriceMessage = "На странице: " + counter + " найдена цена: " + badPrice + ", не соответствующая диапазону: " + rangePair;
            }
        } while (categoryGoods.nextPage() && counter < 1000 && (badPrice == null || badDescription == null));

        final Double finalBadPrice = badPrice;
        final String finalBadDescription = badDescription;
        final String finalBadPriceMessage = badPriceMessage;
        final String finalBadDescriptionMessage = badDescriptionMessage;

        Assertions.assertALL(
                () -> Assertions.assertTrue(finalBadPrice == null,
                        finalBadPriceMessage),
                () -> Assertions.assertTrue(finalBadDescription == null,
                        finalBadDescriptionMessage)
        );
    }

    /**
     * Переходит на страницу {@code pageNumber} списка товаров.
     *
     * @param categoryGoods Page Object для взаимодействия со страницей
     *                      товаров категории
     * @param pageNumber    номер страницы, на которую нужно перейти
     * @author Юрий Юрченко
     */
    @Step("Перейти на страницу: {pageNumber} с результатами поиска товаров")
    public static void goToPage(CategoryGoods categoryGoods, int pageNumber) {
        categoryGoods.toPage(pageNumber);
    }

    /**
     * Метод возвращает наименование {@code number}-го по счету
     * товара страницы.
     *
     * @param categoryGoods Page Object для взаимодействия со
     *                      страницей товаров категории
     * @param number        номер товара, чье наименование
     *                      возвращает метод
     * @return наименование товара, который идет по счету
     * {@code number}-тым
     * @author Юрий Юрченко
     */
    @Step("Получить наименование товара под номером: {number}")
    public static String getProductNameWithNumber(CategoryGoods categoryGoods, int number) {
        return categoryGoods.getClickableProductNames().get(number - 1).getText();
    }

    /**
     * Выполняет поиск товара по тексту с помощью поля поиска Маркета.
     *
     * @param categoryGoods Page Object для взаимодействия со
     *                      страницей товаров категории
     * @param query         текстовый запрос, по которому
     *                      производится поиск
     * @author Юрий Юрченко
     */
    @Step("Найти товар по запросу: {query}")
    public static void find(CategoryGoods categoryGoods, String query) {
        categoryGoods.find(query);
    }

    /**
     * Метод проверяет, что среди всех наименований товаров на странице
     * есть хотя бы одно, содержащее в себе текст {@code targetProductName}
     *
     * @param categoryGoods     Page Object для взаимодействия со
     *                          страницей товаров категории
     * @param targetProductName текст, который должен содержаться
     *                          в наименовании хотя бы одного товара
     * @author Юрий Юрченко
     */
    @Step("Проверить, что среди товаров страницы есть товар, содержащий в названии:\n{targetProductName}")
    public static void checkProductNamesByContaining(CategoryGoods categoryGoods, String targetProductName) {
        List<String> productNames = categoryGoods.getProductNames();
        boolean productSearchResult = productNames.stream()
                .anyMatch(name -> name.toLowerCase().contains(targetProductName.toLowerCase()));
        Assertions.assertTrue(productSearchResult, "Среди товаров не найдено ни одного содержащего в названии:\n" + targetProductName);
    }
}
