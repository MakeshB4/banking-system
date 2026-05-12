graph TB
    Client(["👤 Client"])

    subgraph AWS["AWS Cloud"]

        subgraph VPC["VPC — 10.0.0.0/16"]

            subgraph Public["Public Subnet"]
                ALB["Application Load Balancer\nHTTPS :443"]
            end

            subgraph Private["Private Subnet"]

                subgraph UA["useraccounts-service\nAuto Scaling  Min:1 · Max:4  |  CPU > 60% → Scale Out"]
                    UA1["EC2 t3.medium :8081"]
                    UA2["EC2 t3.medium :8081"]
                end

                subgraph PAY["payments-service\nAuto Scaling  Min:1 · Max:4  |  CPU > 60% → Scale Out"]
                    PAY1["EC2 t3.medium :8082"]
                    PAY2["EC2 t3.medium :8082"]
                end

                subgraph NOT["notifications-service\nAuto Scaling  Min:1 · Max:3  |  CPU > 70% → Scale Out"]
                    NOT1["EC2 t3.small :8083"]
                end

            end

            subgraph DB["DB Subnet"]
                RDS1[("useraccounts_db\nPostgreSQL · Multi-AZ")]
                RDS2[("payments_db\nPostgreSQL · Multi-AZ")]
                RDS3[("notifications_db\nPostgreSQL · Multi-AZ")]
            end

        end

        CW["📊 CloudWatch\nCPU Alarms · Auto Scaling"]
        S3[("S3\nJAR Artifacts")]

    end

    Client -->|HTTPS| ALB

    ALB -->|"/api/v1/users/**"| UA1
    ALB -->|"/api/v1/payments/**"| PAY1
    ALB -->|"/api/v1/notifications/**"| NOT1

    UA1 -->|"Internal REST"| PAY1
    UA1 -->|"Internal REST"| NOT1
    PAY1 -->|"Internal REST"| NOT1

    UA1 & UA2 --> RDS1
    PAY1 & PAY2 --> RDS2
    NOT1 --> RDS3

    CW -->|"Scale Out / In"| UA
    CW -->|"Scale Out / In"| PAY
    CW -->|"Scale Out / In"| NOT

    S3 -.->|"Deploy JAR"| UA
    S3 -.->|"Deploy JAR"| PAY
    S3 -.->|"Deploy JAR"| NOT

    style AWS fill:#fffde7,stroke:#f9a825
    style VPC fill:#f3e5f5,stroke:#7b1fa2
    style Public fill:#e8f5e9,stroke:#388e3c
    style Private fill:#e3f2fd,stroke:#1565c0
    style DB fill:#fce4ec,stroke:#c62828
    style UA fill:#bbdefb,stroke:#1565c0
    style PAY fill:#bbdefb,stroke:#1565c0
    style NOT fill:#bbdefb,stroke:#1565c0