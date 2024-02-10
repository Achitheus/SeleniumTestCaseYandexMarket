package helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

public class CustomAllure {

    /**
     * То же, что и {@link Allure#step(String, Allure.ThrowableRunnable)}, но с возможностью установить
     * {@code StepResult.status} изнутри {@code runnable}. <p>
     * Run provided {@link Allure.ThrowableRunnable} as step with given name. Takes no effect
     * if no test run at the moment.
     *
     * @param name     имя степа.
     * @param runnable тело степа.
     * @return то, что возвращает тело степа.
     */
    public static <T> T stepWithChangeableStatus(String name, final Allure.ThrowableRunnable<T> runnable) {
        final String uuid = UUID.randomUUID().toString();
        getLifecycle().startStep(uuid, new StepResult().setName(name));

        try {
            T result = runnable.run();
            getLifecycle().updateStep(uuid, step -> step.setStatus(
                    step.getStatus() == null
                    ? Status.PASSED
                    : step.getStatus()
            ));
            return result;
        } catch (Throwable throwable) {
            getLifecycle().updateStep(s -> s
                    .setStatus(getStatus(throwable).orElse(Status.BROKEN))
                    .setStatusDetails(getStatusDetails(throwable).orElse(null)));
            throw new RuntimeException(throwable);
        } finally {
            getLifecycle().stopStep(uuid);
        }
    }

    /**
     * То же, что и {@link #stepWithChangeableStatus(String, Allure.ThrowableRunnable)} но для {@code RunnableVoid}.
     *
     * @param name     имя степа.
     * @param runnable тело степа.
     */
    public static void stepWithChangeableStatus(String name, final Allure.ThrowableRunnableVoid runnable) {
        final String uuid = UUID.randomUUID().toString();
        getLifecycle().startStep(uuid, new StepResult().setName(name));

        try {
            runnable.run();
            getLifecycle().updateStep(uuid, step -> step.setStatus(
                    step.getStatus() == null
                    ? Status.PASSED
                    : step.getStatus()
            ));
        } catch (Throwable throwable) {
            getLifecycle().updateStep(s -> s
                    .setStatus(getStatus(throwable).orElse(Status.BROKEN))
                    .setStatusDetails(getStatusDetails(throwable).orElse(null)));
            ExceptionUtils.sneakyThrow(throwable);
        } finally {
            getLifecycle().stopStep(uuid);
        }
    }

    /**
     * Добавляет скриншот в аллюр-отчет в текущий тест или степ.
     *
     * @param name   имя файла.
     * @param driver веб-драйвер.
     */
    public static void screenshotInAllure(String name, WebDriver driver) {
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)), ".png");
    }
}
