package helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;

public class AllureCustom {
    private static final AllureLifecycle lifecycle = Allure.getLifecycle();

    public static void markOuterStepAsFailedAndStop() {
        lifecycle.updateStep(step -> step.setStatus(Status.FAILED));
        lifecycle.stopStep();
    }
}
