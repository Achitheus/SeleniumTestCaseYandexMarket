package helpers;

import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DataProvider {
    /**
     * Предоставляет данные в формате: <br> String названиеРаздела; <br> String названиеСекции;<br>
     * Map&lt;String, List&lt;String&gt;&gt; фильтры перечислений, где ключ - название фильтра, значение - список названий чекбоксов
     * (названия чекбоксов регистронезависимы);<br>
     * Map&lt;String, List&lt;String&gt;&gt; фильтры диапазонов (ключ - название фильтра, значение - список из двух элементов:
     * "min", "max"). Значения "min", "max" могут содержать пробелы, разделитель может быть как точкой, так и запятой.
     * Одно из значений может быть пустой строкой;<br>
     * int число, с которым производится сравнение количества доступных товаров на странице.
     *
     * @return поток аргументов данных
     * @author Юрий Юрченко
     */
    public static Stream<Arguments> dataForTestingMarket() {
        RangeFilter priceFilter = new RangeFilter("Цена", "10000", "900000");
        Map<String, List<String>> enumFilters = new HashMap<>();
        enumFilters.put("Производитель", List.of("Lenovo", "Huawei"));
        return Stream.of(
                Arguments.of("Ноутбуки и компьютеры", "Ноутбуки", priceFilter, enumFilters, 12)
        );
    }

}
