package pa2.model;

public class EmployeePlain {
    public String employeeID;
    public String firstName;
    public String lastName;
    public String fullName;
    public String dateOfBirth;
    public int age;
    public String email;
    public String contactPhoneNumber;
    public String personalPhoneNumber;
    public String jobTitle;
    public String departmentID;
    public String hireDate;
    public String employmentType;
    public long salary;
    public String salaryBand;
    public String bonusEligibility;

    public boolean isBonusEligible() {
        return bonusEligibility != null && bonusEligibility.trim().equalsIgnoreCase("Yes");
    }

    @Override
    public String toString() {
        return "EmployeePlain{" +
                "employeeID='" + employeeID + '\'' +
                ", fullName='" + fullName + '\'' +
                ", departmentID='" + departmentID + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                ", bonusEligibility='" + bonusEligibility + '\'' +
                '}';
    }
}
