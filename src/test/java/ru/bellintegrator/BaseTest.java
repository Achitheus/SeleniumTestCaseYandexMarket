package ru.bellintegrator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import static helpers.Properties.testProperties;

public class BaseTest {
    protected WebDriver driver;
    public static final int IMPLICITLY_WAIT = 15;

    /**
     * Осуществляет привязку к драйверу, подключает отдельный профиль хрома
     * для хранения кэша и cookies между запусками. Открывает браузер,
     * делает его размер на весь экран, устанавливает время неявного ожидания.
     *
     * @author Юрий Юрченко
     */
    @BeforeEach
    public void beforeEach() {
        System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER") + "/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=" + testProperties.userDataDir());
        options.addArguments("--profile-directory=" + testProperties.profileDir());
        if (testProperties.headless()) {
            options.addArguments("--user-agent=" + editedUserAgent());
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICITLY_WAIT));
    }

    private String editedUserAgent() {
        WebDriver chromedriver = new ChromeDriver();
        chromedriver.get("http://github.com");
        String currentUserAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
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
