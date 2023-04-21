package ru.bellintegrator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class BaseTest {
    protected WebDriver chromeDriver;
    public static final int IMPLICITLY_WAIT = 30;

    /**
     * Осуществляет привязку к драйверу, подключает отдельный профиль хрома
     * для хранения кэша и cookies между запусками. Открывает браузер,
     * делает его размер на весь экран, устанавливает время неявного ожидания.
     *
     * @author Юрий Юрченко
     */
    @BeforeEach
    public void beforeEach() {
        System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER"));
        ChromeOptions options = new ChromeOptions();
        //папка с профилями хрома
        options.addArguments("--user-data-dir=C:\\Users\\Admin\\IdeaProjects\\BellLessonHW2\\chrome-profiles");
        //конкретный профиль
        options.addArguments("--profile-directory=Profile3");

        // или стандартно
        //chromeDriver = new ChromeDriver();
        chromeDriver = new ChromeDriver(options);
        chromeDriver.manage().window().maximize();
        chromeDriver.manage().timeouts().implicitlyWait(IMPLICITLY_WAIT, TimeUnit.SECONDS);
    }

    /**
     * Закрывает браузер.
     *
     * @author Юрий Юрченко
     */
    @AfterEach
    public void afterEach() {
        chromeDriver.quit();
    }
}
