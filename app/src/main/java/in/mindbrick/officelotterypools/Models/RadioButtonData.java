package in.mindbrick.officelotterypools.Models;

/**
 * Created by chethana on 1/28/2019.
 */

public class RadioButtonData {

    String Sno;
    String Poolname;
    String Status;


    public RadioButtonData(String sno, String poolname, String status) {
        Sno = sno;
        Poolname = poolname;
        Status = status;
    }

    public String getSno() {
        return Sno;
    }

    public void setSno(String sno) {
        Sno = sno;
    }

    public String getPoolname() {
        return Poolname;
    }

    public void setPoolname(String poolname) {
        Poolname = poolname;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
