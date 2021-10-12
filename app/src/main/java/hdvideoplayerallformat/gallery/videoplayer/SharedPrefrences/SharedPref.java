package hdvideoplayerallformat.gallery.videoplayer.SharedPrefrences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import hdvideoplayerallformat.gallery.videoplayer.Class.EqualizerModel;

public class SharedPref {

    public static final String MyPREFERENCES = "MX Video";
    public static String VIEW_TYPE = "VIEW_TYPE";
    public static String SORTING_TYPE = "SORTING_TYPE";
    public static String SORTING_TYPE2 = "SORTING_TYPE2";

    public static String FIELD_NAME = "FIELD_NAME";
    public static String FIELD_LENGTH = "FIELD_LENGTH";
    public static String FIELD_EXTENSION = "FIELD_EXTENSION";
    public static String FIELD_PATH = "FIELD_PATH";
    public static String FIELD_SIZE = "FIELD_SIZE";
    public static String FIELD_DATE = "FIELD_DATE";

    public static String MODEL = "MODEL";
    public static String RECENT_PLAY = "RECENT_PLAY";
    public static String AUTO_PLAY = "AUTO_PLAY";
    public static String ORIENTATION = "ORIENTATION";

    public static void setSortingType(Context c1, String view_type) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(SORTING_TYPE, view_type);
        edit.commit();
    }

    public static String getSortingType(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String set = sharedpreferences.getString(SORTING_TYPE, "DATE");
        return set;
    }

    public static void setSortingType1(Context c1, String view_type) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(SORTING_TYPE2, view_type);
        edit.commit();
    }

    public static String getSortingType1(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String set = sharedpreferences.getString(SORTING_TYPE2, "DESC");
        return set;
    }

    public static void setViewType(Context c1, String view_type) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(VIEW_TYPE, view_type);
        edit.commit();
    }

    public static String getViewType(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String set = sharedpreferences.getString(VIEW_TYPE, "GRID");
        return set;
    }

    public static void setFieldName(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIELD_NAME, name);
        edit.commit();
    }

    public static boolean getFieldName(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(FIELD_NAME, true);
        return set;
    }

    public static void setFieldLength(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIELD_LENGTH, name);
        edit.commit();
    }

    public static boolean getFieldLength(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(FIELD_LENGTH, true);
        return set;
    }

    public static void setFieldExtension(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIELD_EXTENSION, name);
        edit.commit();
    }

    public static boolean getFieldExtension(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(FIELD_EXTENSION, true);
        return set;
    }

    public static void setFieldPath(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIELD_PATH, name);
        edit.commit();
    }

    public static boolean getFieldPath(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(FIELD_PATH, true);
        return set;
    }

    public static void setFieldSize(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIELD_SIZE, name);
        edit.commit();
    }

    public static boolean getFieldSize(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(FIELD_SIZE, true);
        return set;
    }

    public static void setFieldDate(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIELD_DATE, name);
        edit.commit();
    }

    public static boolean getFieldDate(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(FIELD_DATE, true);
        return set;
    }

    public static void setEqualizer(Context c1, EqualizerModel model) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(model);
        edit.putString(MODEL, json);
        edit.commit();
    }

    public static EqualizerModel getEqualizer(Context c1) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        String json = prefs.getString(MODEL, "");
        EqualizerModel obj = gson.fromJson(json, EqualizerModel.class);
        edit.commit();
        return obj;
    }

    public static void setRecentPlay(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(RECENT_PLAY, name);
        edit.commit();
    }

    public static boolean getRecentPlay(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(RECENT_PLAY, true);
        return set;
    }

    public static void setAutoPlay(Context c1, boolean name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(AUTO_PLAY, name);
        edit.commit();
    }

    public static boolean getAutoPlay(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean set = sharedpreferences.getBoolean(AUTO_PLAY, true);
        return set;
    }

    public static void setOrientation(Context c1, String name) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(ORIENTATION, name);
        edit.commit();
    }

    public static String getOrientation(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String set = sharedpreferences.getString(ORIENTATION, "Sensor");
        return set;
    }
}
