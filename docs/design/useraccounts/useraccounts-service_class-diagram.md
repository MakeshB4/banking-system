classDiagram

%% ── Enumerations ──────────────────────────────

class CustomerStatus {
    <<enumeration>>
    PENDING
    ACTIVE
    INACTIVE
    SUSPENDED
    BLOCKED
    REJECTED
    CLOSED
}

class Gender {
    <<enumeration>>
    MALE
    FEMALE
    OTHER
}

class CustomerType {
    <<enumeration>>
    INDIVIDUAL
    CORPORATE
    PARTNERSHIP
    TRUST
    GOVERNMENT
}

class CifStatus {
    <<enumeration>>
    PENDING
    ACTIVE
    INACTIVE
    SUSPENDED
    CLOSED
    REJECTED
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

class IdProofType {
    <<enumeration>>
    AADHAAR
    PASSPORT
    DRIVING_LICENSE
    VOTER_ID
    NATIONAL_ID
}

class AddressProofType {
    <<enumeration>>
    AADHAAR
    PASSPORT
    UTILITY_BILL
    BANK_STATEMENT
    RENTAL_AGREEMENT
    VOTER_ID
}

class AddressType {
    <<enumeration>>
    PERMANENT
    TEMPORARY
    OFFICE
}

%% ── Entities ──────────────────────────────────

class Customer {
    -Long id
    -String firstName
    -String middleName
    -String lastName
    -LocalDate dateOfBirth
    -Gender gender
    -String email
    -String phoneNumber
    -String alternatePhone
    -String panNumber
    -String aadharNumber
    -String occupation
    -BigDecimal annualIncome
    -String nationality
    -CustomerStatus status
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    -String createdBy
    -String updatedBy
    -Boolean delFlg
    +register() void
    +updateProfile() void
    +deactivate() void
}

class CIF {
    -Long id
    -Long customerNumber
    -CustomerType customerType
    -CifStatus cifStatus
    -String riskCategory
    -LocalDate activationDate
    -LocalDate closureDate
    -LocalDate lastReviewDate
    -LocalDate nextReviewDate
    -String approvedBy
    -LocalDateTime approvedDate
    -String remarks
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    -String createdBy
    -String updatedBy
    -Boolean delFlg
    +generateCIF() Long
    +activate(String approvedBy) void
    +reject(String remarks) void
    +review() void
}

class Account {
    -Long id
    -Long cifId
    -AccountType accountType
    -BigDecimal balance
    -String currency
    -String branchCode
    -String ifscCode
    -LocalDate openingDate
    -AccountStatus status
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    -String createdBy
    -String updatedBy
    -Boolean delFlg
    +createAccount() void
    +updateBalance(BigDecimal amount) void
    +markDormant() void
    +closeAccount() void
    +reactivate() void
    +getBalance() BigDecimal
}

class KYCDetails {
    -Long id
    -Long customerId
    -IdProofType idProofType
    -String idProofNumber
    -LocalDate idProofIssueDate
    -LocalDate idProofExpiryDate
    -String idProofDocumentPath
    -AddressProofType addressProofType
    -String addressProofNumber
    -String addressProofDocumentPath
    -String panNumber
    -String panDocumentPath
    -String photoPath
    -String signaturePath
    -String verifiedBy
    -LocalDateTime verifiedDate
    -String kycRemarks
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    -String createdBy
    -String updatedBy
    -Boolean delFlg
    +verify(String verifiedBy) void
    +updateDocuments() void
}

class Address {
    -Long id
    -Long customerId
    -String addressLine1
    -String addressLine2
    -String landmark
    -String city
    -String state
    -String country
    -String postalCode
    -AddressType addressType
    -Boolean isCommunicationAddress
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    -String createdBy
    -String updatedBy
    -Boolean delFlg
    +updateAddress() void
}

class UserInfo {
    -Long id
    -String username
    -String password
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    -String createdBy
    -String updatedBy
    -Boolean delFlg
    +register() void
    +changePassword() void
    +deactivate() void
}

class UserRole {
    -Long id
    -String name
}

class AdminApproval {
    -Long approvalId
    -Long customerId
    -String adminId
    -String comments
    -String approvalStatus
    -LocalDateTime approvalDate
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    -String createdBy
    -String updatedBy
    -Boolean delFlg
    +approve() void
    +reject() void
    +addComments(String comment) void
}

%% ── Service Interfaces ────────────────────────

class IUserRegistrationService {
    <<interface>>
    +registerUser(UserRegistrationRequest) UserRegistrationResponse
    +getPendingCustomerById(Long customerNumber) PendingCustomerResponse
    +updateUser(UserModificationRequest) UserRegistrationResponse
}

class ICIFService {
    <<interface>>
    +createCIF(Customer) CIF
    +generateCustomerNumber() Long
    +getCIFByCustomerNumber(Long) CIF
    +activateCIF(Long cifId, String approvedBy) void
    +rejectCIF(Long cifId, String remarks) void
}

class IAccountService {
    <<interface>>
    +createAccount(AccountRequest, CIF) Account
    +getAccountsByCIF(Long customerNumber) List~AccountResponse~
    +getAccount(Long accountNumber) AccountResponse
    +getAccountBalance(Long accountNumber) BigDecimal
}

class IApprovalService {
    <<interface>>
    +submitForApproval(Long customerId) AdminApproval
    +approveCustomer(Long customerId, String adminId) void
    +rejectCustomer(Long customerId, String remarks) void
    +getPendingApprovals() List~AdminApproval~
}

%% ── Repository Interfaces ─────────────────────

class ICustomerRepository {
    <<interface>>
    +save(Customer) Customer
    +findById(Long) Optional~Customer~
    +findByEmail(String) Optional~Customer~
    +existsByPanNumber(String) boolean
    +existsByAadharNumber(String) boolean
}

class ICIFRepository {
    <<interface>>
    +save(CIF) CIF
    +findById(Long) Optional~CIF~
    +findByCustomerNumber(Long) Optional~CIF~
    +existsByCustomerNumber(Long) boolean
}

class IAccountRepository {
    <<interface>>
    +save(Account) Account
    +findById(Long) Optional~Account~
    +findByCifId(Long) List~Account~
    +findByAccountNumber(String) Optional~Account~
}

class IKYCRepository {
    <<interface>>
    +save(KYCDetails) KYCDetails
    +findByCustomerId(Long) Optional~KYCDetails~
    +existsByIdProofNumber(String) boolean
    +existsByPanNumber(String) boolean
}

class IAddressRepository {
    <<interface>>
    +save(Address) Address
    +findByCustomerId(Long) List~Address~
}

class IUserRepository {
    <<interface>>
    +save(UserInfo) UserInfo
    +findByUsername(String) Optional~UserInfo~
    +existsByUsername(String) boolean
}

class IApprovalRepository {
    <<interface>>
    +save(AdminApproval) AdminApproval
    +findByCustomerId(Long) Optional~AdminApproval~
    +findByApprovalStatus(String) List~AdminApproval~
}

%% ── Entity Relationships ──────────────────────

Customer "1" --> "1"     CIF           : linked to
Customer "1" --> "1"     KYCDetails    : verified by
Customer "1" --> "1..*"  Address       : lives at
Customer "1" --> "0..*"  AdminApproval : reviewed via
CIF      "1" --> "1..*"  Account       : holds
UserInfo "0..*" --> "1..*" UserRole    : assigned

%% ── Service → Repository ──────────────────────

IUserRegistrationService ..> ICustomerRepository : uses
IUserRegistrationService ..> IUserRepository : uses
IUserRegistrationService ..> IAddressRepository : uses
IUserRegistrationService ..> IKYCRepository : uses
IUserRegistrationService ..> ICIFService         : uses
IUserRegistrationService ..> IAccountService     : uses

ICIFService     ..> ICIFRepository     : uses
IAccountService ..> IAccountRepository : uses
IApprovalService ..> IApprovalRepository : uses
IApprovalService ..> IUserRegistrationService : uses
UserService ..> UserRepository : uses

%% ── Enum Usages ───────────────────────────────

Customer    --> CustomerStatus  : status
Customer    --> Gender          : gender
CIF         --> CustomerType    : customerType
CIF         --> CifStatus       : cifStatus
Account     --> AccountType     : accountType
Account     --> AccountStatus   : status
KYCDetails  --> IdProofType     : idProofType
KYCDetails  --> AddressProofType : addressProofType
Address     --> AddressType     : addressType