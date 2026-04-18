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
        +getAccountBalance(Long) BigDecimal
    }
    
    class IApprovalService {
        <<interface>>
        +submitForApproval(Long) AdminApproval
        +approveUser(Long, String) void
        +rejectUser(Long, String) void
        +getPendingApprovals() List~AdminApproval~
        +getApprovalByUserId(Long) AdminApproval
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
    
    class IApprovalRepository {
        <<interface>>
        +save(AdminApproval) AdminApproval
        +findById(Long) Optional~AdminApproval~
        +findByUserId(Long) Optional~AdminApproval~
        +findByApprovalStatus(UserStatus) List~AdminApproval~
        +delete(AdminApproval) void
    }

    %% Relationships - Entity to Entity
    User "1" --> "1" CIF : has
    User "1" --> "0..*" AdminApproval : undergoes
    CIF "1" --> "0..*" Account : owns
    
    %% Relationships - Service to Repository
    IUserService ..> IUserRepository : uses
    IUserService ..> ICIFService : uses
    
    ICIFService ..> ICIFRepository : uses
    ICIFService ..> IAccountService : uses
    
    IAccountService ..> IAccountRepository : uses
    
    IApprovalService ..> IApprovalRepository : uses
    IApprovalService ..> IUserService : uses
    IApprovalService ..> ICIFService : uses
    
    %% Enum relationships
    User --> UserType : uses
    User --> UserStatus : uses
    CIF --> UserType : uses
    Account --> AccountType : uses
    Account --> AccountStatus : uses
    AdminApproval --> UserStatus : uses