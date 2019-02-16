package in.mindbrick.officelotterypools.Models;

/**
 * Created by chethana on 1/2/2019.
 */

public class Contacts {

    private String Mobile;
    private String commonGroups;

    public Contacts(String mobile, String commonGroups) {

        Mobile = mobile;
        this.commonGroups = commonGroups;
    }



    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getCommonGroups() {
        return commonGroups;
    }

    public void setCommonGroups(String commonGroups) {
        this.commonGroups = commonGroups;
    }
}
