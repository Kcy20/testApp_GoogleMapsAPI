package com.decoders.icontacts.application;

import android.app.Application;

import com.decoders.icontacts.R;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@ReportsCrashes(mailTo = "yangk60@csp.edu", customReportContent = {
        ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
        ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
        ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class MyApp extends Application {
    private static Realm realm;
    private static final String realmName = "icontacts.realm";

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        Realm.init(this);
    }

    public static Realm getRealm() {
        return realm;
    }

    public static RealmConfiguration getRealmDatabaseConfiguration() {
        return new RealmConfiguration.Builder().name(realmName).deleteRealmIfMigrationNeeded().build();
    }

    public static Realm getRealmDatabaseInstance() {
        if (realm == null)
            realm = Realm.getInstance(getRealmDatabaseConfiguration());
        return realm;
    }

    public static void closeRealm() {
        if (realm != null)
            realm.close();
        realm = null;
    }

}
