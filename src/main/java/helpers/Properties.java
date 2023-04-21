package helpers;

import org.aeonbits.owner.ConfigFactory;

public class Properties {
    /**
     * Объект для доступа к различным свойствам представленным в {@link TestProperties}
     *
     * @see TestProperties
     */
    public static TestProperties testProperties = ConfigFactory.create(TestProperties.class);
}
