package helpers;

public class RangeFilter {
    public final String NAME;
    public final String MIN;
    public final String MAX;

    public RangeFilter(String name, String min, String max) {
        this.NAME = name;
        this.MIN = min;
        this.MAX = max;
    }

    public double minAsDouble() {
        return Double.parseDouble(MIN.replaceFirst(",", ".").replaceAll("[ ]+", ""));
    }

    public double maxAsDouble() {
        return Double.parseDouble(MAX.replaceFirst(",", ".").replaceAll("[ ]+", ""));
    }

    public boolean isInRange(double number) {
        return minAsDouble() <= number && number <= maxAsDouble();
    }

    @Override
    public String toString() {
        return NAME +": [от " + MIN + " до " + MAX + "]";
    }
}
