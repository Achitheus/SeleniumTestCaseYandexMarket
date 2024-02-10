package helpers;

import io.qameta.allure.Step;
import org.junit.jupiter.api.function.Executable;

/**
 * Класс содержит переопределенные Assertions. При их использовании в allure-отчетах
 * сообщения отображаются и при успешных проверках. Идея в том, чтобы использовать только
 * ассерты из этого класса, а оригинальные Junit ассерты в тестовые классы не импортировать
 * вообще.
 *
 * @author Юрий Юрченко
 */
public class Assertions {

    /**
     * "Степпированная" версия {@link org.junit.jupiter.api.Assertions#assertTrue(boolean, String)}.
     */
    @Step("Проверяем что нет ошибки: {message}")
    public static void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }

    /**
     * Поскольку внутри будут другие ассерты, дополнительный обрамляющий степ избыточен.
     * Нужен для избежания импорта оригинального Junit ассерта в тестовых классах.
     *
     * @author Юрий Юрченко
     */
    public static void assertALL(Executable... executables) {
        org.junit.jupiter.api.Assertions.assertAll(executables);
    }
}
