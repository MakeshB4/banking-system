classDiagram
    %% Enums
    class UserType {
        <<enumeration>>
        RETAIL
        CORPORATE
    }
    
    class UserStatus {
        <<enumeration>>
        PENDING_APPROVAL
        APPROVED
        REJECTED
        ACTIVE
        INACTIVE
    }
    
    class AccountType {
        <<enumeration>>
        SAVINGS
        CURRENT
        FIXED_DEPOSIT
        RECURRING_DEPOSIT
    }
    
    class AccountStatus {
        <<enumeration>>
        ACTIVE
        DORMANT
        CLOSED
    }
    
    class NotificationType {
        <<enumeration>>
        EMAIL
        SMS
    }
    
    class TransactionStatus {
        <<enumeration>>
        INITIATED
        PENDING
        PROCESSING
        SUCCESS
        FAILED
        REVERSED
        CANCELLED
    }
    
    class PaymentType {
        <<enumeration>>
        DOMESTIC
        INTERNATIONAL
        NEFT
        RTGS
        IMPS
        SWIFT
    }

    %% Main Entities
    class User {
        -Long userId
        -String loginId
        -String password
        -String email
        -String phoneNumber
        -UserType userType
        -UserStatus status
        -Long cifId
        -Boolean delFlg
        -LocalDateTime creationTime
        -LocalDateTime updationTime
        -LocalDateTime modificationTime
        -String createdBy
        -String modifiedBy
        +register() void
        +updateProfile() void
        +changePassword() void
        +deactivate() void
    }
    
    class CIF {
        -Long cifId
        -String cifNumber
        -String customerName
        -String dateOfBirth
        -String address
        -String panNumber
        -String aadharNumber
        -UserType customerType
        -Boolean delFlg
        -LocalDateTime creationTime
        -LocalDateTime updationTime
        -LocalDateTime modificationTime
        -String createdBy
        -String modifiedBy
        +generateCIF() String
        +updateCustomerInfo() void
        +getAccounts() List~Account~
    }
    
    class Account {
        -Long accountId
        -String accountNumber
        -String accountName
        -AccountType accountType
        -AccountStatus status
        -Boolean dormant
        -BigDecimal balance
        -Long cifId
        -Boolean delFlg
        -LocalDateTime creationTime
        -LocalDateTime updationTime
        -LocalDateTime modificationTime
        -String createdBy
        -String modifiedBy
        +createAccount() void
        +updateBalance() void
        +markDormant() void
        +closeAccount() void
        +reactivate() void
    }
    
    class Beneficiary {
        -Long beneficiaryId
        -Long userId
        -String beneficiaryName
        -String nickName
        -String beneficiaryAccountNumber
        -String ifscSwiftCode
        -String beneficiaryCountry
        -Boolean isActive
        -Boolean delFlg
        -LocalDateTime creationTime
        -LocalDateTime updationTime
        -LocalDateTime modificationTime
        -String createdBy
        -String modifiedBy
        +addBeneficiary() void
        +updateBeneficiary() void
        +deleteBeneficiary() void
        +activateBeneficiary() void
        +deactivateBeneficiary() void
    }
    
    class Payment {
        -Long paymentId
        -Long userId
        -Long beneficiaryId
        -String debitAccount
        -String creditAccount
        -BigDecimal paymentAmount
        -BigDecimal charges
        -BigDecimal totalAmount
        -LocalDateTime transactionDate
        -TransactionStatus transactionStatus
        -PaymentType paymentType
        -String transactionReference
        -String remarks
        -Boolean delFlg
        -LocalDateTime creationTime
        -LocalDateTime updationTime
        -LocalDateTime modificationTime
        -String createdBy
        -String modifiedBy
        +initiatePayment() void
        +processPayment() void
        +cancelPayment() void
        +reversePayment() void
        +getPaymentStatus() TransactionStatus
    }
    
    class AdminApproval {
        -Long approvalId
        -Long userId
        -String adminId
        -String comments
        -UserStatus approvalStatus
        -LocalDateTime approvalDate
        -Boolean delFlg
        -LocalDateTime creationTime
        -LocalDateTime updationTime
        -LocalDateTime modificationTime
        -String createdBy
        -String modifiedBy
        +approve() void
        +reject() void
        +addComments() void
    }
    
    class Notification {
        -Long notificationId
        -Long userId
        -NotificationType type
        -String recipient
        -String subject
        -String message
        -Boolean sent
        -LocalDateTime sentTime
        -Boolean delFlg
        -LocalDateTime creationTime
        -String createdBy
        +send() void
        +retry() void
    }

    %% Service Interfaces
    class IUserService {
        <<interface>>
        +registerUser(UserDTO) User
        +getUserById(Long) User
        +updateUser(Long, UserDTO) User
        +deleteUser(Long) void
        +authenticateUser(String, String) User
        +getUserByCIF(Long) User
    }
    
    class ICIFService {
        <<interface>>
        +createCIF(CIFDTO) CIF
        +generateCIFNumber() String
        +getCIFById(Long) CIF
        +updateCIF(Long, CIFDTO) CIF
        +getCIFWithAccounts(Long) CIF
        +deleteCIF(Long) void
    }
    
    class IAccountService {
        <<interface>>
        +createAccount(AccountDTO) Account
        +getAccountById(Long) Account
        +getAccountsByCIF(Long) List~Account~
        +updateAccount(Long, AccountDTO) Account
        +markAccountDormant(Long) void
        +closeAccount(Long) void
        +deleteAccount(Long) void
    }
    
    class IBeneficiaryService {
        <<interface>>
        +addBeneficiary(BeneficiaryDTO) Beneficiary
        +getBeneficiaryById(Long) Beneficiary
        +getBeneficiariesByUser(Long) List~Beneficiary~
        +updateBeneficiary(Long, BeneficiaryDTO) Beneficiary
        +deleteBeneficiary(Long) void
        +activateBeneficiary(Long) void
        +deactivateBeneficiary(Long) void
    }
    
    class IPaymentService {
        <<interface>>
        +initiatePayment(PaymentDTO) Payment
        +processPayment(Long) void
        +getPaymentById(Long) Payment
        +getPaymentsByUser(Long) List~Payment~
        +cancelPayment(Long) void
        +reversePayment(Long) void
        +getPaymentHistory(Long, LocalDate, LocalDate) List~Payment~
    }
    
    class IApprovalService {
        <<interface>>
        +submitForApproval(Long) AdminApproval
        +approveUser(Long, String) void
        +rejectUser(Long, String) void
        +getPendingApprovals() List~AdminApproval~
        +getApprovalByUserId(Long) AdminApproval
    }
    
    class INotificationService {
        <<interface>>
        +sendNotification(NotificationDTO) void
        +sendEmailNotification(Long, String) void
        +sendSMSNotification(Long, String) void
        +resendNotification(Long) void
    }

    %% Repository Interfaces
    class IUserRepository {
        <<interface>>
        +save(User) User
        +findById(Long) Optional~User~
        +findByLoginId(String) Optional~User~
        +findByCifId(Long) Optional~User~
        +findByEmailAndDelFlgFalse(String) Optional~User~
        +existsByEmail(String) boolean
        +delete(User) void
    }
    
    class ICIFRepository {
        <<interface>>
        +save(CIF) CIF
        +findById(Long) Optional~CIF~
        +findByCifNumber(String) Optional~CIF~
        +findByDelFlgFalse() List~CIF~
        +delete(CIF) void
    }
    
    class IAccountRepository {
        <<interface>>
        +save(Account) Account
        +findById(Long) Optional~Account~
        +findByCifIdAndDelFlgFalse(Long) List~Account~
        +findByAccountNumber(String) Optional~Account~
        +findByStatusAndDelFlgFalse(AccountStatus) List~Account~
        +delete(Account) void
    }
    
    class IBeneficiaryRepository {
        <<interface>>
        +save(Beneficiary) Beneficiary
        +findById(Long) Optional~Beneficiary~
        +findByUserIdAndDelFlgFalse(Long) List~Beneficiary~
        +findByUserIdAndIsActiveTrue(Long) List~Beneficiary~
        +findByBeneficiaryAccountNumber(String) Optional~Beneficiary~
        +delete(Beneficiary) void
    }
    
    class IPaymentRepository {
        <<interface>>
        +save(Payment) Payment
        +findById(Long) Optional~Payment~
        +findByUserIdAndDelFlgFalse(Long) List~Payment~
        +findByTransactionStatus(TransactionStatus) List~Payment~
        +findByUserIdAndTransactionDateBetween(Long, LocalDateTime, LocalDateTime) List~Payment~
        +findByDebitAccount(String) List~Payment~
        +delete(Payment) void
    }
    
    class IApprovalRepository {
        <<interface>>
        +save(AdminApproval) AdminApproval
        +findById(Long) Optional~AdminApproval~
        +findByUserId(Long) Optional~AdminApproval~
        +findByApprovalStatus(UserStatus) List~AdminApproval~
        +delete(AdminApproval) void
    }
    
    class INotificationRepository {
        <<interface>>
        +save(Notification) Notification
        +findById(Long) Optional~Notification~
        +findByUserIdAndDelFlgFalse(Long) List~Notification~
        +findBySentFalse() List~Notification~
    }

    %% Relationships - Entity to Entity
    User "1" --> "1" CIF : has
    User "1" --> "0..*" Beneficiary : manages
    User "1" --> "0..*" Payment : initiates
    User "1" --> "0..*" AdminApproval : undergoes
    User "1" --> "0..*" Notification : receives
    CIF "1" --> "0..*" Account : owns
    Beneficiary "1" --> "0..*" Payment : receives
    
    %% Relationships - Service to Repository
    IUserService ..> IUserRepository : uses
    IUserService ..> ICIFService : uses
    IUserService ..> INotificationService : uses
    
    ICIFService ..> ICIFRepository : uses
    ICIFService ..> IAccountService : uses
    
    IAccountService ..> IAccountRepository : uses
    
    IBeneficiaryService ..> IBeneficiaryRepository : uses
    IBeneficiaryService ..> IUserService : uses
    
    IPaymentService ..> IPaymentRepository : uses
    IPaymentService ..> IUserService : uses
    IPaymentService ..> IBeneficiaryService : uses
    IPaymentService ..> IAccountService : uses
    IPaymentService ..> INotificationService : uses
    
    IApprovalService ..> IApprovalRepository : uses
    IApprovalService ..> IUserService : uses
    IApprovalService ..> ICIFService : uses
    IApprovalService ..> INotificationService : uses
    
    INotificationService ..> INotificationRepository : uses
    
    %% Enum relationships
    User --> UserType : uses
    User --> UserStatus : uses
    CIF --> UserType : uses
    Account --> AccountType : uses
    Account --> AccountStatus : uses
    Payment --> TransactionStatus : uses
    Payment --> PaymentType : uses
    AdminApproval --> UserStatus : uses
    Notification --> NotificationType : uses