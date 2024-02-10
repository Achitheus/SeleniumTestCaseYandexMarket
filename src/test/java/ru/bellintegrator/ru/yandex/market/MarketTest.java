package ru.bellintegrator.ru.yandex.market;

import helpers.NamedRange;
import helpers.pageable.AssertionCheckThatEachElement;
import helpers.pageable.PageableChecker;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.ru.ya.YaMain;
import pages.ru.yandex.market.CategoryGoods;
import ru.bellintegrator.BaseTest;

import java.util.List;
import java.util.Map;

import static helpers.Assertions.assertTrue;
import static helpers.Properties.testProperties;
import static helpers.StringsUtils.stringContainsAnyStringCaseInsensitively;
import static io.qameta.allure.Allure.step;

public class MarketTest extends BaseTest {

    /**
     * @param section      секция, в которой нужно найти категорию
     * @param category     категория секции, в которую нужно перейти
     * @param priceRange   диапазон цен товаров.
     * @param enumFilters  фильтры перечислений, где ключ - название фильтра,
     *                     значение - список названий чекбоксов
     * @param productCount актуальное количество товаров на странице должно быть строго больше данного значения.
     */
    @Feature("Фильтрация товаров")
    @Feature("Поиск товара")
    @DisplayName("Проверка работы фильтров товаров и поиска товара")
    @Link(name = "Тест кейс", url = "https://github.com/Achitheus/SeleniumTestCaseYandexMarket")
    @ParameterizedTest(name = "[{index}]: {arguments}")
    @MethodSource(value = "helpers.DataProvider#dataForTestingMarket")
    public void checkMarket(String section, String category,
                            NamedRange priceRange, Map<String, List<String>> enumFilters, int productCount) {
        step("Переход по адресу " + testProperties.yandexUrl(), () -> driver.get(testProperties.yandexUrl()));
        YaMain yaMain = new YaMain(driver);
        yaMain.goToService(testProperties.yandexServiceTitle());
        CategoryGoods categoryGoods = new CategoryGoods(driver, IMPLICITLY_WAIT);
        categoryGoods.toCategoryProductsPage(section, category);
        categoryGoods.setEnumFilters(enumFilters);
        categoryGoods.setRangeFilter(priceRange);

        int actualProductCount = categoryGoods.getClickableProductNames().size();
        assertTrue(actualProductCount > productCount, "Число товаров " + actualProductCount
                + " не соответствует условию: " + "число товаров > " + productCount);

        PageableChecker<CategoryGoods> pageableChecker = categoryGoods.schedulePageableCheck()
                .addCheck(new AssertionCheckThatEachElement<>("соответствует фильтру Производитель: " + enumFilters.get("Производитель"),
                        CategoryGoods::getProductNames,
                        (name, message) -> Assertions.assertTrue(stringContainsAnyStringCaseInsensitively(name, enumFilters.get("Производитель")), message)
                ))
                .addCheck(new AssertionCheckThatEachElement<>("соответствует фильтру " + priceRange,
                        CategoryGoods::getProductPrices,
                        (price, message) -> Assertions.assertTrue(priceRange.includes(price), message)
                ))
                .beLazy(true)
                .runWithoutThrowing();

        categoryGoods.toPage(1);
        String firstProductName = categoryGoods.getProductNames().get(0);
        categoryGoods.findProduct(firstProductName);
        assertTrue(categoryGoods.getProductNames().contains(firstProductName),
                "Результаты поиска не содержат товара: " + firstProductName);

        pageableChecker.assertAll();
    }
}
