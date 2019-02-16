package in.mindbrick.officelotterypools.Models;

/**
 * Created by chethana on 12/8/2018.
 */

public class Pool {
    private String PoolGID;
    private String Pooltype;
    private String PooladminId;
    private String Adminname;
    private String Poolname;
    private String Amount;
    private String PGpic;
    private String CreatedDate;


    public Pool(String poolGID, String pooltype, String pooladminId, String adminname, String poolname, String amount, String PGpic, String createdDate) {
        PoolGID = poolGID;
        Pooltype = pooltype;
        PooladminId = pooladminId;
        Adminname = adminname;
        Poolname = poolname;
        Amount = amount;
        this.PGpic = PGpic;
        CreatedDate = createdDate;
    }

    public String getPoolGID() {
        return PoolGID;
    }

    public void setPoolGID(String poolGID) {
        PoolGID = poolGID;
    }

    public String getPooltype() {
        return Pooltype;
    }

    public void setPooltype(String pooltype) {
        Pooltype = pooltype;
    }

    public String getPooladminId() {
        return PooladminId;
    }

    public void setPooladminId(String pooladminId) {
        PooladminId = pooladminId;
    }

    public String getAdminname() {
        return Adminname;
    }

    public void setAdminname(String adminname) {
        Adminname = adminname;
    }

    public String getPoolname() {
        return Poolname;
    }

    public void setPoolname(String poolname) {
        Poolname = poolname;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getPGpic() {
        return PGpic;
    }

    public void setPGpic(String PGpic) {
        this.PGpic = PGpic;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}
