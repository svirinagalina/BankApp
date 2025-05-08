package ru.katacademy.bank_app.notification.infrastructure;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.notification.application.NotificationService;
import ru.katacademy.bank_app.notification.template.TransferNotificationTemplate;
import ru.katacademy.bank_shared.valueobject.Money;

import java.util.logging.Logger;

/**
 * Реализация сервиса уведомлений для отправки уведомлений о переводах.
 * <p>
 * Этот сервис используется для отправки уведомлений, когда осуществляется перевод между счетами.
 * Уведомления форматируются с использованием шаблона и записываются в лог.
 * </p>
 *
 * @see NotificationService
 *
 * @author Sheffy
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    // Логгер для записи сообщений уведомлений
    private static final Logger logger = Logger.getLogger(NotificationServiceImpl.class.getName());

    /**
     * Отправляет уведомление о переводе средств между двумя счетами.
     * Форматирует сообщение об уведомлении, используя шаблон уведомления.
     * Уведомление выводится в лог с помощью Logger.
     *
     * @param fromAccount Счет, с которого производится перевод.
     * @param toAccount   Счет, на который переводятся средства.
     * @param amount      Сумма перевода.
     */
    @Override
    public void sendTransferNotification(Account fromAccount, Account toAccount, Money amount) {
        // Форматируем сообщение с использованием шаблона уведомления
        final String message = TransferNotificationTemplate.format(fromAccount, toAccount, amount);

        // Логируем уведомление
        logger.info(message);
    }
}
