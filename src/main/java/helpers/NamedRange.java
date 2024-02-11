package helpers;

/**
 * Класс для удобной работы с именованными диапазонами.
 *
 * @author Achitheus (Yury Yurchenko)
 */
public class NamedRange {
    public final String NAME;
    public final String MIN;
    public final String MAX;
    public final double DOUBLE_MIN;
    public final double DOUBLE_MAX;

    /**
     * Создает объект именованного диапазона. Дробная часть границ диапазонов может отделяться как точкой, так и запятой.
     * Записи чисел могут содержать пробелы.
     *
     * @param name имя диапазона.
     * @param min  нижняя граница диапазона.
     * @param max  верхняя граница диапазона.
     * @author Achitheus (Yury Yurchenko)
     */
    public NamedRange(String name, String min, String max) {
        this.NAME = name;
        this.MIN = min;
        this.MAX = max;
        DOUBLE_MIN = stringAsDouble(MIN);
        DOUBLE_MAX = stringAsDouble(MAX);
    }

    /**
     * @author Achitheus (Yury Yurchenko)
     */
    private double stringAsDouble(String doubleNumber) {
        return Double.parseDouble(prepareDoubleForParsing(doubleNumber));
    }

    /**
     * @author Achitheus (Yury Yurchenko)
     */
    private String prepareDoubleForParsing(String doubleNumber) {
        return doubleNumber.replaceFirst(",", ".")
                           .replaceAll("[ ]+", "");
    }

    /**
     * Проверяет, принадлежит ли переданное число диапазону (включая границы).
     *
     * @param number проверяемое число.
     * @return {@code true}, если переданное число принадлежит диапазону, иначе - {@code false}.
     * @author Achitheus (Yury Yurchenko)
     */
    public boolean includes(double number) {
        return DOUBLE_MIN <= number && number <= DOUBLE_MAX;
    }

    @Override
    public String toString() {
        return NAME +": [от " + DOUBLE_MIN + " до " + DOUBLE_MAX + "]";
    }
}
