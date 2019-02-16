package in.mindbrick.officelotterypools.Models;

/**
 * Created by chethana on 1/17/2019.
 */

public class Contact {
    private String Sno;
    private String Phone;
    private String Firstname;
    private String Lastname;
    private String Email;
    private String ProfilePic;
    private String Status;

    public Contact(String sno, String phone, String firstname, String lastname, String email, String profilePic, String status) {
        Sno = sno;
        Phone = phone;
        Firstname = firstname;
        Lastname = lastname;
        Email = email;
        ProfilePic = profilePic;
        Status = status;
    }

    public String getSno() {
        return Sno;
    }

    public void setSno(String sno) {
        Sno = sno;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
