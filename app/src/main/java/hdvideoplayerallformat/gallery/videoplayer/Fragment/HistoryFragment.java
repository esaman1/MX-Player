package hdvideoplayerallformat.gallery.videoplayer.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
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
import hdvideoplayerallformat.gallery.videoplayer.Adapter.RecentAdapter;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Class.Database_Helper;
import hdvideoplayerallformat.gallery.videoplayer.Interface.BottomOptionInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.FragmentHistoryBinding;

import static hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment.mp;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static FragmentHistoryBinding historyBinding;
    public static BottomSheetBehavior mBottomSheetBehavior1;
    Database_Helper database;
    View view;
    RecentAdapter recentAdapter;
    BaseModel baseModel;
    TextView mDeleteFile;
    CardView mDialogDelete, mDialogCancel;
    BottomOptionInterface anInterface;
    TextView tvPath, tvDuration, tvSize, tvVideoPath, tvVideoDate;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyReceiver myReceiver;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        historyBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_history, container, false);
        view = historyBinding.getRoot();
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_VIEW_TYPE"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_REFRESH_HISTORY"));
        database = new Database_Helper(getActivity());
        mBottomSheetBehavior1 = BottomSheetBehavior.from(historyBinding.mBottomRL);
        anInterface = new BottomOptionInterface() {
            @Override
            public void onMoreClick(BaseModel model, int i) {
                baseModel = model;
//                Log.e("Model:",baseModel.getBucketPath());
                historyBinding.mTitle.setText(baseModel.getName());
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        };
        recentAdapter = new RecentAdapter(getActivity(), anInterface);


        historyBinding.mAudio.setOnClickListener(new View.OnClickListener() {
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
                    mp.setDataSource(getActivity(), Uri.parse(baseModel.getBucketPath()));
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            }
        });

        historyBinding.mShare.setOnClickListener(new View.OnClickListener() {
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

        historyBinding.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                DeleteDialog();
            }
        });

        historyBinding.mProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                PropertyDialog();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getHistory();
        }
    }

    public void getHistory() {
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

        try {
            if (folderListArray != null && folderListArray.size() > 0) {
                historyBinding.noData.setVisibility(View.GONE);
                historyBinding.mRecentRecycler.setVisibility(View.VISIBLE);
                if (Util.VIEW_TYPE.equals("GRID")) {
                    historyBinding.mRecentRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    historyBinding.mRecentRecycler.setLayoutAnimation(null);
                } else {
                    historyBinding.mRecentRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                }
                historyBinding.mRecentRecycler.setAdapter(recentAdapter);
                recentAdapter.Addall(folderListArray);
            } else {
                historyBinding.noData.setVisibility(View.VISIBLE);
                historyBinding.mRecentRecycler.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
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
        text.setText("This file can be delete from history.");
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

        database.getHistoryCheck(file.getAbsolutePath());

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
                Intent localIn = new Intent("TAG_REFRESH_HISTORY");
                lbm.sendBroadcast(localIn);

            }
        }, 1000);
        Toasty.success(getActivity(), "History successfully deleted!!!", Toast.LENGTH_SHORT, true).show();
    }

    public void PropertyDialog() {
        File file = new File(baseModel.getBucketPath());
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

        tvPath.setText(file.getName());
        tvDuration.setText(Util.generateTime(file));
        tvSize.setText(Util.getSize(file.length()));
        tvVideoPath.setText(file.getParentFile().getPath());
        String dateString = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
        tvVideoDate.setText(dateString);
        alertadd.show();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    }
}