package Model;

/** Faculty member: borrow limit 5, loan period 30 days (UML). */
public class FacultyMember extends Member {

    private String department;

    public FacultyMember(String memberId,
                         String name,
                         String email,
                         String phoneNumber,
                         String department) {
        super(memberId, name, email, phoneNumber);
        this.department = department;
    }

    @Override
    public int getBorrowLimit() {
        return 5;
    }

    @Override
    public int getLoanPeriodDays() {
        return 30;
    }

    @Override
    public String getMemberType() {
        return "Faculty";
    }

    public String getDepartment() {
        return department;
    }
}
