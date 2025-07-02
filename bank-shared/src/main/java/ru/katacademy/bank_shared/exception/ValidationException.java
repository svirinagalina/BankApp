package ru.katacademy.bank_shared.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {
    private List<String> errors;

    public ValidationException(List<String> errors) {
        // мы создаем копию списка, чтобы защититься от изменений во внешнем коде.
        this.errors = new ArrayList<>(errors);  // Сохраняем копию списка
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors); // Возвращаем неизменяемый список
    }
}
