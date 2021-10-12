package hdvideoplayerallformat.gallery.videoplayer.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import hdvideoplayerallformat.gallery.videoplayer.Adapter.AlbumVideoAdapter;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Class.Database_Helper;
import hdvideoplayerallformat.gallery.videoplayer.Interface.BottomOptionInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.ActivityAlbumViewBinding;

import static hdvideoplayerallformat.gallery.videoplayer.Adapter.AlbumVideoAdapter.arrayFilterList;
import static hdvideoplayerallformat.gallery.videoplayer.Adapter.AlbumVideoAdapter.arrayVideoList;


public class AlbumViewActivity extends AppCompatActivity {

    public static ArrayList<BaseModel> arrayList = new ArrayList<>();
    public static String bucket_id;
    public static boolean IsAlbumUpdate = false;
    public static BottomSheetBehavior mBottomSheetBehavior1;
    public MediaPlayer mp;
    AlbumVideoAdapter videoAdapter;
    ActivityAlbumViewBinding viewBinding;
    BottomOptionInterface anInterface;
    BaseModel baseModel;
    TextView mCancel, mDone;
    EditText mNewName;
    TextView mDeleteFile;
    Database_Helper database;
    CardView mDialogDelete, mDialogCancel;
    MediaScannerConnection msConn;
    TextView tvPath, tvDuration, tvSize, tvVideoPath, tvVideoDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(AlbumViewActivity.this, R.layout.activity_album_view);


        database = new Database_Helper(AlbumViewActivity.this);
        bucket_id = getIntent().getStringExtra("BUCKET_ID");


        arrayList.clear();
        arrayFilterList.clear();
        arrayVideoList.clear();
        IsAlbumUpdate = false;
        startAnim();
        arrayList = Util.getFolderVideoCover(bucket_id, AlbumViewActivity.this);

        viewBinding.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Util.VIEW_TYPE.equals("GRID")) {
            viewBinding.mVideoRecycler.setLayoutManager(new GridLayoutManager(AlbumViewActivity.this, 2));
            viewBinding.mVideoRecycler.setLayoutAnimation(null);
        } else {
            viewBinding.mVideoRecycler.setLayoutManager(new LinearLayoutManager(AlbumViewActivity.this, RecyclerView.VERTICAL, false));
        }
        mBottomSheetBehavior1 = BottomSheetBehavior.from(viewBinding.mBottomRL);
        anInterface = new BottomOptionInterface() {
            @Override
            public void onMoreClick(BaseModel model, int i) {
                baseModel = model;
                viewBinding.mTitle.setText(baseModel.getName());
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        };
        videoAdapter = new AlbumVideoAdapter(AlbumViewActivity.this, anInterface);
        viewBinding.mVideoRecycler.setAdapter(videoAdapter);
        viewBinding.mVideoRecycler.setLayoutAnimation(null);
        if (arrayList != null && arrayList.size() > 0) {
            viewBinding.mTitle.setText(arrayList.get(0).getBucketName());
            for (int i = 0; i < arrayList.size(); i++) {
                videoAdapter.add(i, arrayList.get(i));
                videoAdapter.notifyDataSetChanged();
            }
            stopAnim();
        }

        viewBinding.searchBar.addTextChangedListener(new TextWatcher() {
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

        viewBinding.mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoAdapter.getFilter().filter("");
                viewBinding.searchBar.setText("");
            }
        });

        viewBinding.mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBinding.searchBar.setText("");
                if (viewBinding.searchRL.getVisibility() == View.VISIBLE) {
                    Util.collapse(viewBinding.searchRL);
                    hideKeyboard();
                } else {
                    Util.expand(viewBinding.searchRL);
                }
            }
        });

        viewBinding.mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Toasty.info(AlbumViewActivity.this, "Play in background. Press Back key to stop.", Toast.LENGTH_SHORT, true).show();
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
                    mp.setDataSource(AlbumViewActivity.this, Uri.parse(baseModel.getBucketPath()));
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            }
        });

        viewBinding.mRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                RenameDialog();
            }
        });

        viewBinding.mShare.setOnClickListener(new View.OnClickListener() {
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

        viewBinding.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                DeleteDialog();
            }
        });

        viewBinding.mProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                PropertyDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        arrayList.clear();
        arrayFilterList.clear();
        arrayVideoList.clear();
        IsAlbumUpdate = false;
        startAnim();
        arrayList = Util.getFolderVideoCover(bucket_id, AlbumViewActivity.this);
        if (arrayList != null && arrayList.size() > 0) {
            viewBinding.mTitle.setText(arrayList.get(0).getBucketName());
            for (int i = 0; i < arrayList.size(); i++) {
                videoAdapter.add(i, arrayList.get(i));
                videoAdapter.notifyDataSetChanged();
            }
            stopAnim();
        }
    }

    public void RenameDialog() {
        AlertDialog alertadd = new AlertDialog.Builder(AlbumViewActivity.this).create();
        LayoutInflater factory = LayoutInflater.from(AlbumViewActivity.this);
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

    public void ChangeName(String newName) {

        File from = new File(baseModel.getBucketPath());
        String extension = from.getAbsolutePath().substring(from.getAbsolutePath().lastIndexOf("."));
        String FolderPath = from.getParentFile().getAbsolutePath();
        File to = new File(FolderPath + "/" + newName + extension);
        boolean isRename = from.renameTo(to);
//        Log.e("From path",from.getPath());
//        Log.e("To path",to.getPath());
        if (isRename) {

            ContentResolver resolver = getApplicationContext().getContentResolver();
            resolver.delete(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA +
                            " =?", new String[]{from.getAbsolutePath()});
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(to));
            sendBroadcast(intent);
            Util.IsUpdate = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scanPhoto(to.toString());
                }
            });
            database.renameHistory(from.getAbsolutePath(), to.getAbsolutePath());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onResume();
                }
            }, 1000);
            Toasty.success(AlbumViewActivity.this, "Rename Successfully!", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(AlbumViewActivity.this, "Something went wrong!", Toast.LENGTH_SHORT, true).show();
        }
    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(AlbumViewActivity.this, new MediaScannerConnection.MediaScannerConnectionClient() {
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

    public void PropertyDialog() {
        AlertDialog alertadd = new AlertDialog.Builder(AlbumViewActivity.this).create();
        LayoutInflater factory = LayoutInflater.from(AlbumViewActivity.this);
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
        AlertDialog deleteDialog = new AlertDialog.Builder(AlbumViewActivity.this).create();
        LayoutInflater factory = LayoutInflater.from(AlbumViewActivity.this);
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

        boolean isDelete = Util.delete(AlbumViewActivity.this, file);
        if (!isDelete)
            isDelete = file.delete();
        if (isDelete) {
            arrayList.remove(file.getAbsolutePath());
            database.getHistoryCheck(file.getAbsolutePath());
            if (arrayList.size() == 0) {
                onBackPressed();
            }

            Util.IsUpdate = true;
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    onResume();
                }
            }, 1000);
            Toasty.success(AlbumViewActivity.this, "Video successfully deleted!!!", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(AlbumViewActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT, true).show();
        }
    }

    public void hideKeyboard() {
        View view = findViewById(R.id.searchBar);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void startAnim() {
        viewBinding.rlProgress.setVisibility(View.VISIBLE);
        viewBinding.avi.show();
    }

    public void stopAnim() {
        viewBinding.rlProgress.setVisibility(View.GONE);
        viewBinding.avi.hide();
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
                Toasty.info(AlbumViewActivity.this, "Play in background stop.", Toast.LENGTH_SHORT, true).show();
            }
        } else {
            Util.FOLDER_BACK = true;
            super.onBackPressed();
        }

    }


}