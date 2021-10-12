package hdvideoplayerallformat.gallery.videoplayer.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;

import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

public class Util {
    public static String VIEW_TYPE = "GRID";
    public static String SORT_BY = "DATE";
    public static String SORTING_TYPE = "DESC";
    public static boolean FOLDER_BACK = false;
    public static boolean HISTORY_BACK = false;
    public static SimpleDateFormat format = new SimpleDateFormat(
            "dd LLL yyyy");
    public static boolean NameField = true;
    public static boolean LengthField = false;
    public static boolean ExtensionField = false;
    public static boolean PathField = false;
    public static boolean SizeField = false;
    public static boolean DateField = false;
    public static boolean IsUpdate = false;
    public static boolean IsEnable = true;

    public static boolean IsRecentPlay = true;
    public static boolean IsAutoPlay = true;
    public static String Orientation = "Sensor";
    public static String isRepeat = "off";
    public static boolean isShuffle = false;


    public static ArrayList<BaseModel> GetFolder(Activity activity) {
        ArrayList<BaseModel> folderListArray = new ArrayList<BaseModel>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA};

        if (activity != null) {
            Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, DATE_ADDED + " DESC");
            ArrayList<String> ids = new ArrayList<String>();

            if (cursor != null) {

                while (cursor.moveToNext()) {
                    BaseModel album = new BaseModel();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
                    album.setBucketId(cursor.getString(columnIndex));

                    if (!ids.contains(album.getBucketId())) {
                        columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                        album.setBucketName(cursor.getString(columnIndex));
                        //------------------------folder path
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                        String result = cursor.getString(column_index);
                        String ParentPath = GetParentPath(result);
                        album.setBucketPath(ParentPath);
                        int column_index1 = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
                        String result1 = cursor.getString(column_index1);
                        album.setBucketId(result1);
                        album.setPath(ParentPath);
//                        Log.e("Name=>", "" + album.getBucketPath());
                        //----------------------
                        columnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                        album.setId(cursor.getString(columnIndex));
                        album.setDate(String.valueOf(cursor.getColumnIndex(DATE_ADDED)));
                        album.pathlist = getCameraVideoCover("" + album.getBucketId(), activity); //----get four image path arraylist
                        folderListArray.add(album);
                        ids.add(album.getBucketId());
                    }
                }

                cursor.close();
            }
        }
        return folderListArray;
    }

    public static ArrayList<String> getCameraVideoCover(String id, Activity activity) {
        String data = null;
        ArrayList<String> result = new ArrayList<String>();
        final String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        final String selection = MediaStore.Video.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {id};
        final Cursor cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        if (cursor.moveToFirst()) {

            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            final int name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            do {
                data = cursor.getString(dataColumn);
//                Log.e("Name=>", "" + data);
                result.add(data);
                //---------------------------------------------
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public static String GetParentPath(String path) {
        File file = new File(path);
        return file.getAbsoluteFile().getParent();
    }


    public static ArrayList<BaseModel> GetAllVideos(Activity activity) {
        ArrayList<BaseModel> folderListArray = new ArrayList<>();
        try {

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, DATE_ADDED + " DESC");
            ArrayList<String> ids = new ArrayList<String>();
            int count = 0;

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    BaseModel album = new BaseModel();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
                    album.setBucketId(cursor.getString(columnIndex));

                    columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                    album.setBucketName(cursor.getString(columnIndex));

                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    String result = cursor.getString(column_index);

                    String ParentPath = GetParentPath(result);
                    File file = new File(result);
                    album.setBucketPath(file.getAbsolutePath());
//                    Log.e("Path:",album.getBucketPath());
                    album.setName(file.getName());
                    columnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    album.setId(cursor.getString(columnIndex));
                    album.setPath(file.getPath());
                    album.pathlist = getCameraVideoCover("" + album.getBucketId(), activity); //----get four image path arraylist
                    folderListArray.add(album);
                    album.setSize(file.getAbsolutePath().length());

                    ids.add(album.getBucketId());

                }

                cursor.close();

            }
        } catch (Exception w) {
            Log.e("Error:", w.getMessage());
        }
        return folderListArray;
    }

    public static ArrayList<BaseModel> getFolderVideoCover(String id, Activity activity) {
        ArrayList<BaseModel> folderListArray = new ArrayList<>();
        String data = null;

        final String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        final String selection = MediaStore.Video.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {id};
        final Cursor cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    BaseModel album = new BaseModel();

                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    String result = cursor.getString(column_index);

                    File file = new File(result);
                    album.setBucketId(id);
                    album.setBucketPath(file.getPath());
                    album.setFolderPath(file.getParentFile().getPath());
                    album.setBucketName(file.getParentFile().getName());
                    album.setName(file.getName());
                    album.setPath(file.getPath());

                    album.setSize(file.length());
//                    Log.e("Name:" + album.getName() , "Size :" + album.getSize());
                    folderListArray.add(album);
                }

                cursor.close();
            }
        } catch (Exception w) {
            Log.e("Error:", w.getMessage());
        }
        return folderListArray;
    }

    public static String generateTime(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getPath());
        long time = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public static String getSize(long size) {
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;
        long tera = giga * kilo;
        String s = "";
        double kb = (double) size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;
        double tb = gb / kilo;
        if (size < kilo) {
            s = size + " Bytes";
        } else if (size >= kilo && size < mega) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size >= mega && size < giga) {
            s = String.format("%.2f", mb) + " MB";
        } else if (size >= giga && size < tera) {
            s = String.format("%.2f", gb) + " GB";
        } else if (size >= tera) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    public static boolean delete(final Context context, final File file) {

        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[]{
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");
        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {
            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }

    public static void expand(final View v) {

        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

// Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

// Expansion speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {


        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

// Collapse speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }


}
