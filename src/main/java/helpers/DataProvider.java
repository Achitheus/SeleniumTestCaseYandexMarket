package helpers;

import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DataProvider {
    /**
     * Предоставляет данные в формате: <br>
     * {@code String} названиеРаздела; <br>
     * {@code String} названиеСекции;<br>
     * {@code Map<String, List<String>>} фильтры перечислений, где ключ - название фильтра, значение - список названий чекбоксов;<br>
     * {@code RangeFilter} фильтр диапазона цен;<br>
     * {@code int} количество доступных на странице товаров должно превышать это значение.
     *
     * @return поток аргументов данных.
     * @author Юрий Юрченко
     */
    public static Stream<Arguments> dataForTestingMarket() {
        NamedRange priceFilter = new NamedRange("Цена", "10000", "900000");
        Map<String, List<String>> enumFilters = new HashMap<>();
        enumFilters.put("Производитель", List.of("Lenovo", "Huawei"));
        return Stream.of(
                Arguments.of("Ноутбуки и компьютеры", "Ноутбуки", priceFilter, enumFilters, 12)
        );
    }

}
