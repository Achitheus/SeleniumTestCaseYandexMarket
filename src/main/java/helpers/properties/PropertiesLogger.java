package helpers.properties;

import org.aeonbits.owner.Accessible;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class PropertiesLogger {
    public static final Logger logger = LoggerFactory.getLogger(PropertiesLogger.class);

    /**
     * Находит в классе все статические поля с типом {@link org.aeonbits.owner.Accessible} и
     * логгирует содержащиеся в них проперти.
     *
     * @param clazz класс со статическими проперти-полями.
     * @author Achitheus (Yury Yurchenko)
     */
    public static void logAllProperties(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field ->
                        Modifier.isStatic(field.getModifiers())
                                && Arrays.stream(field.getType().getInterfaces()).anyMatch(interf -> interf == Accessible.class))
                .forEach(field -> {
                    try {
                        logProperties(field.getName(), (Accessible) field.get(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Логгирует переданный именованный набор пропертей.
     *
     * @param titleOfSet название логгируемого набора пропертей.
     * @param properties логгируемые проперти.
     * @author Achitheus (Yury Yurchenko)
     */
    public static void logProperties(String titleOfSet, Accessible properties) {
        logger.info(">>> >>> {} <<< <<<:", titleOfSet);
        Map<String, String> sortedProperties = new TreeMap<>();
        properties.fill(sortedProperties);
        sortedProperties.forEach((key, value) ->
                logger.info(String.format("%-25s = %s", key, value)));
    }
}