package pl.radoslawdrewa.droid.altdom;

import android.content.res.Resources;

/**
 * Created by radoslaw.drewa on 2014-10-19.
 */
public class StringResource {

    Resources resources;
    String packageName;

    public StringResource(Resources resources, String packageName) {
        this.resources = resources;
        this.packageName = packageName;
    }

    public String get(String value) {
        int resId = resources.getIdentifier(value, "string", packageName);
        return resId == 0 ? null : resources.getString(resId);
    }
}
