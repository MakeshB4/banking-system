graph TD
    DEV(["👨‍💻 Developer\npushes code"])

    subgraph GH["  GitHub Repository  "]
        PUSH["git push\nmain / feature branch"]
        PR["Pull Request\nOpened"]
    end

    subgraph CICD["  GitHub Actions — CI Pipeline  "]

        subgraph BUILD["  Stage 1 — Build  "]
            CHECKOUT["Checkout Code\ngit checkout"]
            JDK["Setup JDK 21\namazon-corretto"]
            MVN["mvn clean package\n-DskipTests"]
        end

        subgraph TEST["  Stage 2 — Tests  "]
            UNIT["Unit Tests\nmvn test"]
            INTG["Integration Tests\nmvn verify"]
            COV["Code Coverage\nJacoco Report\nMin 80%"]
        end

        subgraph QUALITY["  Stage 3 — Code Quality  "]
            SONAR["SonarQube Analysis\nCode Smells · Bugs · Duplications"]
            QGATE["Quality Gate Check\nSonar — Pass / Fail"]
        end

        subgraph SECURITY["  Stage 4 — Security Scan  "]
            OWASP["OWASP Dependency Check\nCVE Vulnerability Scan"]
            TRIVY["Trivy\nContainer & JAR Scan"]
            SAST["SAST Scan\nCodeQL Analysis"]
        end

        subgraph PACKAGE["  Stage 5 — Package & Push  "]
            JAR["Build Final JAR\nmvn package"]
            UPLOAD["Upload to S3\naws s3 cp .jar s3://banking-deployments"]
            VERSION["Tag Version\ngit tag v1.x.x"]
        end

    end

    subgraph CD["  GitHub Actions — CD Pipeline  "]
        subgraph DEPLOY["  Stage 6 — Deploy to AWS  "]
            EBUSR["Deploy useraccounts-service\naws elasticbeanstalk\ncreate-application-version"]
            EBPAY["Deploy payments-service\naws elasticbeanstalk\ncreate-application-version"]
            EBNOT["Deploy notifications-service\naws elasticbeanstalk\ncreate-application-version"]
        end

        subgraph VERIFY["  Stage 7 — Post Deploy  "]
            HEALTH["Health Check\nGET /actuator/health"]
            SMOKE["Smoke Tests\nBasic API Tests"]
            NOTIFY["Notify Team\nSlack / Email Alert"]
        end
    end

    DEV --> PUSH
    PUSH --> PR
    PR --> CHECKOUT

    CHECKOUT --> JDK
    JDK --> MVN

    MVN --> UNIT
    UNIT --> INTG
    INTG --> COV

    COV -->|Pass| SONAR
    COV -->|Fail — Pipeline Stops| FAIL1(["❌ Coverage\nBelow 80%"])

    SONAR --> QGATE
    QGATE -->|Pass| OWASP
    QGATE -->|Fail — Pipeline Stops| FAIL2(["❌ Quality Gate\nFailed"])

    OWASP --> TRIVY
    TRIVY --> SAST
    SAST -->|No Critical CVE| JAR
    SAST -->|Critical Found — Pipeline Stops| FAIL3(["❌ Vulnerability\nFound"])

    JAR --> UPLOAD
    UPLOAD --> VERSION

    VERSION -->|Triggers CD| EBUSR
    VERSION --> EBPAY
    VERSION --> EBNOT

    EBUSR --> HEALTH
    EBPAY --> HEALTH
    EBNOT --> HEALTH

    HEALTH --> SMOKE
    SMOKE -->|All Pass| NOTIFY
    SMOKE -->|Fail| ROLLBACK(["🔄 Auto Rollback\nPrevious Version"])