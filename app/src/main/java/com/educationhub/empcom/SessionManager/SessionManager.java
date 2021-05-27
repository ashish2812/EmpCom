package com.educationhub.empcom.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    //variables
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    private static final String IS_LOGIN = "IsLoggedIn";


    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_REMEBERMEPHONENUMBER = "RememberMePhoneNumber";
    public static final String KEY_PASSWORD = "password";

    //Remember me Variable
    private static final String IS_REMEMBERME = "IsRememberMeIn";
    public static final String KEY_SESSIONPHONENUMBER = "RememberMePhoneNumber";
    public static final String KEY_SESSIONPASSWORD = "password";

    public SessionManager(Context _context, String sessionName) {
        context = _context;
        userSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = userSession.edit();

    }
     /*
        User login
        session
    * */

    public void createLoginSession(String fullname, String username, String phoneNumber, String password) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PHONENUMBER, phoneNumber);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getUserDetailFromSession() {

        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_FULLNAME, userSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_USERNAME, userSession.getString(KEY_USERNAME, null));
        userData.put(KEY_PHONENUMBER, userSession.getString(KEY_PHONENUMBER, null));
        userData.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD, null));

        return userData;

    }

    public boolean checkLogin() {
        if (userSession.getBoolean(IS_LOGIN, false)) {

            return true;

        } else
            return false;

    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    /*
    Remember me
    session
    function
     */
    public void createRememberMeSession(String phoneNumber, String password) {

        editor.putBoolean(IS_REMEMBERME, true);

        editor.putString(KEY_REMEBERMEPHONENUMBER, phoneNumber);
        editor.putString(KEY_SESSIONPASSWORD, password);


        editor.commit();
    }

    public HashMap<String, String> getRememberSession() {

        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_SESSIONPHONENUMBER, userSession.getString(KEY_SESSIONPHONENUMBER, null));
        userData.put(KEY_SESSIONPASSWORD, userSession.getString(KEY_SESSIONPASSWORD, null));
        return userData;

    }

    public boolean checkRememberMe() {
        if (userSession.getBoolean(IS_REMEMBERME, false)) {

            return true;

        } else
            return false;

    }


}
