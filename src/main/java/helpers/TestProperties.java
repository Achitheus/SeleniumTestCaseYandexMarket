package helpers;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "system:env",
        "file:src/test/resources/test.properties"})
public interface TestProperties extends Config {
    @Config.Key("yandex.url")
    String yandexUrl();

    @Config.Key("yandex.title")
    String yandexTitle();

    @Config.Key("yandex.service.title")
    String yandexServiceTitle();
}
