package helpers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringsUtils {

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

    public static boolean stringContainsAnyStringCaseInsensitively(String string, List<String> list) {
        return list.stream().anyMatch(
                target -> string.toLowerCase().contains(target.toLowerCase()));
    }
}
