package hdvideoplayerallformat.gallery.videoplayer.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import hdvideoplayerallformat.gallery.videoplayer.Activity.AsyncActivity;
import hdvideoplayerallformat.gallery.videoplayer.Adapter.BaseFolderAdapter;
import hdvideoplayerallformat.gallery.videoplayer.Adapter.RecentFolderAdapter;
import hdvideoplayerallformat.gallery.videoplayer.BuildConfig;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Class.Database_Helper;
import hdvideoplayerallformat.gallery.videoplayer.Interface.BottomOptionInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.SharedPrefrences.SharedPref;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.FragmentFolderBinding;

import static hdvideoplayerallformat.gallery.videoplayer.Activity.MainActivity.mainBinding;
import static hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment.mp;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FolderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FolderFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static BaseFolderAdapter videoAdapter;
    public static FragmentFolderBinding folderBinding;
    public static BottomSheetBehavior mBottomSheetBehavior1;
    View view;
    RecentFolderAdapter recentAdapter;
    ArrayList<BaseModel> arrayList = new ArrayList<>();
    Database_Helper database;
    BottomOptionInterface anInterface;
    BaseModel baseModel;
    TextView mCancel, mDone;
    EditText mNewName;
    TextView mDeleteFile;
    CardView mDialogDelete, mDialogCancel;
    int i = 0;
    int pos;
    TextView tvPath, tvDuration, tvSize, tvVideoPath, tvVideoDate;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyReceiver myReceiver;

    public FolderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FolderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FolderFragment newInstance(String param1, String param2) {
        FolderFragment fragment = new FolderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getAlbumPathRenamed(String olderPath, String newName) {
        return olderPath.substring(0, olderPath.lastIndexOf('/')) + "/" + newName;
    }

    public static String getPhotoPathRenamedAlbumChange(String olderPath, String albumNewName) {
        String c = "";
        String[] b = olderPath.split("/");
        for (int x = 0; x < b.length - 2; x++) c += b[x] + "/";
        c += albumNewName + "/" + b[b.length - 1];
        return c;
    }

    @Override
    void permissionGranted() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        folderBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_folder, container, false);
        view = folderBinding.getRoot();

        Util.IsRecentPlay = SharedPref.getRecentPlay(getActivity());

        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_REFRESH_HISTORY"));
        database = new Database_Helper(getActivity());
        mBottomSheetBehavior1 = BottomSheetBehavior.from(folderBinding.mBottomRL);
        anInterface = new BottomOptionInterface() {
            @Override
            public void onMoreClick(BaseModel model, int i) {
                baseModel = model;
                pos = i;
                folderBinding.mTitle.setText(baseModel.getBucketName());
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        };
        videoAdapter = new BaseFolderAdapter(getActivity(), anInterface);
        folderBinding.mVideoRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        mainBinding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                videoAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mainBinding.mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoAdapter.getFilter().filter("");
                mainBinding.searchBar.setText("");
            }
        });

        folderBinding.mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Toasty.info(getActivity(), "Play in background. Press Back key to stop.", Toast.LENGTH_SHORT, true).show();
                i = 0;
                playAudio();
            }
        });

        folderBinding.mRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                RenameDialog();
            }
        });

        folderBinding.mDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent = new Intent(getActivity(), AsyncActivity.class);
                intent.putExtra("model", baseModel.getBucketId());
                getActivity().startActivity(intent);
            }
        });

        folderBinding.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                DeleteDialog("Folder");
            }
        });

        folderBinding.mProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                PropertyDialog();
            }
        });

        folderBinding.mDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialog("History");
            }
        });
        return view;
    }

    public void playAudio() {

        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
                mp = new MediaPlayer();
            }
        } else {
            mp = new MediaPlayer();
        }
        try {
//            Log.e("Path:",baseModel.getPathlist().get(i));
            mp.reset();
            mp.setDataSource(getActivity(), Uri.parse(baseModel.getPathlist().get(i)));
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
            i++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        completeAudio();

    }

    public void completeAudio() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                    mp = null;
                    mp = new MediaPlayer();
                }
                if (baseModel.getPathlist().size() > i) {
                    playAudio();
                } else {
                    Toasty.info(getActivity(), "Play in background finish.", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

    }

    public void PropertyDialog() {
        AlertDialog alertadd = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.properties_dialog, null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tvPath = view.findViewById(R.id.tvPath);
        TextView t1 = view.findViewById(R.id.t1);
        TextView t2 = view.findViewById(R.id.t2);
        t1.setText("Folder name");
        t2.setText("Folder size");
        tvDuration = view.findViewById(R.id.tvDuration);
        tvSize = view.findViewById(R.id.tvSize);
        tvVideoPath = view.findViewById(R.id.tvVideoPath);
        tvVideoDate = view.findViewById(R.id.tvVideoDate);

        File file = new File(baseModel.getBucketPath());
        tvPath.setText(file.getName());
        long total = 0;
        for (int i = 0; i < baseModel.getPathlist().size(); i++) {
            File file1 = new File(baseModel.getPathlist().get(i));
            total += file1.length();
        }
        tvDuration.setText(Util.getSize(total));
        tvSize.setText(baseModel.getPathlist().size() + " Videos");
        tvVideoPath.setText(file.getParentFile().getPath());
        String dateString = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
        tvVideoDate.setText(dateString);
        alertadd.show();
    }

    public void DeleteDialog(String mFrom) {
        AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.delete_dialog, null);
        deleteDialog.setView(view);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDeleteFile = view.findViewById(R.id.txt_filename);
        TextView text = view.findViewById(R.id.txt_text);
        if (mFrom.equals("Folder")) {
            text.setText("This folder and contents can be delete permanently.");
        } else {
            text.setText("All history will be delete permanently.");
        }
        mDialogCancel = view.findViewById(R.id.mCancel);
        mDialogDelete = view.findViewById(R.id.mDelete);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.show();

        mDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                if (mFrom.equals("Folder")) {
                    new ASynchTaskDelete().execute((Void[]) null);
                } else {
                    database.deleteTable();
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
                    Intent localIn = new Intent("TAG_REFRESH_HISTORY");
                    lbm.sendBroadcast(localIn);
                    onResume();
                }
            }
        });
        mDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
    }

    public void DeleteAction(File file) {

        boolean isDelete = Util.delete(getActivity(), file);
        if (!isDelete)
            isDelete = file.delete();
        if (isDelete) {

            arrayList.remove(file.getAbsolutePath());
            database.getHistoryCheck(file.getAbsolutePath());

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Util.IsUpdate = true;
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
                    Intent localIn = new Intent("TAG_REFRESH_HISTORY");
                    lbm.sendBroadcast(localIn);
                    onResume();
                }
            }, 1000);
            Toasty.success(getActivity(), "Video successfully deleted!!!", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(getActivity(), "Something went wrong!!!", Toast.LENGTH_SHORT, true).show();
        }
    }

    public void getFolder() {
        arrayList.clear();
        arrayList = Util.GetFolder(getActivity());
        try {
            if (arrayList != null && arrayList.size() > 0) {
                mainBinding.noData.setVisibility(View.GONE);
                mainBinding.bottomNavigationViewLinear.setVisibility(View.VISIBLE);
                mainBinding.viewPager.setVisibility(View.VISIBLE);
                videoAdapter.Addall(arrayList);
                folderBinding.mVideoRecycler.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
            } else {
                mainBinding.noData.setVisibility(View.VISIBLE);
                mainBinding.bottomNavigationViewLinear.setVisibility(View.GONE);
                mainBinding.viewPager.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("Error folder:", e.getMessage());
        }
    }

    public void RenameDialog() {
        AlertDialog alertadd = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.rename_dialog, null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCancel = view.findViewById(R.id.mCancel);
        mDone = view.findViewById(R.id.mDone);
        mNewName = view.findViewById(R.id.mEditText);
        TextView mTitle = view.findViewById(R.id.mTitle);
        mTitle.setText(baseModel.getBucketName());
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertadd.dismiss();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNewName.getText() != null) {
                    new ASynchTaskRename(baseModel, mNewName.getText().toString().trim()).execute();
                }
                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    public boolean renameAlbum(BaseModel albumDetail, String newName) {
        boolean success;
        File newDir = new File(getAlbumPathRenamed(albumDetail.getBucketPath(), newName));
        File oldDir = new File(albumDetail.getBucketPath());
        success = oldDir.renameTo(newDir);
        Log.e("TAG", "renameAlbum: " + success);
//        Log.e("From", oldDir.getPath());
//        Log.e("To", newDir.getPath());
        if (success) {
            for (final String m : albumDetail.getPathlist()) {
                final File from = new File(m);
                File to = new File(getPhotoPathRenamedAlbumChange(m, newName));

                final String where = MediaStore.MediaColumns.DATA + "=?";
                final String[] selectionArgs = new String[]{albumDetail.getBucketPath() + "/" + to.getName()};

                try {
                    getActivity().getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, where, selectionArgs);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DATA, to.getPath());
                    values.put(MediaStore.Video.Media.DATE_TAKEN, to.lastModified());
                    getActivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } catch (Exception e) {
                    getActivity().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, selectionArgs);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, to.getPath());
                    values.put(MediaStore.Images.Media.DATE_TAKEN, to.lastModified());
                    getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }

                scanFile(new String[]{from.getAbsolutePath()});
                scanFile(new String[]{to.getAbsolutePath()}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        Log.d(s, "onScanCompleted: " + s);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(new File(s)));
                        getActivity().sendBroadcast(intent);
                    }
                });
                database.renameHistory(from.getAbsolutePath(), to.getAbsolutePath());
            }
        }
        return success;
    }

    public void scanFile(String[] path) {
        MediaScannerConnection.scanFile(getActivity(), path, null, null);
    }

    public void scanFile(String[] path, MediaScannerConnection.OnScanCompletedListener onScanCompletedListener) {
        MediaScannerConnection.scanFile(getActivity(), path, null, onScanCompletedListener);
    }

    @Override
    public void onResume() {

        super.onResume();

        getFolder();
        ArrayList<BaseModel> folderListArray = new ArrayList<>();
        ArrayList<BaseModel> folderListArray1;
        folderListArray1 = database.getHistoryData();

        for (int i = 0; i < folderListArray1.size(); i++) {
            BaseModel baseModel = folderListArray1.get(i);
            File file = new File(baseModel.getBucketPath());
            if (file.exists()) {
                folderListArray.add(baseModel);
            } else {
                database.deleteOldHistory(baseModel.getBucketPath());
            }
        }
        Collections.reverse(folderListArray);

        if (folderListArray != null && folderListArray.size() > 0) {
            if (Util.IsRecentPlay) {
                folderBinding.mRecentRL.setVisibility(View.VISIBLE);
            } else {
                folderBinding.mRecentRL.setVisibility(View.GONE);
            }
            folderBinding.mScrollView.fullScroll(ScrollView.FOCUS_UP);
            recentAdapter = new RecentFolderAdapter(getActivity(), "Folder");
            folderBinding.mRecentRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            folderBinding.mRecentRecycler.setAdapter(recentAdapter);
            recentAdapter.Addall(folderListArray);
        } else {
            folderBinding.mRecentRL.setVisibility(View.GONE);
        }
    }

    public class ASynchTaskDelete extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int nofiles = 0;
            for (int i = 0; i < baseModel.getPathlist().size(); i++) {
                File file = new File(baseModel.getPathlist().get(i));
                if (file.exists()) {
                    boolean d = file.delete();
                    if (d) {
                        try {
//                            Log.e("Inner file:",file.getPath());
                            database.getHistoryCheck(file.getPath());
                            final String where = MediaStore.MediaColumns.DATA + "=?";
                            final String[] selectionArgs = new String[]{file.getAbsolutePath()};
                            getActivity().getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, where, selectionArgs);
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
                            } else {
                                uri = Uri.fromFile(file);
                            }
                            getActivity().getContentResolver().notifyChange(uri, null);

                            arrayList.remove(pos);

                            publishProgress((nofiles + 1));
                        } catch (Exception e) {
                        }
                    } else {
                        Toasty.error(getActivity(), "Something went wrong!!!", Toast.LENGTH_SHORT, true).show();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressDialog != null) {
                progressDialog.setProgress(values[0]);
                progressDialog.setMessage("Deleting Files " + values[0]);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //  progressDialog.dismiss();
            try {
                if (progressDialog != null) {
                    if (!getActivity().isFinishing() && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                Toasty.success(getActivity(), "Delete folder successfully!", Toast.LENGTH_SHORT, true).show();
                Util.IsUpdate = true;
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
                        Intent localIn = new Intent("TAG_REFRESH_HISTORY");
                        lbm.sendBroadcast(localIn);
                        onResume();
                    }
                }, 1000);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class ASynchTaskRename extends AsyncTask<Void, Integer, Boolean> {
        ProgressDialog progressDialog;
        BaseModel albumDetail;
        String etName;

        public ASynchTaskRename(BaseModel albumDetail, String newName) {
            this.albumDetail = albumDetail;
            this.etName = newName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return renameAlbum(albumDetail, etName);
        }

        @Override
        protected void onPostExecute(Boolean str) {
            super.onPostExecute(str);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                if (str) {
                    Toasty.success(getActivity(), "Rename folder successfully!", Toast.LENGTH_SHORT, true).show();
                    Util.IsUpdate = true;
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
                            Intent localIn = new Intent("TAG_REFRESH_HISTORY");
                            lbm.sendBroadcast(localIn);
                            onResume();
                        }
                    }, 1000);
                } else {
                    Toasty.error(getActivity(), "Something went wrong!!!", Toast.LENGTH_SHORT, true).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            onResume();
        }
    }
}

