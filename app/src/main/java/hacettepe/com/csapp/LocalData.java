package hacettepe.com.csapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class LocalData {

    private static final String SHARED_PREF_KEY = "JEOLOJI";
    private Context context;
    private SharedPreferences sharedPreferences;

    public LocalData(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public String getStringValue(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setStringValue(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public String UserCode() {
        String currentUserCode = getStringValue("USER_CODE", "");
        if (currentUserCode == null || currentUserCode.isEmpty()) {
            currentUserCode = UUID.randomUUID().toString();
            setStringValue("USER_CODE", currentUserCode);
        }
        return currentUserCode;
    }

    public boolean isFirstOpen() {

        if (getStringValue("FIRST_OPEN", "NO").equals("NO")) {
            setStringValue("FIRST_OPEN", "YES");
            return true;
        }

        return false;
    }

}
