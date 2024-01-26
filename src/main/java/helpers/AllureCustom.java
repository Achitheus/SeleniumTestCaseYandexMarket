package helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ExceptionUtils;

import java.util.UUID;

import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

public class AllureCustom {
    private static final AllureLifecycle lifecycle = getLifecycle();

    /**
     * Run provided {@link Allure.ThrowableRunnable} as step with given name. Takes no effect
     * if no test run at the moment.
     *
     * @param runnable the step's body.
     */
    public static <T> T stepWithoutRewriting(String name, final Allure.ThrowableRunnable<T> runnable) {
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

    public static void stepWithoutRewriting(String name, final Allure.ThrowableRunnableVoid runnable) {
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
}
