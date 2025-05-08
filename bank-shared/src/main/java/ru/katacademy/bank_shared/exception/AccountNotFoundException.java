package ru.katacademy.bank_shared.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующему банковскому счету.
 * <p>
 * Используется для обработки ситуаций, когда запрашиваемый счет не найден в системе.
 * Наследуется от {@link RuntimeException}, что делает его unchecked-исключением.
 * </p>
 *
 * @author Sheffy
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
