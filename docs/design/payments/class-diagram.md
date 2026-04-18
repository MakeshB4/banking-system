classDiagram
%% Enums
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

    %% Service Interfaces
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

    %% Repository Interfaces
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

    %% Relationships - Entity to Entity
    Beneficiary "1" --> "0..*" Payment : receives
    
    %% Relationships - Service to Repository
    IBeneficiaryService ..> IBeneficiaryRepository : uses
    
    IPaymentService ..> IPaymentRepository : uses
    IPaymentService ..> IBeneficiaryService : uses
    
    %% Enum relationships
    Payment --> TransactionStatus : uses
    Payment --> PaymentType : uses