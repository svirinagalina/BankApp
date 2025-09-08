# Monolith → Microservices mapping


Name                         Monolith Path                                                                                       New Path
Account                      src/main/java/ru/katacademy/bank_app/account/domain/entity/Account.java                             fraud-detection/src/main/java/ru/katacademy/bank_app/frauddetection/account/domain/entity/Account.java
AccountDto                   src/main/java/ru/katacademy/bank_app/account/application/dto/AccountDto.java                           
AccountEntity                src/main/java/ru/katacademy/bank_app/account/infrastructure/persistence/entity/AccountEntity.java   
AccountMapper                src/main/java/ru/katacademy/bank_app/account/application/mapper/AccountMapper.java                  
AccountStatus                src/main/java/ru/katacademy/bank_app/account/domain/enumtype/AccountStatus.java                     fraud-detection/src/main/java/ru/katacademy/bank_app/frauddetection/account/domain/enumtype/AccountStatus.java
AccountRepository            src/main/java/ru/katacademy/bank_app/account/domain/repository/AccountRepository.java
AccountRepositoryImpl        src/main/java/ru/katacademy/bank_app/account/infrastructure/repository/AccountRepositoryImpl.java
JpaAccountRepository         src/main/java/ru/katacademy/bank_app/account/infrastructure/repository/JpaAccountRepository.java
TransferEventPublisher       src/main/java/ru/katacademy/bank_app/account/application/port/out/TransferEventPublisher.java
TransferValidator            src/main/java/ru/katacademy/bank_app/account/application/service/TransferValidator.java
CreateAccountCommand         src/main/java/ru/katacademy/bank_app/account/application/command/CreateAccountCommand.java
AccountService               src/main/java/ru/katacademy/bank_app/account/application/service/AccountService.java
AccountEntityMapper          src/main/java/ru/katacademy/bank_app/account/infrastructure/persistence/mapper/AccountEntityMapper.java
FraudActionService           src/main/java/ru/katacademy/bank_app/notification/application/FraudActionService.java
FraudActionServiceImpl       src/main/java/ru/katacademy/bank_app/notification/infrastructure/FraudActionServiceImpl.java
NotificationService          src/main/java/ru/katacademy/bank_app/notification/application/NotificationService.java              notification-service/src/main/java/ru/katacademy/notification/application/service/NotificationService.java
NotificationServiceImpl      src/main/java/ru/katacademy/bank_app/notification/infrastructure/NotificationServiceImpl.java       notification-service/src/main/java/ru/katacademy/notification/application/service/NotificationServiceImpl.java
TransferNotificationTemplate src/main/java/ru/katacademy/bank_app/notification/template/TransferNotificationTemplate.java        notification-service/src/main/java/ru/katacademy/notification/application/template/TransferNotificationTemplate.java
ExceptionConfig              src/main/java/ru/katacademy/bank_app/shared/exception/ExceptionConfig.java