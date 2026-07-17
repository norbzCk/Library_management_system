package Model;

/** this is a student model ,he/she can borrow up to 3 books, 5-day loan period. */
public class StudentMember extends Member {

    private String institution;

    public StudentMember(String memberId, String name, String email,String phoneNumber, String institution) {
        super(memberId, name, email, phoneNumber);
        this.institution = institution;
    }

    @Override 
    public int getBorrowLimit() { 
        return 3; }

    @Override 
    public int getLoanPeriodDays() {
        return 5; }
    
    @Override 
    public String getMemberType() {
        return "Student"; }

    public String getInstitution() { 
        return institution; }
}
