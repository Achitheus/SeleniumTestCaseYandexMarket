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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

import static helpers.properties.Properties.testProperties;
import static helpers.CustomAllure.screenshotInAllure;

public class BaseTest {
    protected WebDriver driver;
    public static final int IMPLICITLY_WAIT = 15;

    /**
     * Если тест падает, перед закрытием драйвера (браузера) делается скриншот.
     * @author Achitheus (Yury Yurchenko)
     */
    @RegisterExtension
    AfterTestExecutionCallback afterTestExecutionCallback = new AfterTestExecutionCallback() {
        @Override
        public void afterTestExecution(ExtensionContext context) throws Exception {
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
        if(testProperties.useBrowserProfile()) {
            options.addArguments("--user-data-dir=" + testProperties.userDataDir())
                    .addArguments("--profile-directory=" + testProperties.profileDir());
        }
        if (testProperties.headless()) {
            options
                    .addArguments("--headless=new")
                    .addArguments("--user-agent=" + editedUserAgent());
        }
        if(testProperties.useSelenoid()) {
            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
        } else {
            System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER") + "/chromedriver.exe");
            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICITLY_WAIT));
        Allure.parameter("OS", System.getProperty("os.name") + " (" + System.getProperty("os.version") + ')');
        Allure.parameter("JDK", System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ')');
    }

    /**
     * Получает user-agent и возвращает отредактированную его копию, не содержащую упоминаний о том, что браузер запущен
     * в headless режиме.
     *
     * @return значение user-agent, не содержащее подстроки "Headless".
     * @author Achitheus (Yury Yurchenko)
     */
    private String editedUserAgent() {
        WebDriver chromedriver = new ChromeDriver(new ChromeOptions().addArguments("--headless=new"));
        chromedriver.get("http://github.com");
        String currentUserAgent = (String) ((JavascriptExecutor) chromedriver).executeScript("return navigator.userAgent;");
        chromedriver.quit();
        return currentUserAgent.replaceAll("(Headless)", "");
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
