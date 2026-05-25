package pa2.model;

public class EmployeeEncrypted {
    public long dbId;

    public String employeeIDEnc;
    public String employeeIDIndex;

    public String firstNameEnc;
    public String lastNameEnc;
    public String fullNameEnc;
    public String fullNameIndex;

    public String dateOfBirthEnc;
    public String ageEnc;
    public long ageOpe;

    public String emailEnc;
    public String contactPhoneNumberEnc;
    public String personalPhoneNumberEnc;

    public String jobTitleEnc;
    public String departmentIDEnc;
    public String departmentIDIndex;

    public String hireDateEnc;
    public String employmentTypeEnc;

    public String salaryEnc;
    public String salaryPaillier;
    public long salaryOpe;

    public String salaryBandEnc;

    public String bonusEligibilityEnc;
    public String bonusEligibilityIndex;

    public String recordHmac;
    public String recordSignature;

    public String canonicalDataForIntegrity() {
        return String.join("|",
                safe(employeeIDEnc), safe(employeeIDIndex),
                safe(firstNameEnc), safe(lastNameEnc),
                safe(fullNameEnc), safe(fullNameIndex),
                safe(dateOfBirthEnc), safe(ageEnc), String.valueOf(ageOpe),
                safe(emailEnc), safe(contactPhoneNumberEnc), safe(personalPhoneNumberEnc),
                safe(jobTitleEnc), safe(departmentIDEnc), safe(departmentIDIndex),
                safe(hireDateEnc), safe(employmentTypeEnc),
                safe(salaryEnc), safe(salaryPaillier), String.valueOf(salaryOpe),
                safe(salaryBandEnc),
                safe(bonusEligibilityEnc), safe(bonusEligibilityIndex)
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
