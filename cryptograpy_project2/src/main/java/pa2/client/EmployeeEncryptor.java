package pa2.client;

import pa2.crypto.*;
import pa2.model.EmployeeDecrypted;
import pa2.model.EmployeeEncrypted;
import pa2.model.EmployeePlain;

import java.math.BigInteger;

public final class EmployeeEncryptor {
    private EmployeeEncryptor() {}

    public static EmployeeEncrypted encrypt(EmployeePlain p, ClientKeys keys) throws Exception {
        PaillierCrypto paillier = new PaillierCrypto();

        EmployeeEncrypted e = new EmployeeEncrypted();

        e.employeeIDEnc = AESCrypto.encrypt(p.employeeID, keys.aesKey);
        e.employeeIDIndex = HMACIndex.token(p.employeeID, keys.indexKey);

        e.firstNameEnc = AESCrypto.encrypt(p.firstName, keys.aesKey);
        e.lastNameEnc = AESCrypto.encrypt(p.lastName, keys.aesKey);

        e.fullNameEnc = AESCrypto.encrypt(p.fullName, keys.aesKey);
        e.fullNameIndex = HMACIndex.token(p.fullName, keys.indexKey);

        e.dateOfBirthEnc = AESCrypto.encrypt(p.dateOfBirth, keys.aesKey);

        e.ageEnc = AESCrypto.encrypt(String.valueOf(p.age), keys.aesKey);
        e.ageOpe = OPECrypto.encryptOrderValue(p.age, keys.opeKey);

        e.emailEnc = AESCrypto.encrypt(p.email, keys.aesKey);
        e.contactPhoneNumberEnc = AESCrypto.encrypt(p.contactPhoneNumber, keys.aesKey);
        e.personalPhoneNumberEnc = AESCrypto.encrypt(p.personalPhoneNumber, keys.aesKey);

        e.jobTitleEnc = AESCrypto.encrypt(p.jobTitle, keys.aesKey);

        e.departmentIDEnc = AESCrypto.encrypt(p.departmentID, keys.aesKey);
        e.departmentIDIndex = HMACIndex.token(p.departmentID, keys.indexKey);

        e.hireDateEnc = AESCrypto.encrypt(p.hireDate, keys.aesKey);
        e.employmentTypeEnc = AESCrypto.encrypt(p.employmentType, keys.aesKey);

        e.salaryEnc = AESCrypto.encrypt(String.valueOf(p.salary), keys.aesKey);
        e.salaryPaillier = paillier.encrypt(BigInteger.valueOf(p.salary), keys.paillierPublicKey).toString();
        e.salaryOpe = OPECrypto.encryptOrderValue(p.salary, keys.opeKey);

        e.salaryBandEnc = AESCrypto.encrypt(p.salaryBand, keys.aesKey);

        e.bonusEligibilityEnc = AESCrypto.encrypt(p.bonusEligibility, keys.aesKey);
        e.bonusEligibilityIndex = HMACIndex.token(p.bonusEligibility, keys.indexKey);

        String canonical = e.canonicalDataForIntegrity();
        e.recordHmac = IntegrityUtils.hmac(canonical, keys.integrityKey);
        e.recordSignature = ECDSAUtils.sign(canonical, keys.ecdsaKeyPair.getPrivate());

        return e;
    }

    public static EmployeeDecrypted decryptAndVerify(EmployeeEncrypted e, ClientKeys keys) throws Exception {
        String canonical = e.canonicalDataForIntegrity();

        if (!IntegrityUtils.verify(canonical, e.recordHmac, keys.integrityKey)) {
            throw new SecurityException("Invalid HMAC: encrypted record integrity check failed.");
        }

        if (!ECDSAUtils.verify(canonical, e.recordSignature, keys.ecdsaKeyPair.getPublic())) {
            throw new SecurityException("Invalid ECDSA signature: encrypted record authenticity check failed.");
        }

        EmployeeDecrypted d = new EmployeeDecrypted();

        d.employeeID = AESCrypto.decrypt(e.employeeIDEnc, keys.aesKey);
        d.firstName = AESCrypto.decrypt(e.firstNameEnc, keys.aesKey);
        d.lastName = AESCrypto.decrypt(e.lastNameEnc, keys.aesKey);
        d.fullName = AESCrypto.decrypt(e.fullNameEnc, keys.aesKey);
        d.dateOfBirth = AESCrypto.decrypt(e.dateOfBirthEnc, keys.aesKey);
        d.age = Integer.parseInt(AESCrypto.decrypt(e.ageEnc, keys.aesKey));
        d.email = AESCrypto.decrypt(e.emailEnc, keys.aesKey);
        d.contactPhoneNumber = AESCrypto.decrypt(e.contactPhoneNumberEnc, keys.aesKey);
        d.personalPhoneNumber = AESCrypto.decrypt(e.personalPhoneNumberEnc, keys.aesKey);
        d.jobTitle = AESCrypto.decrypt(e.jobTitleEnc, keys.aesKey);
        d.departmentID = AESCrypto.decrypt(e.departmentIDEnc, keys.aesKey);
        d.hireDate = AESCrypto.decrypt(e.hireDateEnc, keys.aesKey);
        d.employmentType = AESCrypto.decrypt(e.employmentTypeEnc, keys.aesKey);
        d.salary = Long.parseLong(AESCrypto.decrypt(e.salaryEnc, keys.aesKey));
        d.salaryBand = AESCrypto.decrypt(e.salaryBandEnc, keys.aesKey);
        d.bonusEligibility = AESCrypto.decrypt(e.bonusEligibilityEnc, keys.aesKey);

        return d;
    }
}
