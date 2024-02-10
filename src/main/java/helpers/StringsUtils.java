package helpers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringsUtils {
    /**
     * Преобразует коллекцию в строку, разделяя выводимые элементы переходом на новую строку.
     *
     * @param collection коллекция.
     * @return строковое представление коллекции.
     */
    public static String collectionToString(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        if (!it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (true) {
            Object e = it.next();
            sb.append(e);
            if (!it.hasNext())
                return sb.append(']').toString();
            sb.append('\n');
        }
    }

    /**
     * Нечувствительно к регистру убеждается, что строка {@code string} содержит по крайней мере
     * одну из строк списка {@code substrings}.
     *
     * @param string     проверяемая строка.
     * @param substrings список строк.
     * @return {@code true}, если строка {@code string} содержит хотя бы одну строку из {@code substrings}, иначе - {@code false}.
     */
    public static boolean stringContainsAnyStringCaseInsensitively(String string, List<String> substrings) {
        return substrings.stream().anyMatch(
                subStr -> string.toLowerCase().contains(subStr.toLowerCase()));
    }
}
