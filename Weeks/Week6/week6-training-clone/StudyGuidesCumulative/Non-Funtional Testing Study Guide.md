# Non-Functional Testing Study Guide

## System Testing Overview

### System Testing Introduction
**System Testing** involves validating functional and non-functional requirements are met when all systems are working together in an application. This includes the database, back end, front end, and supporting cloud infrastructure. Good **System Testing** can reveal novel defects that are undetectable by lower level testing, and a robust **System Testing** suite helps create confidence in the system as a whole

### End to End Testing
A comprehensive form of **System Testing** is **End to End Testing**. In this form of testing, a feature is tested from the start of its functionality all the way to the end, usually done so from the end-user perspective. This allows for validating all pieces of the application are working together correctly, and can reveal defects that lower levels of testing might miss

### Typical Defects
Common defects discovered during system testing include incorrect or unexpected functional behavior, where the system does not conform to the specified requirements. This often results in an inability to complete all parts of end-to-end tasks effectively. Additionally, the system's performance may fail to meet minimal requirements, and the application may not adhere to essential security standards. Furthermore, the cloud environment might not scale appropriately, leading to potential issues in handling increased workloads

## System Testing : Non-Functional Testing

### Performance Testing
**Performance Testing** is a type of testing focused on evaluating the performance of an application rather than verifying its functional correctness. The primary goal is to ensure that the application meets the performance expectations set by stakeholders. Key metrics such as responsiveness, which measures how quickly the application responds to user inputs or requests, and throughput, which assesses the amount of data the application can process within a given time frame, are tested. This helps validate that the application can handle its expected load and perform efficiently under various conditions, as well as to identify and address performance bottlenecks that could impact user experience. **Performance Testing** is closely tied to Service Level Agreements (SLAs), as companies often commit to specific performance standards in their SLAs. By conducting thorough performance tests, organizations can verify that their systems are capable of delivering the promised levels of performance

### Endurance Testing
**Endurance Testing** involves evaluating how long an application can sustain normal load conditions over an extended period. This type of testing can reveal defects that only become apparent over time, such as memory leaks, poorly configured log management, and other issues that may not be immediately evident during shorter testing periods

### Load Testing
**Load Testing** involves measuring the performance of an application under varying levels of traffic. This type of testing is particularly useful for services deployed in auto-scaling environments in the cloud, as it can reveal defects in the deployment environment. **Load Testing** is distinct from **Endurance Testing** in that it focuses on the application's behavior under different load conditions, rather than its ability to sustain performance over an extended period. While **Endurance Testing** aims to identify issues that emerge over time, such as memory leaks and poorly configured log management, **Load Testing** is designed to uncover how the application handles spikes and fluctuations in traffic, ensuring that it can scale appropriately and maintain performance during peak usage

### Stress Testing
**Stress Testing** involves continually increasing the load on a service until it reaches its capacity, with the goal of identifying the application's breaking point. This type of testing is distinct from load testing and endurance testing; while load testing measures performance under varying traffic levels and endurance testing evaluates how long an application can sustain normal load conditions, stress testing pushes the application beyond its normal operational limits to determine its maximum capacity. By identifying the breaking point, stress testing helps uncover potential weaknesses and ensures that the application can handle extreme conditions without failing

### Compatibility Testing
**Compatibility Testing** involves verifying that a feature or application functions consistently across different platforms, operating systems, browsers, and devices. This type of testing ensures that users have a uniform experience regardless of the environment they are using. **Compatibility Testing** detect issues that arise due to variations in hardware, software, and configurations. By conducting thorough compatibility tests, developers can ensure that their applications are accessible and functional for a wide range of users and reduce the risk of platform-specific defects

### Penetration Testing
**Penetration Testing** involves validating the security of an application to ensure it is robust enough to withstand well-known attacks. This type of testing simulates real-world cyberattacks to identify vulnerabilities and weaknesses in the application's security. By conducting **Penetration Tests**, organizations can discover and address security flaws before they can be exploited by malicious actors, safeguarding sensitive data, maintaining user trust, and ensuring compliance with security standards and regulations