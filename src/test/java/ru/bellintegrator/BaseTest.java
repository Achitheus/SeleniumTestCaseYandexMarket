package ru.bellintegrator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

import static helpers.Properties.testProperties;
import static helpers.Screenshoter.screenshotInAllure;

public class BaseTest {
    protected WebDriver driver;
    public static final int IMPLICITLY_WAIT = 15;

    @RegisterExtension
    AfterTestExecutionCallback afterTestExecutionCallback = new AfterTestExecutionCallback() {
        @Override
        public void afterTestExecution(ExtensionContext context) throws Exception {
            Optional<Throwable> exception = context.getExecutionException();
            exception.ifPresent(throwable -> screenshotInAllure("Screenshot on test fail", driver));
        }
    };

    /**
     * Осуществляет привязку к драйверу, подключает отдельный профиль хрома
     * для хранения кэша и cookies между запусками. Открывает браузер,
     * делает его размер на весь экран, устанавливает время неявного ожидания.
     *
     * @author Юрий Юрченко
     */
    @BeforeEach
    public void beforeEach() throws MalformedURLException {
        System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER") + "/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        if(testProperties.useBrowserProfile()) {
            options.addArguments("--user-data-dir=" + testProperties.userDataDir());
            options.addArguments("--profile-directory=" + testProperties.profileDir());
        }
        if (testProperties.headless()) {
            options
                    .addArguments("--headless=new")
                    .addArguments("--user-agent=" + editedUserAgent());
        }
        if(testProperties.useSelenoid()) {
            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
        } else {
            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICITLY_WAIT));
    }

    private String editedUserAgent() {
        WebDriver chromedriver = new ChromeDriver(new ChromeOptions().addArguments("--headless=new"));
        chromedriver.get("http://github.com");
        String currentUserAgent = (String) ((JavascriptExecutor) chromedriver).executeScript("return navigator.userAgent;");
        chromedriver.quit();
        return currentUserAgent.replaceAll("(Headless)", "");
    }

    /**
     * Закрывает браузер.
     *
     * @author Юрий Юрченко
     */
    @AfterEach
    public void afterEach() {
        driver.quit();
    }
}
