package ru.bellintegrator;

import io.qameta.allure.Allure;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

import static helpers.CustomAllure.screenshotInAllure;
import static helpers.properties.Properties.testProperties;

public class BaseTest {
    public static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected WebDriver driver;
    public static final int IMPLICITLY_WAIT = 15;

    /**
     * Если тест падает, перед закрытием драйвера (браузера) делается скриншот.
     * @author Achitheus (Yury Yurchenko)
     */
    @RegisterExtension
    AfterTestExecutionCallback afterTestExecutionCallback = new AfterTestExecutionCallback() {
        @Override
        public void afterTestExecution(ExtensionContext context) {
            Optional<Throwable> exception = context.getExecutionException();
            exception.ifPresent(throwable -> screenshotInAllure("Screenshot on test fail", driver));
        }
    };

    /**
     * Настраивает и создает веб-драйвер в зависимости от установленных тестовых пропертей,
     * добавляет в отчет информацию о версии Java и операционной системе, на которой
     * тесты были запущены.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    @BeforeEach
    public void beforeEach() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        if (testProperties.useBrowserProfile()) {
            options.addArguments("--user-data-dir=" + testProperties.userDataDir())
                    .addArguments("--profile-directory=" + testProperties.profileDir());
        }
        if (testProperties.headless()) {
            options
                    .addArguments("--headless=new")
                    .addArguments("--user-agent="
                            + getUserAgent().replaceAll("(Headless)", ""));
        }
        if (testProperties.useSelenoid()) {
            driver = new RemoteWebDriver(new URL(testProperties.selenoidURL()), options);
        } else {
            System.setProperty("webdriver.chrome.driver", testProperties.chromeDriver());
            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICITLY_WAIT));
        Allure.parameter("OS", System.getProperty("os.name") + " (" + System.getProperty("os.version") + ')');
        Allure.parameter("JDK", System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ')');
        Allure.parameter("Maven profile", testProperties.mavenProfile());
        Allure.parameter("Selenoid used", testProperties.useSelenoid());
        Allure.parameter("Headless mode", testProperties.headless());
        Allure.parameter("Browser profile used", testProperties.useBrowserProfile());
    }

    /**
     * Возвращает значение заголовка {@code user-agent} для текущего окружения.
     *
     * @return значение {@code user-agent}.
     * @author Achitheus (Yury Yurchenko)
     */
    private String getUserAgent() {
        WebDriver chromedriver = new ChromeDriver(new ChromeOptions().addArguments("--headless=new"));
        String currentUserAgent = (String) ((JavascriptExecutor) chromedriver).executeScript("return navigator.userAgent;");
        chromedriver.quit();
        log.info("user-agent = {}", currentUserAgent);
        return currentUserAgent;
    }

    /**
     * Закрывает веб-драйвер.
     *
     * @author Achitheus (Yury Yurchenko)
     */
    @AfterEach
    public void afterEach() {
        driver.quit();
    }
}
