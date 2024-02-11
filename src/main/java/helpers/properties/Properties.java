package helpers.properties;

import org.aeonbits.owner.ConfigFactory;

import static helpers.properties.PropertiesLogger.logAllProperties;

public class Properties {
    /**
     * Объект для доступа к различным свойствам представленным в {@link TestProperties}.
     */
    public static TestProperties testProperties = ConfigFactory.create(TestProperties.class);

    static {
        logAllProperties(Properties.class);
    }
}
