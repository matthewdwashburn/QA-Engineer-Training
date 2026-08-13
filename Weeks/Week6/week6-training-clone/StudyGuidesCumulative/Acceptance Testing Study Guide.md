# Acceptance Testing Study Guide

## Acceptance Testing Overview

### Acceptance Testing Introduction
**Acceptance Testing** is a process that allows for validating the application in question actually meets the requirements of the application owner and the end users to ensure the successful release of the application. This process not only creates confidence in the system as a whole but also ensures that the application is ready for release to users. While system defect identification is not the primary focus, discovering numerous defects may indicate substantial foundational issues within the application

### Typical Defects
**Acceptance Testing** can identify various defects, such as business rules not being implemented or interpreted correctly, which may result in the system not meeting contract or regulatory requirements. It can also uncover issues related to poor performance and non-functional failures, like images not loading correctly or scaling improperly, and page loading times being excessively long. Additionally, acceptance testing can reveal problems with accessibility features not working correctly, which can significantly impact the user experience for those that require their use

## Acceptance Testing Types

### User Acceptance Testing
**User Acceptance Testing** (UAT) ensures that the application effectively meets all the requirements necessary for the end user. This phase of testing validates that the system performs as expected and satisfies the user's needs, confirming that the application is ready for deployment. This type of testing is difficult to automate, since much of the content being assessed is subjective in nature

### Useability Testing
**Usability Testing** involves evaluating how natural and intuitive users find the application being tested. This type of testing aims to identify any areas where users may struggle, indicating potential design defects. If multiple end users encounter difficulties with the same part of the application, it suggests a flaw in the design that needs to be addressed. Ideally, the feedback from users would indicate that the application is intuitive and easy to navigate, ensuring a smooth and satisfactory user experience

### Alpha/Beta Testing
**Alpha testing** is the initial round of **acceptance testing** performed on software. This phase typically involves internal personnel, not testers, who test the application to identify any major issues before it is released to a broader audience. **Beta testing** follows, involving a limited release of the application to specific end-users outside the internal team. This phase allows real-world users to test the application, providing valuable feedback and identifying any remaining defects that need to be addressed before the final release. The benefit of both rounds of testing is that it gets fresh eyes on the application, all of which bring a different perspective and differing expectations to the application. The unique perspectives can help with detecting novel defects the testing team missed

### Regulatory Testing
**Contractual and Regulatory Acceptance Testing** involves ensuring that an application meets all legal and contractual requirements. This type of testing is often conducted in the presence of an authority who witnesses the application in action to verify compliance. For example, an auditor from the SEC might be present to confirm that financial records within an application cannot be illegally edited, ensuring the system adheres to regulatory standards

### Operational Acceptance Testing
**Operational Acceptance Testing (OAT)** ensures that the application can handle internal administrative tasks successfully. This phase of testing is focused on the auxiliary parts of a service, such as verifying that data is appropriately backed up, logs are generated correctly, and the processes for installing and uninstalling the application work as intended. Additionally, **OAT** addresses security vulnerabilities, ensuring that the application is secure and ready for operational use

### Smoke Testing
**Smoke Testing** is a subset of **acceptance testing** performed on the entire system to verify basic and critical functionality. The idea is to identify any glaring defects early on, based on the principle "if there's smoke, there's fire." This means if basic functions fail, there's no point in testing more advanced features. **Smoke Testing** is conducted first in a build to avoid wasting time on further testing if fundamental issues are present. For example, in a calculator application, **Smoke Testing** would involve checking that pressing the ON button starts the calculator. If this basic function fails, there's no need to test other features. Essentially, **Smoke Testing** acts as a general health check-up for the system

### Sanity Testing
**Sanity testing** is a subset of regression testing focused on specific features. It performs a "sanity check" to ensure that a new feature functions as intended: it can be thought of it as a **smoke test** for regression testing: if the new feature or update has defects, then older tests that interact with this new feature are likely to fail as well. **Sanity Testing** can be compared to a specialized health check-up, verifying that the new additions to the system do not disrupt existing functionality