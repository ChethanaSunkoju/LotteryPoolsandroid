package in.mindbrick.officelotterypools.Models;

/**
 * Created by chethana on 12/30/2018.
 */

public class FileResponse {

    private String _id;
    private String Sno;
    private String Phone;
    private String Firstname;
    private String Lastname;
    private String Email;
    private String ProfilePic;
    private String msg;

    public FileResponse(String _id, String sno, String phone, String firstname, String lastname, String email, String profilePic, String msg) {
        this._id = _id;
        Sno = sno;
        Phone = phone;
        Firstname = firstname;
        Lastname = lastname;
        Email = email;
        ProfilePic = profilePic;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
}
