package hdvideoplayerallformat.gallery.videoplayer.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;

public class AsyncActivity extends AppCompatActivity {

    private static final MessageDigest messageDigest;
    public static ArrayList<BaseModel> mFinalList;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("cannot initialize SHA-512 hash function", e);
        }
    }

    public ArrayList<BaseModel> mAllFiles;
    public HashMap<String, Long> mCommenlist;
    public HashMap<String, Long> mPairlist;
    public ArrayList<String> mTitleList;
    public ArrayList<BaseModel> mDuplicateFiles1;
    boolean aBoolean = false;
    String bucketID;
    Map<String, List<String>> mList;

    public static void findDuplicateFiles(Map<String, List<String>> filesList, File directory) {
        for (File dirChild : directory.listFiles()) {
            if (!dirChild.getName().startsWith(".")) {
                if (dirChild.isDirectory()) {
                    findDuplicateFiles(filesList, dirChild);

                } else {
                    String name = dirChild.getName().toLowerCase();
                    if (name.contains(".mp4") || name.contains(".wav") || name.contains(".wmv") ||
                            name.contains(".mov") || name.contains(".avi") || name.contains(".avchd") ||
                            name.contains(".webm") || name.contains(".html5") || name.contains(".mpeg-2")) {
                        try {
                            // Read file as bytes
                            FileInputStream fileInput = new FileInputStream(dirChild);
                            byte[] fileData = new byte[(int) dirChild.length()];
                            fileInput.read(fileData);
                            fileInput.close();
                            // Create unique hash for current file
                            String uniqueFileHash = new BigInteger(1, messageDigest.digest(fileData)).toString(16);
                            List<String> identicalList = filesList.get(uniqueFileHash);
                            if (identicalList == null) {
                                identicalList = new LinkedList<String>();
                            }
                            // Add path to list
                            identicalList.add(dirChild.getName());
                            // push updated list to Hash table
                            filesList.put(uniqueFileHash, identicalList);
                        } catch (IOException e) {
                            throw new RuntimeException("cannot read file " + dirChild.getAbsolutePath(), e);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        AsyncTaskExample asyncTask = new AsyncTaskExample();
        asyncTask.execute(String.valueOf(new String[0]));
    }

    @Override
    protected void onPause() {
        super.onPause();
        aBoolean = true;
//        Log.e("On","Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("On","Resume");
        if (aBoolean)
            onBackPressed();
    }

    public void createDuplicateList() {
        mCommenlist = new HashMap<>();
        mPairlist = new HashMap<>();
        mTitleList = new ArrayList<>();
        mFinalList = new ArrayList<>();
        mDuplicateFiles1 = new ArrayList<>();
        for (List<String> list : mList.values()) {
            if (list.size() > 1) {
                for (String file : list) {

                    mTitleList.add(file);
                }
            }
        }

        Collections.reverse(mAllFiles);
        for (int i = 0; i < mAllFiles.size(); i++) {
            for (int j = 0; j < mTitleList.size(); j++) {
                if (mAllFiles.get(i).getName().equals(mTitleList.get(j))) {
                    mDuplicateFiles1.add(mAllFiles.get(i));
                }
            }
        }

        List<String> MainList = new ArrayList<>();
        for (int i = 0; i < mDuplicateFiles1.size(); i++) {
            if (!MainList.contains(mDuplicateFiles1.get(i).getName())) {
                MainList.add(mDuplicateFiles1.get(i).getName());
                mCommenlist.put(mDuplicateFiles1.get(i).getName(), mDuplicateFiles1.get(i).getSize());
            }
        }

        for (int i = 0; i < MainList.size(); i++) {
            if (!mPairlist.containsKey(MainList.get(i))) {    //match title
                Long size = mCommenlist.get(MainList.get(i));
                if (!mPairlist.containsValue(size)) {
                    mPairlist.put(MainList.get(i), size);
                }
            }
        }

        for (String name : mPairlist.keySet()) {
            Long value = mPairlist.get(name);
            ChildItemList(value);
        }

    }

    public List<BaseModel> ChildItemList(Long size) {
        for (int i = 0; i < mDuplicateFiles1.size(); i++) {

            if (!mFinalList.contains(mDuplicateFiles1.get(i))) {
                if (size == mDuplicateFiles1.get(i).getSize() && size != 0) {
                    mFinalList.add(mDuplicateFiles1.get(i));
                }
            }
        }
        return mFinalList;
    }

    private class AsyncTaskExample extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            if (!aBoolean) {
                bucketID = getIntent().getStringExtra("model");
                mAllFiles = Util.getFolderVideoCover(bucketID, AsyncActivity.this);
                mList = new HashMap<String, List<String>>();
                findDuplicateFiles(mList, new File(mAllFiles.get(0).getFolderPath()));

                createDuplicateList();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            if (!aBoolean) {
                Intent intent = new Intent(AsyncActivity.this, DuplicateActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}