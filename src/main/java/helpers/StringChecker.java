package helpers;

import java.util.Collection;
import java.util.List;

public class StringChecker {

    /**
     * Находит строку, в которой нет хотя бы одной ключевой фразы из какого-либо набора.
     * То есть каждая строка из {@code strings} проверяется на содержание в ней хотя бы по одной ключевой фразе
     * из каждого набора {@code lists}
     *
     * @param strings список проверяемых строк
     * @param lists   наборы ключевых фраз
     * @return первую случайную строку из {@code strings}, не прошедшую проверку, либо null, если таковых нет
     * @author Юрий Юрченко
     */
    public static String eachStringContainAnyStringOfEachList(List<String> strings, Collection<List<String>> lists) {
        return strings.stream()
                .filter(string -> !lists.stream()
                        .allMatch(list -> list.stream()
                                .anyMatch(value -> string.toLowerCase().contains(value.toLowerCase()))))
                .findFirst().orElse(null);
    }
}
