package ru.bellintegrator.ru.yandex.market;

import Steps.Steps;
import helpers.Assertions;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.ru.yandex.market.CategoryGoods;
import pages.ru.yandex.market.ExpandedCatalog;
import ru.bellintegrator.BaseTest;

import java.util.List;
import java.util.Map;

import static helpers.Properties.*;

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
    @Feature("Проверка фильтров и поиска Маркета")
    @DisplayName("Проверка работы фильтров и результатов при поиске")
    @ParameterizedTest(name = "{displayName}: {arguments}")
    @MethodSource(value = "helpers.DataProvider#dataForTestingMarket")
    public void checkMarket(String section, String category,
                            Map<String, List<String>> rangeFilters, Map<String, List<String>> enumFilters, int productCount) {
        Steps.openSite(testProperties.yandexUrl(), testProperties.yandexTitle(), chromeDriver);
        Steps.openService(testProperties.yandexServiceTitle());
        ExpandedCatalog expandedCatalog = new ExpandedCatalog(chromeDriver);
        Steps.openCatalogAndGoToSectionCategory(expandedCatalog, section, category);
        CategoryGoods categoryGoods = new CategoryGoods(chromeDriver, IMPLICITLY_WAIT);
        Steps.setRangeFilters(categoryGoods, rangeFilters);
        Steps.setEnumFilters(categoryGoods, enumFilters);
        Assertions.assertALL(
                () -> Steps.comparePageProductsCountToValue(categoryGoods, (v) -> v > productCount),
                () -> Steps.checkPricesAndDescriptionsAllProductsOfAllPages(categoryGoods, enumFilters.values(), rangeFilters.get("Цена")),
                () -> {
                    Steps.goToPage(categoryGoods, 1);
                    String targetProductName = Steps.getProductNameWithNumber(categoryGoods, 1);
                    Steps.find(categoryGoods, targetProductName);
                    Steps.checkProductNamesByContaining(categoryGoods, targetProductName);
                }
        );
    }

}
