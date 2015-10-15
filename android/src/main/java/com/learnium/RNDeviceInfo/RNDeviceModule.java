package com.learnium.RNDeviceInfo;

import javax.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Callback;

import android.os.Build;
import android.provider.Settings.Secure;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.text.format.Time;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;

public class RNDeviceModule extends ReactContextBaseJavaModule {

    ReactApplicationContext reactContext;

    public RNDeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNDeviceInfo";
    }

    /*
     * @Description: Returns a well-formed ITEF BCP 47 language tag representing
     * the locale identifier for the client's current locale
     *
     * @Return: String: The BCP 47 language tag for the current locale
     */
    private String toBcp47Language(Locale loc){
        final char SEP = '-';       // we will use a dash as per BCP 47
        String language = loc.getLanguage();
        String region = loc.getCountry();
        String variant = loc.getVariant();

        // special case for Norwegian Nynorsk since "NY" cannot be a variant as per BCP 47
        // this goes before the string matching since "NY" wont pass the variant checks
        if( language.equals("no") && region.equals("NO") && variant.equals("NY")){
            language = "nn";
            region = "NO";
            variant = "";
        }

        if( language.isEmpty() || !language.matches("\\p{Alpha}{2,8}")){
            language = "und";       // Follow the Locale#toLanguageTag() implementation
            // which says to return "und" for Undetermined
        }else if(language.equals("iw")){
            language = "he";        // correct deprecated "Hebrew"
        }else if(language.equals("in")){
            language = "id";        // correct deprecated "Indonesian"
        }else if(language.equals("ji")){
            language = "yi";        // correct deprecated "Yiddish"
        }

        // ensure valid country code, if not well formed, it's omitted
        if (!region.matches("\\p{Alpha}{2}|\\p{Digit}{3}")) {
            region = "";
        }

        // variant subtags that begin with a letter must be at least 5 characters long
        if (!variant.matches("\\p{Alnum}{5,8}|\\p{Digit}\\p{Alnum}{3}")) {
            variant = "";
        }

        StringBuilder bcp47Tag = new StringBuilder(language);
        if (!region.isEmpty()) {
            bcp47Tag.append(SEP).append(region);
        }
        if (!variant.isEmpty()) {
            bcp47Tag.append(SEP).append(variant);
        }

        return bcp47Tag.toString();
    }

    @Override
    public @Nullable Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();

        PackageManager packageManager = this.reactContext.getPackageManager();
        String packageName = this.reactContext.getPackageName();

        constants.put("appVersion", "not available");
        constants.put("buildNumber", 0);

        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            constants.put("appVersion", info.versionName);
            constants.put("buildNumber", info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        constants.put("systemName", "Android");
        constants.put("systemVersion", Build.VERSION.RELEASE);
        constants.put("model", Build.MODEL);
        constants.put("deviceId", Secure.getString(this.reactContext.getContentResolver(), Secure.ANDROID_ID));
        constants.put("systemManufacturer", Build.MANUFACTURER);
        constants.put("bundleId", packageName);

        //TimeZone from users device
        TimeZone tz = TimeZone.getTimeZone(Time.getCurrentTimezone());
        constants.put("utcOffset", tz.getRawOffset()/1000);
        constants.put("dstOffset", tz.getDSTSavings() / 1000);

        //User's locale
        constants.put("locale", toBcp47Language(Locale.getDefault()));

        return constants;
    }
}
