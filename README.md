# Privacy-Preserving Employee Database

This project implements a **privacy-preserving employee database** using **Java**, **MySQL**, **Searchable Encryption**, **Partial Homomorphic Encryption**, and **Order-Preserving Encryption**.

The goal is to allow a company to outsource its employee database to an untrusted MySQL server while keeping all employee information encrypted. The server stores only encrypted data and can still process selected operations over encrypted records.

---

## Project Goal

The system protects employee data against an **honest-but-curious server**.

The server is allowed to store data and execute queries, but it must not learn plaintext employee information such as:

- employee IDs;
- employee names;
- departments;
- ages;
- salaries;
- emails;
- phone numbers;
- bonus eligibility.

Only the client owns the cryptographic keys and can decrypt the results.

---

## Technologies Used

- Java 17
- Maven
- MySQL 8.x
- JDBC MySQL Connector
- AES encryption
- HMAC-SHA256 searchable indexes
- Paillier homomorphic encryption
- Order-Preserving Encryption for ordered queries
- ECDSA digital signatures
- HMAC-SHA256 integrity verification

---

## Dataset

The project uses the provided employee dataset:

```text
Dataset-Emp-Database.csv
```

The dataset contains the following columns:

```text
employeeID
First Name
Last Name
Full Name
DateofBirth
Age
Email
Contact Phone Number
Personal Phone Number
JobTitle
DepartmentID
HireDate
EmploiementType
Salary
SalaryBand
BonusEligibiity
```

---

## Cryptographic Design

Different cryptographic techniques are used depending on the operation required for each attribute.

| Attribute         | Protection Used         | Purpose                                     |
| ----------------- | ----------------------- | ------------------------------------------- |
| Employee ID       | AES + HMAC-SHA256 index | Confidentiality and exact search            |
| Full Name         | AES + HMAC-SHA256 index | Confidentiality and exact search            |
| Department ID     | AES + HMAC-SHA256 index | Confidentiality and department search       |
| Age               | AES + OPE               | Confidentiality and age ordering            |
| Salary            | AES + Paillier + OPE    | Retrieval, encrypted computations, ordering |
| Bonus Eligibility | AES + HMAC-SHA256 index | Confidentiality and eligibility search      |
| Other fields      | AES                     | Confidential storage                        |
| Whole record      | HMAC-SHA256 + ECDSA     | Integrity and authenticity                  |

---

## Supported Operations

The system supports the 11 required operations:

1. Search and retrieve registered information by Employee Identification.
2. Search and retrieve registered information by Employee Full Name.
3. Search and retrieve employees ordered by Salary.
4. Search and retrieve employees belonging to a given Department.
5. Search and retrieve the employee with the highest Salary.
6. Compare whether an employee has a higher Salary than another, given both full names.
7. Search and retrieve employees ordered by Age.
8. Obtain the salary of an employee converted to US Dollars.
9. Find all employees in a given Department and compute the encrypted payroll sum.
10. Find the oldest employee and retrieve all registered information.
11. For all employees with Bonus Eligibility, compute the 25% salary bonus.

---

## MySQL Setup

Create the database:

```sql
CREATE DATABASE IF NOT EXISTS company_db;
```

Then open:

```text
src/main/java/pa2/config/DbConfig.java
```

Edit the MySQL configuration:

```java
public static final String HOST = "localhost";
public static final int PORT = 3306;
public static final String DATABASE = "company_db";
public static final String USER = "root";
public static final String PASSWORD = "your_password";
```

Replace `"your_password"` with your MySQL password.

---

## Running MySQL with Docker

If you do not have a local MySQL installation, you can use Docker:

```bash
docker run --name pa2-mysql \
  -e MYSQL_ROOT_PASSWORD=pa2password \
  -e MYSQL_DATABASE=company_db \
  -p 3306:3306 \
  -d mysql:8.4
```

Then set this in `DbConfig.java`:

```java
public static final String PASSWORD = "pa2password";
```

To stop MySQL:

```bash
docker stop pa2-mysql
```

To start it again:

```bash
docker start pa2-mysql
```

---

## Build the Project

From the project root, run:

```bash
mvn clean package
```

---

## Bootstrap the Encrypted Database

The bootstrap process reads the plaintext CSV dataset, encrypts all employee records, generates indexes, and uploads the encrypted data to MySQL.

Run:

```bash
mvn exec:java -Dexec.mainClass="pa2.client.Bootstrap"
```

The bootstrap creates the encrypted MySQL table:

```text
employees_encrypted
```

It also creates the client-side key file:

```text
client_keys/client.properties
```

This file contains the cryptographic keys and must remain only on the client side.

---

## Run the Interactive Menu

To manually test each operation, run:

```bash
mvn exec:java -Dexec.mainClass="pa2.client.InteractiveClientApp"
```

The terminal menu shows:

```text
====================================================
 Privacy-Preserving Employee Database - MySQL Client
====================================================
1  - Search and retrieve by Employee Identification
2  - Search and retrieve by Employee Full Name
3  - Search and retrieve employees ordered by Salary
4  - Search and retrieve employees by DepartmentID
5  - Search and retrieve employee with highest Salary
6  - Compare salaries between two employees by Full Name
7  - Search and retrieve employees ordered by Age
8  - Convert salary of an employee to USD
9  - Department employees + encrypted payroll sum
10 - Find oldest employee
11 - Compute 25% bonus for Bonus Eligibility employees
0  - Exit
====================================================
```

Example values to test:

```text
Employee ID: EMP1001
Full Name: Emma Johnson
DepartmentID: DPT109
Exchange rate: 110
```

The exchange rate is scaled by 100.

For example:

```text
110 means 1.10 USD/EUR
105 means 1.05 USD/EUR
120 means 1.20 USD/EUR
```

---

## Run the Fixed 11 Tests

To automatically execute all 11 required operations, run:

```bash
mvn exec:java -Dexec.mainClass="pa2.client.FixedTestsClientApp"
```

This client uses fixed test values such as:

```text
Employee ID: EMP1001
Full Name A: Emma Johnson
Full Name B: John Smith
DepartmentID: DPT109
Exchange rate: 110
```

The fixed test client also prints the latency of each operation.

---

## Final Notes

This project demonstrates how different cryptographic mechanisms can be combined to support practical encrypted database operations. AES protects stored attributes, HMAC indexes enable exact searches, Paillier enables encrypted salary computations, and OPE enables order-based operations over encrypted data.
