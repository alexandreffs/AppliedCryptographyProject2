# Privacy-Preserving Employee Database - MySQL Base

This is a Java/MySQL base for the PA2 project.

It is adapted to the uploaded dataset columns:

```text
employeeID, First Name, Last Name, Full Name, DateofBirth, Age, Email,
Contact Phone Number, Personal Phone Number, JobTitle, DepartmentID,
HireDate, EmploiementType, Salary, SalaryBand, BonusEligibiity
```

## What is implemented

- Dataset loading with the correct CSV columns.
- AES-GCM encryption for normal field retrieval.
- HMAC-SHA256 indexes for exact searches:
  - employeeID
  - Full Name
  - DepartmentID
  - BonusEligibiity
- Paillier encryption for salary arithmetic:
  - department payroll sum
  - 25% bonus calculation
  - salary conversion using a scaled exchange rate
- Order-preserving placeholder for:
  - salary ordering
  - highest salary
  - age ordering
  - oldest employee
- HMAC integrity verification.
- ECDSA signature verification.
- MySQL table creation and insertion.
- Demo client for the 11 required operations.

## Important warning about OPE

`OPECrypto.java` is a placeholder that preserves order so you can test the database operations.
It is not a real Boldyreva OPE implementation. In the report, you must say this leaks full order.
If your teacher requires the exact Boldyreva algorithm, replace this class with a real implementation.

## MySQL setup

Open MySQL and run:

```sql
CREATE DATABASE IF NOT EXISTS company_db;
```

Then edit:

```text
src/main/java/pa2/config/DbConfig.java
```

Change:

```java
public static final String PASSWORD = "your_password";
```

to your real MySQL root password.

## Where to put the dataset

Put the file here:

```text
Dataset-Emp-Database.csv
```

at the project root, next to `pom.xml`.

## Build

```bash
mvn clean package
```

## Run bootstrap

This reads the plaintext dataset, encrypts it, creates indexes, and uploads encrypted data to MySQL.

```bash
mvn exec:java -Dexec.mainClass="pa2.client.Bootstrap"
```

Or with an explicit dataset path:

```bash
mvn exec:java -Dexec.mainClass="pa2.client.Bootstrap" -Dexec.args="Dataset-Emp-Database.csv"
```

## Run demo queries

```bash
mvn exec:java -Dexec.mainClass="pa2.client.ClientApp"
```

## Generated client keys

The bootstrap creates:

```text
client_keys/client.properties
```

This file must stay only on the client side. Do not upload it to the server.

## How this relates to the teacher's script

The teacher's `dataset2mySQLtable.py` uploads the CSV directly to a plaintext MySQL table.
This project instead does:

```text
CSV dataset -> client encryption/bootstrap -> encrypted MySQL table
```

The server/database never receives the plaintext employee fields or secret keys.
