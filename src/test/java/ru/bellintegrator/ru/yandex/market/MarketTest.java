package ru.bellintegrator.ru.yandex.market;

import helpers.pageable.AssertionCheck;
import helpers.pageable.PageableChecker;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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

public class MarketTest extends BaseTest {

    /**
     * @param section      секция, в которой нужно найти категорию
     * @param category     категория секции, в которую нужно перейти
     * @param rangeFilters фильтры диапазонов (ключ - название фильтра,
     *                     значение - список из двух элементов: "min", "max").
     *                     Значения "min", "max" могут содержать пробелы, разделитель
     *                     может быть как точкой, так и запятой.
     *                     Одно из значений может быть пустой строкой
     * @param enumFilters фильтры перечислений, где ключ - название фильтра,
     *                    значение - список названий чекбоксов (названия
     *                    чекбоксов регистронезависимы)
     * @param productCount значение, с которым будет сравнено количество доступных
     *                     на странице товаров
     */
    @Disabled
    @Feature("Проверка фильтров и поиска Маркета")
    @DisplayName("Проверка работы фильтров и результатов при поиске")
    @ParameterizedTest(name = "{displayName}: {arguments}")
    @MethodSource(value = "helpers.DataProvider#dataForTestingMarket")
    public void checkMarket(String section, String category,
                            Map<String, List<String>> rangeFilters, Map<String, List<String>> enumFilters, int productCount) {
        driver.get(testProperties.yandexUrl());
        YaMain yaMain = new YaMain(driver);
        yaMain.goToService(testProperties.yandexServiceTitle());
        CategoryGoods categoryGoods = new CategoryGoods(driver, IMPLICITLY_WAIT);
        categoryGoods.toCategoryProductsPage(section, category);
        categoryGoods.setEnumFilters(enumFilters);
        categoryGoods.setRangeFilters(rangeFilters);

        int actualProductCount = categoryGoods.getClickableProductNames().size();
        assertTrue(actualProductCount > productCount, "Число товаров " + actualProductCount
                + " не соответствует условию: " + "число товаров > " + productCount);

        PageableChecker<CategoryGoods> pageableChecker = categoryGoods.schedulePageableCheck()
                .checkAllPages(true)
                .addCheckThatEachElement(
                        "соответствует фильтру Производитель: " + enumFilters.get("Производитель"),
                        new AssertionCheck<>(
                                CategoryGoods::getProductNames,
                                name -> Assertions.assertTrue(stringContainsAnyStringCaseInsensitively(name, enumFilters.get("Производитель")))
                        )
                )
                .addCheckThatEachElement(
                        "соответствует фильтру Цена: " + rangeFilters.get("Цена"),
                        new AssertionCheck<>(
                                CategoryGoods::getProductPrices,
                                price -> Assertions.assertTrue(price > 100_000 && price < 200_000)
                        )
                )
                .runWithoutThrowing();

        categoryGoods.toPage(1);
        String firstProductName = categoryGoods.getProductNames().getFirst();
        categoryGoods.findProduct(firstProductName);
        assertTrue(categoryGoods.getProductNames().contains(firstProductName),
                "Результаты поиска не содержат товара: " + firstProductName);

        pageableChecker.assertAll();
    }
}
