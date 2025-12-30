package ru.katacademy.apigateway.health;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Память gateway. Хранит состояние каждого сервиса.
 * При изменении состояния сервиса обновляет данные в ConcurrentHashMap statuss
 *
 *  Автор: Krasitskii Dmitrii
 *  дата: 29.12.2025
 */
@Service
public class DownstreamServiceRegistry {

    private final Map<String, ServiceStatus> statuses =  new ConcurrentHashMap<>();

    /**
     * Если по health сервис NOT_READY, то registry просто обновляет состояние
     *
     * @param serviceName - имя сервиса
     * @param status - состояние сервиса
     */
    public void update(String serviceName, ServiceStatus status){
        statuses.put(serviceName,status);
    }

    /**
     * По умолчанию возвращает UNAVAILABLE, так как сервис может быть не проверен/не запущен/gateway стартовал раньше сервиса
     * Поэтому лучше @return UNAVAILABLE и запретить трафик, чем слать запросы вслепую
     */
    public ServiceStatus get(String serviceName){
        return statuses.getOrDefault(serviceName, ServiceStatus.UNAVAILABLE);
    }

    /**
     * Возвращает статусы всех сервисов.
     * Необходимо для actuator и логов
     */
    public Map<String, ServiceStatus> getAll(){
        return Map.copyOf(statuses);
    }
}