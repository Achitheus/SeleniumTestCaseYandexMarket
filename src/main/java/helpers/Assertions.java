package helpers;

import io.qameta.allure.Step;
import org.junit.jupiter.api.function.Executable;

/**
 * Класс содержит переопределенные Asserts. При их использовании в allure-отчетах
 * сообщения отображаются и при успешных проверках
 *
 * @author Юрий Юрченко
 */
public class Assertions {

    @Step("Проверяем что нет ошибки: {message}")
    public static void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }

    /**
     * Поскольку внутри будут другие ассерты, дополнительные сообщения избыточны.
     * Степ не вешаем.
     *
     * @author Юрий Юрченко
     */
    public static void assertALL(Executable... executables) {
        org.junit.jupiter.api.Assertions.assertAll(executables);
    }
}
