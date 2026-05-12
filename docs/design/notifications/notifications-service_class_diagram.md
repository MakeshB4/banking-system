classDiagram

    %% ─── DTOs ───────────────────────────────────────────────────────────────
    class NotificationDTO {
        +Long userId
        +NotificationType type
        +String recipient
        +String subject
        +String message
        +String createdBy
    }

    class NotificationResponseDTO {
        +Long id
        +Long notificationId
        +Long userId
        +NotificationType type
        +String recipient
        +String subject
        +String message
        +Boolean sent
        +LocalDateTime sentTime
        +LocalDateTime creationTime
        +String createdBy
    }

    class ApiResponse~T~ {
        +String status
        +String message
        +T data
        +LocalDateTime timestamp
        +success(String, T) ApiResponse~T~
        +error(String) ApiResponse~T~
    }

    %% ─── Controller ─────────────────────────────────────────────────────────
    class NotificationController {
        -INotificationService notificationService
        +sendNotification(NotificationDTO) ApiResponse~NotificationResponseDTO~
        +getUnsendNotification(Long userId) ApiResponse~List~NotificationResponseDTO~~
    }

    %% ─── Service Interface ───────────────────────────────────────────────────
    class INotificationService {
        <<interface>>
        +sendNotification(NotificationDTO) NotificationResponseDTO
        +getUnsentNotificationsByUser(Long userId) List~NotificationResponseDTO~
    }

    %% ─── Service Implementation ──────────────────────────────────────────────
    class NotificationServiceImpl {
        -NotificationRepository notificationRepository
        -AsyncNotificationSender asyncNotificationSender
        -NotificationSenderService notificationSenderService
        -String EMAIL_PATTERN
        -String PHONE_PATTERN
        +sendNotification(NotificationDTO) NotificationResponseDTO
        +getUnsentNotificationsByUser(Long userId) List~NotificationResponseDTO~
        -validateRecipient(NotificationType, String) void
        -mapToResponseDTO(Notification) NotificationResponseDTO
    }

    %% ─── Async Sender ────────────────────────────────────────────────────────
    class AsyncNotificationSender {
        -NotificationSenderService notificationSenderService
        +sendAsync(Notification) void
    }

    %% ─── Sender Service (Strategy Orchestrator) ──────────────────────────────
    class NotificationSenderService {
        -NotificationFactory notificationFactory
        +send(Notification) void
        +isSupported(NotificationType) boolean
    }

    %% ─── Factory ─────────────────────────────────────────────────────────────
    class NotificationFactory {
        -Map~NotificationType, NotificationStrategy~ strategyMap
        +NotificationFactory(List~NotificationStrategy~)
        +getStrategy(NotificationType) NotificationStrategy
    }

    %% ─── Strategy Interface ──────────────────────────────────────────────────
    class NotificationStrategy {
        <<interface>>
        +send(Notification) void
        +getType() NotificationType
    }

    %% ─── Strategy Implementations ────────────────────────────────────────────
    class EmailNotificationStrategy {
        +send(Notification) void
        +getType() NotificationType
    }

    class SMSNotificationStrategy {
        +send(Notification) void
        +getType() NotificationType
    }

    %% ─── Repository ──────────────────────────────────────────────────────────
    class NotificationRepository {
        <<interface>>
        +findByUserIdAndSentFalseAndDelFlgFalse(Long) List~Notification~
    }

    %% ─── Entity ──────────────────────────────────────────────────────────────
    class BaseEntity {
        <<abstract>>
        +Long id
        +LocalDateTime creationTime
        +LocalDateTime updationTime
        +String createdBy
        +String modifiedBy
        +Boolean delFlg
    }

    class Notification {
        +Long notificationId
        +Long userId
        +NotificationType type
        +String recipient
        +String subject
        +String message
        +Boolean sent
        +LocalDateTime sentTime
    }

    %% ─── Enum ────────────────────────────────────────────────────────────────
    class NotificationType {
        <<enumeration>>
        EMAIL
        SMS
    }

    %% ─── Exceptions ──────────────────────────────────────────────────────────
    class NotificationNotFoundException {
        <<exception>>
    }
    class InvalidRecipientException {
        <<exception>>
    }
    class InvalidNotificationTypeException {
        <<exception>>
    }

    %% ─── Relationships ───────────────────────────────────────────────────────

    NotificationController --> INotificationService : uses
    NotificationController ..> NotificationDTO : receives
    NotificationController ..> ApiResponse : returns

    INotificationService <|.. NotificationServiceImpl : implements

    NotificationServiceImpl --> NotificationRepository : queries
    NotificationServiceImpl --> AsyncNotificationSender : triggers async
    NotificationServiceImpl --> NotificationSenderService : validates type
    NotificationServiceImpl ..> NotificationNotFoundException : throws
    NotificationServiceImpl ..> InvalidRecipientException : throws

    AsyncNotificationSender --> NotificationSenderService : delegates

    NotificationSenderService --> NotificationFactory : resolves strategy
    NotificationSenderService ..> InvalidNotificationTypeException : throws

    NotificationFactory --> NotificationStrategy : manages
    NotificationFactory ..> NotificationType : keyed by

    NotificationStrategy <|.. EmailNotificationStrategy : implements
    NotificationStrategy <|.. SMSNotificationStrategy : implements

    NotificationRepository ..> Notification : persists

    Notification --|> BaseEntity : extends
    Notification --> NotificationType : has

    NotificationServiceImpl ..> NotificationResponseDTO : produces
    NotificationServiceImpl ..> NotificationDTO : consumes