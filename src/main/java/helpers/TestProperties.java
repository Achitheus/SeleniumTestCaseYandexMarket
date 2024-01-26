package helpers;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "system:env",
        "file:target/test-classes/test.properties"})
public interface TestProperties extends Config {
    @Config.Key("yandex.url")
    String yandexUrl();

    @Config.Key("yandex.title")
    String yandexTitle();

    @Config.Key("yandex.service.title")
    String yandexServiceTitle();

    @Key("use.browser.profile")
    boolean useBrowserProfile();

    @Key("user.data.dir")
    String userDataDir();

    @Key("profile.dir")
    String profileDir();

    @Key("maven.profile")
    String mavenProfile();

    @Key("headless")
    boolean headless();

    @Key("use.selenoid")
    boolean useSelenoid();
}
