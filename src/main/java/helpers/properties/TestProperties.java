package helpers.properties;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources("file:target/test-classes/test.properties")
public interface TestProperties extends Accessible {
    @Key("yandex.url")
    String yandexUrl();

    @Key("yandex.title")
    String yandexTitle();

    @Key("chrome.driver")
    String chromeDriver();

    @Key("yandex.service.title")
    String yandexServiceTitle();

    @Key("use.browser.profile")
    boolean useBrowserProfile();

    @Key("user.data.dir")
    String userDataDir();

    @Key("selenoid.url")
    String selenoidURL();

    @Key("profile.dir")
    String profileDir();

    @Key("maven.profile")
    String mavenProfile();

    @Key("headless")
    boolean headless();

    @Key("use.selenoid")
    boolean useSelenoid();
}
