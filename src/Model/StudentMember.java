package Model;

/** Student member: borrow limit 3, loan period 14 days (UML). */
public class StudentMember extends Member {

    private String institution;

    public StudentMember(String memberId,
                         String name,
                         String email,
                         String phoneNumber,
                         String institution) {
        super(memberId, name, email, phoneNumber);
        this.institution = institution;
    }

    @Override
    public int getBorrowLimit() {
        return 3;
    }

    @Override
    public int getLoanPeriodDays() {
        return 5;
    }

    @Override
    public String getMemberType() {
        return "Student";
    }

    public String getInstitution() {
        return institution;
    }
}
