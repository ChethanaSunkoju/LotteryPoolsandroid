package in.mindbrick.officelotterypools.Helpers;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import in.mindbrick.officelotterypools.Activities.LoginActivity;

/**
 * Created by chethana on 6/28/2018.
 */

public class SessionManagement {
    public static final String KEY_SNO = "sno";
    public static final String KEY_NAME = "username";
    public static final String KEY_MOBILENO="MobileNo";
    public static final String KEY_EMAILID="emailid";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_GROUP_ID = "groupId";


    // Sharedpref file name
    private static final String PREF_NAME = "SpottingPref";
    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String IS_INTRO_LOGIN = "IsIntroLoggedIn";
    private static final String KEY_MCODE ="otp";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME ="lastName";
    public static final String KEY_PROFILE_PIC = "profilePic";


    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    HashMap<String, String> user;

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createIntroSession() {
        // Storing login value as TRUE
        editor.putBoolean(IS_INTRO_LOGIN, true);
        editor.commit();
    }

    public void createSession() {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }


    public void createSignUpSession( String mobileno) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_MOBILENO, mobileno);

        editor.commit();
    }

    public void createLoginSession(String sno, String mobileno, String fname, String lname, String email, String profilePic) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_SNO,sno);
        editor.putString(KEY_MOBILENO, mobileno);
        editor.putString(KEY_FIRST_NAME,fname);
        editor.putString(KEY_LAST_NAME,lname);
        editor.putString(KEY_EMAILID,email);
        editor.putString(KEY_PROFILE_PIC,profilePic);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */

    public void saveGroupId(String groupId){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_GROUP_ID,groupId);
        editor.commit();
    }
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity

            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }




    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        user = new HashMap<String, String>();

        user.put(KEY_SNO,pref.getString(KEY_SNO,null));
        //user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        //user number
        user.put(KEY_MOBILENO, pref.getString(KEY_MOBILENO, null));
        //user email_id
        user.put(KEY_EMAILID, pref.getString(KEY_EMAILID, null));

        user.put(KEY_PASSWORD,pref.getString(KEY_PASSWORD,null));

        user.put(KEY_FIRST_NAME,pref.getString(KEY_FIRST_NAME,null));
        user.put(KEY_GROUP_ID,pref.getString(KEY_GROUP_ID,null));




        //return user
        return user;
    }

    /**
     * Clear session details
     *
     * @return
     */
    public Fragment logoutUser() {
        // Clearing all data from Shared Preferences

        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);

        return null;
    }

    /**
     * Quick check for login
     * *
     */
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

}