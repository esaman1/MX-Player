package hdvideoplayerallformat.gallery.videoplayer.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import hdvideoplayerallformat.gallery.videoplayer.Activity.MainActivity;
import hdvideoplayerallformat.gallery.videoplayer.Adapter.BaseVideoAdapter;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Class.Database_Helper;
import hdvideoplayerallformat.gallery.videoplayer.Interface.BottomOptionInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.SharedPrefrences.SharedPref;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.FragmentVideoBinding;

import static hdvideoplayerallformat.gallery.videoplayer.Activity.MainActivity.mainBinding;
import static hdvideoplayerallformat.gallery.videoplayer.Adapter.BaseVideoAdapter.arrayFilterList;
import static hdvideoplayerallformat.gallery.videoplayer.Adapter.BaseVideoAdapter.arrayVideoList;


public class VideoFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static BaseVideoAdapter videoAdapter;
    public static FragmentVideoBinding videoBinding;
    public static BottomSheetBehavior mBottomSheetBehavior1;
    public static MediaPlayer mp;
    View view;
    ArrayList<BaseModel> arrayList = new ArrayList<>();
    BottomOptionInterface anInterface;
    BaseModel baseModel;
    TextView mCancel, mDone;
    EditText mNewName;
    TextView mDeleteFile;
    Database_Helper database;
    CardView mDialogDelete, mDialogCancel;
    TextView tvPath, tvDuration, tvSize, tvVideoPath, tvVideoDate;
    MediaScannerConnection msConn;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyReceiver myReceiver;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(R.id.searchBar);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        videoBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_video, container, false);
        view = videoBinding.getRoot();
        database = new Database_Helper(getActivity());
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_VIEW_TYPE"));

        mBottomSheetBehavior1 = BottomSheetBehavior.from(videoBinding.mBottomRL);
        anInterface = new BottomOptionInterface() {
            @Override
            public void onMoreClick(BaseModel model, int i) {
                baseModel = model;
                videoBinding.mTitle.setText(baseModel.getName());
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        };


        videoAdapter = new BaseVideoAdapter(getActivity(), anInterface);
        if (getActivity() != null) {
            new AsynchLoadData().execute((Void[]) null);
        }

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

        videoBinding.mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Toasty.info(getActivity(), "Play in background. Press Back key to stop.", Toast.LENGTH_SHORT, true).show();
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
                    mp.setDataSource(getActivity(), Uri.parse(baseModel.getPath()));
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            }
        });

        videoBinding.mRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                RenameDialog();
            }
        });

        videoBinding.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(baseModel.getBucketPath())));
                startActivity(Intent.createChooser(sharingIntent, "Share Via"));
            }
        });

        videoBinding.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                DeleteDialog();
            }
        });

        videoBinding.mProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                PropertyDialog();
            }
        });
        return view;
    }

    public void PropertyDialog() {
        AlertDialog alertadd = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.properties_dialog, null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tvPath = view.findViewById(R.id.tvPath);
        tvDuration = view.findViewById(R.id.tvDuration);
        tvSize = view.findViewById(R.id.tvSize);
        tvVideoPath = view.findViewById(R.id.tvVideoPath);
        tvVideoDate = view.findViewById(R.id.tvVideoDate);

        File file = new File(baseModel.getBucketPath());
        tvPath.setText(baseModel.getName());
        tvDuration.setText(Util.generateTime(file));
        tvSize.setText(Util.getSize(file.length()));
        tvVideoPath.setText(file.getParentFile().getPath());
        String dateString = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
        tvVideoDate.setText(dateString);
        alertadd.show();
    }

    public void DeleteDialog() {
        AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.delete_dialog, null);
        deleteDialog.setView(view);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDeleteFile = view.findViewById(R.id.txt_filename);
        TextView text = view.findViewById(R.id.txt_text);
        text.setText("This file can be delete permanently.");
        mDialogCancel = view.findViewById(R.id.mCancel);
        mDialogDelete = view.findViewById(R.id.mDelete);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.show();
        File file = new File(baseModel.getBucketPath());
        mDeleteFile.setText(file.getName());
        mDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                DeleteAction(file);
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
            if (arrayList.size() == 0) {
                mainBinding.noData.setVisibility(View.VISIBLE);
                mainBinding.bottomNavigationViewLinear.setVisibility(View.GONE);
                mainBinding.viewPager.setVisibility(View.GONE);
            }

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

    @Override
    public void onResume() {
        super.onResume();
        if (Util.IsUpdate) {
            if (getActivity() != null) {
                Util.IsUpdate = false;
                new AsynchLoadData().execute((Void[]) null);
            }
        }
    }

    public void sortingAlbumList(final ArrayList<BaseModel> videoList) {

        try {
            int sort = 0;
            if (SharedPref.getSortingType(getActivity()).equals("TITLE")
                    && SharedPref.getSortingType1(getActivity()).equals("ASC")) {
                sort = 0;
            } else if (SharedPref.getSortingType(getActivity()).equals("TITLE")
                    && SharedPref.getSortingType1(getActivity()).equals("DESC")) {
                sort = 1;
            } else if (SharedPref.getSortingType(getActivity()).equals("DATE")
                    && SharedPref.getSortingType1(getActivity()).equals("ASC")) {
                sort = 2;
            } else if (SharedPref.getSortingType(getActivity()).equals("DATE")
                    && SharedPref.getSortingType1(getActivity()).equals("DESC")) {
                sort = 3;
            } else if (SharedPref.getSortingType(getActivity()).equals("LENGTH")
                    && SharedPref.getSortingType1(getActivity()).equals("ASC")) {
                sort = 4;
            } else if (SharedPref.getSortingType(getActivity()).equals("LENGTH")
                    && SharedPref.getSortingType1(getActivity()).equals("DESC")) {
                sort = 5;
            } else if (SharedPref.getSortingType(getActivity()).equals("PATH")
                    && SharedPref.getSortingType1(getActivity()).equals("ASC")) {
                sort = 6;
            } else if (SharedPref.getSortingType(getActivity()).equals("PATH")
                    && SharedPref.getSortingType1(getActivity()).equals("DESC")) {
                sort = 7;
            } else if (SharedPref.getSortingType(getActivity()).equals("TYPE")
                    && SharedPref.getSortingType1(getActivity()).equals("ASC")) {
                sort = 8;
            } else if (SharedPref.getSortingType(getActivity()).equals("TYPE")
                    && SharedPref.getSortingType1(getActivity()).equals("DESC")) {
                sort = 9;
            } else if (SharedPref.getSortingType(getActivity()).equals("SIZE")
                    && SharedPref.getSortingType1(getActivity()).equals("ASC")) {
                sort = 10;
            } else if (SharedPref.getSortingType(getActivity()).equals("SIZE")
                    && SharedPref.getSortingType1(getActivity()).equals("DESC")) {
                sort = 11;
            }

//            Log.e("Sort :",String.valueOf(sort));
            if (videoList.size() > 0) {
                switch (sort) {

                    case 0:
                        // Title Ascending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {
                                return mediaFileListModel.getName().toLowerCase().compareTo(t1.getName().toLowerCase()); // Name wise
                            }
                        });
                        break;

                    case 1:
                        // Title Descending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {
                                return mediaFileListModel.getName().toLowerCase().compareTo(t1.getName().toLowerCase()); // Name wise
                            }
                        });
                        Collections.reverse(videoList);
                        break;

                    case 2:
                        // Date Ascending
                        Collections.sort(videoList, new Comparator<BaseModel>() {

                            @Override
                            public int compare(BaseModel t2, BaseModel t1) {

                                File file = new File(t2.getBucketPath());
                                String lastModDate = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                                Date arg0Date = null;
                                try {
                                    arg0Date = Util.format.parse(lastModDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                file = new File(t1.getBucketPath());
                                String lastModDate2 = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                                Date arg1Date = null;
                                try {
                                    arg1Date = Util.format.parse(lastModDate2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return arg0Date.compareTo(arg1Date);
                            }
                        });

                        break;

                    case 3:
                        // Date Descending
                        Collections.sort(videoList, new Comparator<BaseModel>() {

                            @Override
                            public int compare(BaseModel t2, BaseModel t1) {

                                File file = new File(t2.getBucketPath());
                                String lastModDate = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                                Date arg0Date = null;
                                try {
                                    arg0Date = Util.format.parse(lastModDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                file = new File(t1.getBucketPath());
                                String lastModDate2 = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                                Date arg1Date = null;
                                try {
                                    arg1Date = Util.format.parse(lastModDate2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return arg0Date.compareTo(arg1Date);
                            }
                        });
                        Collections.reverse(videoList);
                        break;

                    case 4:
                        // Length Ascending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                String a1 = Util.generateTime(new File(t1.getBucketPath()));
                                String a2 = Util.generateTime(new File(t2.getBucketPath()));
                                return a1.compareTo(a2);
                            }
                        });
                        break;

                    case 5:
                        // Length Descending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                String a1 = Util.generateTime(new File(t1.getBucketPath()));
                                String a2 = Util.generateTime(new File(t2.getBucketPath()));
                                return a1.compareTo(a2);
                            }
                        });
                        Collections.reverse(videoList);
                        break;

                    case 6:
                        // Path Ascending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                return t1.getPath().toLowerCase().compareTo(t2.getPath().toLowerCase()); // Name wise
                            }
                        });
                        break;

                    case 7:
                        // Path Ascending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                return t1.getPath().toLowerCase().compareTo(t2.getPath().toLowerCase()); // Name wise
                            }
                        });
                        Collections.reverse(videoList);
                        break;

                    case 8:
//                        Type Ascending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                String s1 = FilenameUtils.getExtension(t1.getName());
                                String s2 = FilenameUtils.getExtension(t2.getName());
                                return s1.toLowerCase().compareTo(s2.toLowerCase()); // Name wise
                            }
                        });
                        break;

                    case 9:
                        //                        Type Descending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                String s1 = FilenameUtils.getExtension(t1.getName());
                                String s2 = FilenameUtils.getExtension(t2.getName());
                                return s1.toLowerCase().compareTo(s2.toLowerCase()); // Name wise
                            }
                        });
                        Collections.reverse(videoList);
                        break;

                    case 10:
//                        Size Ascending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                File f1 = new File(t1.getBucketPath());
                                File f2 = new File(t2.getBucketPath());
                                return Long.valueOf(f1.length()).compareTo(f2.length());
                            }
                        });
                        break;

                    case 11:
                        //                        Size Descending
                        Collections.sort(videoList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t1, BaseModel t2) {
                                File f1 = new File(t1.getBucketPath());
                                File f2 = new File(t2.getBucketPath());
                                return Long.valueOf(f1.length()).compareTo(f2.length());
                            }
                        });
                        Collections.reverse(videoList);
                        break;
                }


                for (int i = 0; i < arrayList.size(); i++) {
                    videoAdapter.add(i, arrayList.get(i));
                }


//                videoAdapter.Addall(arrayList);
//                videoBinding.mVideoRecycler.setAdapter(videoAdapter);
//                videoAdapter.notifyDataSetChanged();


            }
        } catch (Exception w) {
            System.out.println("Sorting Error=>" + w.getMessage());
        }
    }

    public void ChangeName(String newName) {

        File from = new File(baseModel.getBucketPath());
        String extension = from.getAbsolutePath().substring(from.getAbsolutePath().lastIndexOf("."));
        String FolderPath = from.getParentFile().getAbsolutePath();
        File to = new File(FolderPath + "/" + newName + extension);
        boolean isRename = from.renameTo(to);
        if (isRename) {

            ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
            resolver.delete(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA +
                            " =?", new String[]{from.getAbsolutePath()});
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(to));
            getActivity().getApplicationContext().sendBroadcast(intent);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scanPhoto(to.toString());
                }
            });
            database.renameHistory(from.getAbsolutePath(), to.getAbsolutePath());
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
            Toasty.success(getActivity(), "Rename Successfully!", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT, true).show();
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
        mTitle.setText(baseModel.getName());
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertadd.dismiss();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNewName.getText() != null)
                    ChangeName(mNewName.getText().toString());
                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(getActivity(), new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj", "scan completed");
            }
        });
        this.msConn.connect();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    }

    public class AsynchLoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                MainActivity.startAnim();
                arrayList.clear();
                arrayVideoList.clear();
                arrayFilterList.clear();
                videoAdapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            arrayList = Util.GetAllVideos(getActivity());
//            Log.e("All video", String.valueOf(arrayList.size()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (arrayList != null && arrayList.size() > 0) {
                    mainBinding.noData.setVisibility(View.GONE);
                    mainBinding.bottomNavigationViewLinear.setVisibility(View.VISIBLE);
                    mainBinding.viewPager.setVisibility(View.VISIBLE);
                    if (Util.VIEW_TYPE.equals("GRID")) {
                        videoBinding.mVideoRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        videoBinding.mVideoRecycler.setLayoutAnimation(null);
                    } else {
                        videoBinding.mVideoRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    }
                    videoBinding.mVideoRecycler.setAdapter(videoAdapter);
                    sortingAlbumList(arrayList);
                } else {
                    mainBinding.noData.setVisibility(View.VISIBLE);
                    mainBinding.bottomNavigationViewLinear.setVisibility(View.GONE);
                    mainBinding.viewPager.setVisibility(View.GONE);
                }
                MainActivity.stopAnim();
            } catch (Exception e) {
            }
        }
    }
}