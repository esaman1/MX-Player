package hdvideoplayerallformat.gallery.videoplayer.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import hdvideoplayerallformat.gallery.videoplayer.Adapter.RecentFolderAdapter;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Class.Database_Helper;
import hdvideoplayerallformat.gallery.videoplayer.Class.EqualizerModel;
import hdvideoplayerallformat.gallery.videoplayer.Interface.PlayerInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.SharedPrefrences.SharedPref;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.ActivityPlayerBinding;
import io.hamed.floatinglayout.FloatingLayout;
import io.hamed.floatinglayout.callback.FloatingListener;
import tcking.github.com.giraffeplayer2.PlayerListener;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tv.danmaku.ijk.media.player.IjkTimedText;

import static hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment.mp;

public class PlayerActivity extends AppCompatActivity {

    public static ArrayList<BaseModel> mainList;
    public static int position = 0;
    public static ActivityPlayerBinding playerBinding;
    public static VideoView videoView;
    public static FloatingLayout floatingLayout;
    public MediaScannerConnection msConn;
    boolean show = true;
    RecentFolderAdapter recentAdapter;
    Database_Helper database;
    PlayerInterface anInterface;
    BottomSheetBehavior mBottomSheetBehavior1;
    Equalizer mEqualizer;
    TabLayout mTabLayout;
    BassBoost bassBoost;
    Virtualizer virtualizer;
    PresetReverb presetReverb;
    Spinner mSpinner;
    SeekBar mBassSp, mVirtulizerSp;
    TextView mBassPer, mVirPer;
    SwitchCompat mSwitch;
    FrameLayout mBlankRl;
    TextView eText;
    AlertDialog deleteDialog;
    SeekBar seekBar1;
    int pos;
    Intent activityIntent = null;
    MediaPlayer mediaPlayer;
    int newPosition = 0;
    private final FloatingListener floatingListener = new FloatingListener() {
        @Override
        public void onCreateListener(View view) {
            videoView = view.findViewById(R.id.video_view);
            videoView.setVideoPath(mainList.get(position).getBucketPath());
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer = mp;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mediaPlayer.seekTo(newPosition, MediaPlayer.SEEK_CLOSEST);
                    } else {
                        mediaPlayer.seekTo(newPosition);
                    }
                }
            });

            videoView.start();

            ImageView fullscreen = view.findViewById(R.id.mFullscreen);
            fullscreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = videoView.getCurrentPosition();
                    floatingLayout.destroy();
                    PlayerActivity playerActivity = new PlayerActivity();
                    playerActivity.SetList(mainList, position);
                    Intent in = new Intent(PlayerActivity.this, PlayerActivity.class);
                    in.putExtra("Seek", pos);
//                    Log.e("Seek", String.valueOf(pos));
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                }
            });

            ImageView close = view.findViewById(R.id.mClose);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingLayout.destroy();
                }
            });
        }

        @Override
        public void onCloseListener() {

        }
    };
    int mSpinItem = 0;
    SeekBar seekBar2;
    int betweenSpace = 10;
    SeekBar[] seekBarFinal = new SeekBar[5];
    SeekBar seekBar3;
    SeekBar seekBar4;
    SeekBar seekBar5;
    TextView tvPath, tvDuration, tvSize, tvVideoPath, tvVideoDate;
    private MyReceiver myReceiver;

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(PlayerActivity.this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
//                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
//                Log.i("msClient obj", "scan completed");
            }
        });
        msConn.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerBinding.videoView.getPlayer() != null) {
            playerBinding.videoView.getPlayer().onActivityPaused();
        }
    }

    public File CaptureImage(Uri uri) {

        Bitmap bitmap = null;
        File f = null;
        try {
            // create bitmap screen capture
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (Exception e) {
                //handle exception
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            //----------------dsestination path--------
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File myDir = new File(root, "DCIM");
            myDir.mkdirs();

            File myDir1 = new File(myDir, "MX Screenshot");
            myDir1.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Uri destination = Uri.fromFile(new File(myDir1.getPath() + "/" + timeStamp + ".png"));
            String NewImagePath = destination.getPath();

            //-----------------------------------------
            if (NewImagePath != null) {
                f = new File(NewImagePath);
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //------------insert into media list----
                File newfilee = new File(destination.getPath());
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, destination.getPath());
                values.put(MediaStore.Images.Media.DATE_TAKEN, newfilee.lastModified());
                scanPhoto(newfilee.getPath());
                Toasty.success(PlayerActivity.this, "Image saved successfully!!!", Toast.LENGTH_SHORT, true).show();

                Uri uri1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri1 = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", newfilee);
                } else {
                    uri1 = Uri.fromFile(newfilee);
                }
                getContentResolver().notifyChange(uri1, null);

            }
        } catch (Exception e) {
        }
        return f;

    }

    public void SetList(ArrayList<BaseModel> pathlist, int position) {
        try {
            mainList = pathlist;
            PlayerActivity.position = position;
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerBinding.videoView.getPlayer() != null) {
            playerBinding.videoView.getPlayer().onActivityDestroyed();
        }
        LocalBroadcastManager.getInstance(PlayerActivity.this).unregisterReceiver(myReceiver);
    }

    public void bottomHandler() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }, 5000);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setupEqualizerFxAndUI(View view) {

        short bands = mEqualizer.getNumberOfBands();

        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;

            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000) + " Hz");

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minEQLevel / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + " dB");


            SeekBar seekBar = new SeekBar(getBaseContext());
            TextView textView = new TextView(getBaseContext());
            TextView textView1 = new TextView(getBaseContext());
            switch (i) {
                case 0:
                    seekBar = view.findViewById(R.id.seekBar1);
                    textView = view.findViewById(R.id.mHz1);
                    textView1 = view.findViewById(R.id.mDb1);
                    break;
                case 1:
                    seekBar = view.findViewById(R.id.seekBar2);
                    textView = view.findViewById(R.id.mHz2);
                    textView1 = view.findViewById(R.id.mDb2);
                    break;
                case 2:
                    seekBar = view.findViewById(R.id.seekBar3);
                    textView = view.findViewById(R.id.mHz3);
                    textView1 = view.findViewById(R.id.mDb3);
                    break;
                case 3:
                    seekBar = view.findViewById(R.id.seekBar4);
                    textView = view.findViewById(R.id.mHz4);
                    textView1 = view.findViewById(R.id.mDb4);
                    break;
                case 4:
                    seekBar = view.findViewById(R.id.seekBar5);
                    textView = view.findViewById(R.id.mHz5);
                    textView1 = view.findViewById(R.id.mDb5);
                    break;
            }
            seekBarFinal[i] = seekBar;
            seekBar.setId(i);
            seekBar.setMax(maxEQLevel - minEQLevel);
            seekBar.setProgress(mEqualizer.getBandLevel(band));

            textView.setText(freqTextView.getText());
            textView.setTextColor(Color.WHITE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            textView1.setText(maxDbTextView.getText());
            textView1.setTextColor(Color.WHITE);
            textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    mTabLayout.setScrollPosition(0, 0f, true);
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerBinding.videoView.getPlayer() != null) {
            playerBinding.videoView.getPlayer().onActivityResumed();
        }
    }

    public void foreward() {
        if (position < mainList.size() - 1) {
            if (playerBinding.videoView.getPlayer().isPlaying()) {
                playerBinding.videoView.getPlayer().release();
                playerBinding.videoView.getPlayer().stop();

                position = position + 1;
                playerBinding.mTitle.setText(mainList.get(position).getName());
                playerBinding.videoView.setVideoPath(mainList.get(position).getBucketPath());
                playerBinding.videoView.getPlayer().start();
                database.insertHistoryData(mainList.get(position));
            }
        } else {
            onBackPressed();
        }
    }

    public void backward() {
        if (position > 0) {
            if (playerBinding.videoView.getPlayer().isPlaying()) {
                playerBinding.videoView.getPlayer().release();
                playerBinding.videoView.getPlayer().stop();

                position = position - 1;
                playerBinding.mTitle.setText(mainList.get(position).getName());
                playerBinding.videoView.setVideoPath(mainList.get(position).getBucketPath());
                playerBinding.videoView.getPlayer().start();
                database.insertHistoryData(mainList.get(position));
            }
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerBinding = DataBindingUtil.setContentView(PlayerActivity.this, R.layout.activity_player);
        database = new Database_Helper(PlayerActivity.this);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(playerBinding.mBottomRL);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (floatingLayout != null) {
            floatingLayout.destroy();
        }
        Util.IsAutoPlay = SharedPref.getAutoPlay(PlayerActivity.this);
        Util.Orientation = SharedPref.getOrientation(PlayerActivity.this);
        anInterface = new PlayerInterface() {
            @Override
            public void onBottomItemClick(BaseModel model) {
                if (playerBinding.videoView.getPlayer().isPlaying()) {
                    playerBinding.videoView.getPlayer().release();
                    playerBinding.videoView.getPlayer().stop();
                    playerBinding.mTitle.setText(model.getName());
                    playerBinding.videoView.setVideoPath(model.getBucketPath());
                    playerBinding.videoView.getPlayer().start();
                    database.insertHistoryData(model);
                }
            }
        };
        recentAdapter = new RecentFolderAdapter(PlayerActivity.this, "Player", anInterface);
        playerBinding.mBottomRecycler.setLayoutManager(new LinearLayoutManager(PlayerActivity.this, RecyclerView.HORIZONTAL, false));
        if (mainList != null && mainList.size() > 0) {
            recentAdapter.Addall(mainList);
            playerBinding.mBottomRecycler.setAdapter(recentAdapter);
            recentAdapter.notifyDataSetChanged();
        }
        database.insertHistoryData(mainList.get(position));
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(PlayerActivity.this).registerReceiver(myReceiver,
                new IntentFilter("TAG_SHOW"));
        LocalBroadcastManager.getInstance(PlayerActivity.this).registerReceiver(myReceiver,
                new IntentFilter("TAG_HIDE"));
        LocalBroadcastManager.getInstance(PlayerActivity.this).registerReceiver(myReceiver,
                new IntentFilter("FORWARD"));
        LocalBroadcastManager.getInstance(PlayerActivity.this).registerReceiver(myReceiver,
                new IntentFilter("BACKWARD"));

        pos = getIntent().getIntExtra("Seek", 0);
        activityIntent = getIntent();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
                mp = new MediaPlayer();
            }
        }
        File file = new File(mainList.get(position).getBucketPath());
        playerBinding.mTitle.setText(file.getName());
        playerBinding.videoView.setVideoPath(mainList.get(position).getBucketPath());

        int selectedAspectRatio = VideoInfo.AR_ASPECT_WRAP_CONTENT;
        playerBinding.videoView.getPlayer().aspectRatio(selectedAspectRatio);

        playerBinding.videoView.getPlayer().start();

        if (Util.Orientation.equals("Sensor")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else if (Util.Orientation.equals("Portrait")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        playerBinding.videoView.getVideoInfo().setBgColor(Color.BLACK).setAspectRatio(VideoInfo.AR_ASPECT_WRAP_CONTENT);//config player
        playerBinding.videoView.setPlayerListener(new PlayerListener() {
            public void onPrepared(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer) {
                if (pos >= 1000) {
                    playerBinding.videoView.getPlayer().seekTo(pos);
                    pos = 0;
                }
            }

            @Override
            public void onBufferingUpdate(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer, int percent) {

            }

            @Override
            public boolean onInfo(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer, int what, int extra) {
                return false;
            }

            @Override
            public void onCompletion(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer) {

                if (Util.isShuffle) {
                    Collections.shuffle(mainList);
                }

                if (Util.isRepeat.equals("one")) {

                    playerBinding.mTitle.setText(mainList.get(position).getName());
                    playerBinding.videoView.setVideoPath(mainList.get(position).getBucketPath());
                    playerBinding.videoView.getPlayer().start();
                    database.insertHistoryData(mainList.get(position));
                    if (deleteDialog != null) {
                        deleteDialog.dismiss();
                    }
                } else if (Util.isRepeat.equals("all")) {
                    if (position == mainList.size() - 1) {
                        position = 0;
                    } else {
                        position = position + 1;
                    }
                    playerBinding.mTitle.setText(mainList.get(position).getName());
                    playerBinding.videoView.setVideoPath(mainList.get(position).getBucketPath());
                    playerBinding.videoView.getPlayer().start();
                    database.insertHistoryData(mainList.get(position));
                    if (deleteDialog != null) {
                        deleteDialog.dismiss();
                    }
                } else {
                    if (Util.IsAutoPlay) {
                        if (position != mainList.size() - 1) {
                            position = position + 1;
                            playerBinding.mTitle.setText(mainList.get(position).getName());
                            playerBinding.videoView.setVideoPath(mainList.get(position).getBucketPath());
                            playerBinding.videoView.getPlayer().start();
                            database.insertHistoryData(mainList.get(position));
                            if (deleteDialog != null) {
                                deleteDialog.dismiss();
                            }
                        }
                    } else {
                        onBackPressed();
                    }
                }
            }

            @Override
            public void onSeekComplete(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer) {

            }

            @Override
            public boolean onError(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer, int what, int extra) {
                return false;
            }

            @Override
            public void onPause(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer) {

            }

            @Override
            public void onRelease(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer) {

            }

            @Override
            public void onStart(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer) {

            }

            @Override
            public void onTargetStateChange(int oldState, int newState) {

            }

            @Override
            public void onCurrentStateChange(int oldState, int newState) {

            }

            @Override
            public void onDisplayModelChange(int oldModel, int newModel) {

            }

            @Override
            public void onPreparing(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer) {

            }

            @Override
            public void onTimedText(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer, IjkTimedText text) {

            }

            @Override
            public void onLazyLoadProgress(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer, int progress) {

            }

            @Override
            public void onLazyLoadError(tcking.github.com.giraffeplayer2.GiraffePlayer giraffePlayer, String message) {

            }
        });

        playerBinding.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        playerBinding.mScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerBinding.videoView.getPlayer().getCurrentDisplay().setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(playerBinding.videoView.getPlayer().getCurrentDisplay().getBitmap());
                playerBinding.videoView.getPlayer().getCurrentDisplay().setDrawingCacheEnabled(false);
                Uri uri = getImageUri(PlayerActivity.this, bitmap);
                CaptureImage(uri);
            }
        });

        playerBinding.mShowAllIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomHandler();
            }
        });

        playerBinding.mEqua.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                EqualizerDialog();
            }
        });

        playerBinding.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerBinding.mOptionLL.setVisibility(View.VISIBLE);
                playerBinding.blankRL.setVisibility(View.VISIBLE);
            }
        });

        playerBinding.blankRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                playerBinding.mOptionLL.setVisibility(View.GONE);
                playerBinding.blankRL.setVisibility(View.GONE);
                return false;
            }
        });

        playerBinding.mRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
                if (deleteDialog != null && deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }
            }
        });

        playerBinding.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerBinding.mOptionLL.setVisibility(View.GONE);
                playerBinding.blankRL.setVisibility(View.GONE);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mainList.get(position).getBucketPath())));
                startActivity(Intent.createChooser(sharingIntent, "Share Via"));
            }
        });

        playerBinding.mPopupPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playerBinding.mOptionLL.setVisibility(View.GONE);
                playerBinding.blankRL.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(PlayerActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 100);
                    } else {
                        floatingLayout = new FloatingLayout(PlayerActivity.this, R.layout.popup);
                        floatingLayout.setFloatingListener(floatingListener);
                        floatingLayout.create();
                        if (playerBinding.videoView.getPlayer().isPlaying()) {
                            newPosition = playerBinding.videoView.getPlayer().getCurrentPosition();
                            onBackPressed();
                        }
                    }
                } else {
                    floatingLayout = new FloatingLayout(PlayerActivity.this, R.layout.popup);
                    floatingLayout.setFloatingListener(floatingListener);
                    floatingLayout.create();
                    if (playerBinding.videoView.getPlayer().isPlaying()) {
                        newPosition = playerBinding.videoView.getPlayer().getCurrentPosition();
                        onBackPressed();
                    }
                }
            }
        });

        playerBinding.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                playerBinding.mOptionLL.setVisibility(View.GONE);
                playerBinding.blankRL.setVisibility(View.GONE);
                if (!isChecked) {
                    playInBackground(playerBinding.videoView.getPlayer().getCurrentPosition());
                    onBackPressed();
                }
            }
        });


        playerBinding.mProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerBinding.mOptionLL.setVisibility(View.GONE);
                playerBinding.blankRL.setVisibility(View.GONE);
                PropertyDialog();
            }
        });


        playerBinding.mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isRepeat.equals("off")) {
                    Util.isRepeat = "one";
                    playerBinding.mRepeat.setImageResource(R.drawable.ic_repeat_one);
                    Toasty.info(PlayerActivity.this, "Repeat One", Toast.LENGTH_SHORT, true).show();
                } else if (Util.isRepeat.equals("one")) {
                    Util.isRepeat = "all";
                    playerBinding.mRepeat.setImageResource(R.drawable.ic_repeat_all);
                    Toasty.info(PlayerActivity.this, "Repeat All", Toast.LENGTH_SHORT, true).show();
                } else {
                    Util.isRepeat = "off";
                    playerBinding.mRepeat.setImageResource(R.drawable.ic_repeat);
                    Toasty.info(PlayerActivity.this, "Repeat Off", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        playerBinding.mSuffle.setOnClickListener(v -> {
            if (Util.isShuffle) {
                Util.isShuffle = false;
                v.setSelected(false);
                Toasty.info(PlayerActivity.this, "Shuffle Off", Toast.LENGTH_SHORT, true).show();

            } else {
                Util.isShuffle = true;
                v.setSelected(true);
                Toasty.info(PlayerActivity.this, "Shuffle On", Toast.LENGTH_SHORT, true).show();
            }
        });

        playerBinding.mMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    playerBinding.videoView.getPlayer().setMute(false);
                } else {
                    v.setSelected(true);
                    playerBinding.videoView.getPlayer().setMute(true);
                }
            }
        });


    }

    public void PropertyDialog() {
        AlertDialog alertadd = new AlertDialog.Builder(PlayerActivity.this).create();
        LayoutInflater factory = LayoutInflater.from(PlayerActivity.this);
        final View view = factory.inflate(R.layout.properties_dialog, null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tvPath = view.findViewById(R.id.tvPath);
        tvDuration = view.findViewById(R.id.tvDuration);
        tvSize = view.findViewById(R.id.tvSize);
        tvVideoPath = view.findViewById(R.id.tvVideoPath);
        tvVideoDate = view.findViewById(R.id.tvVideoDate);

        File file = new File(mainList.get(position).getBucketPath());
        tvPath.setText(file.getName());
        tvDuration.setText(Util.generateTime(file));
        tvSize.setText(Util.getSize(file.length()));
        tvVideoPath.setText(file.getParentFile().getPath());
        String dateString = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
        tvVideoDate.setText(dateString);
        alertadd.show();
    }

    public void playInBackground(int pos) {
        Toasty.info(PlayerActivity.this, "Video play in background.", Toast.LENGTH_SHORT, true).show();

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
            mp.setDataSource(PlayerActivity.this, Uri.parse(mainList.get(position).getBucketPath()));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mp.seekTo(pos, MediaPlayer.SEEK_CLOSEST);
        } else {
            mp.seekTo(pos);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                if (!Settings.canDrawOverlays(PlayerActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                }
            } else {
                floatingLayout = new FloatingLayout(PlayerActivity.this, R.layout.popup);

                floatingLayout.setFloatingListener(floatingListener);
                floatingLayout.create();
                if (playerBinding.videoView.getPlayer().isPlaying()) {
                    newPosition = playerBinding.videoView.getPlayer().getCurrentPosition();
                    onBackPressed();
                }
            }

        }

    }

    @Override
    public void onBackPressed() {

        int orientation = getResources().getConfiguration().orientation;
        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (playerBinding.mOptionLL.getVisibility() == View.VISIBLE) {
            playerBinding.mOptionLL.setVisibility(View.GONE);
            playerBinding.blankRL.setVisibility(View.GONE);
        } else if (playerBinding.videoView.getPlayer() != null) {
            super.onBackPressed();
        }
    }

    protected void showTopControl(boolean show) {
        if (show) {
            playerBinding.mTopLayer.setVisibility(View.VISIBLE);
            playerBinding.mSideOptions.setVisibility(View.VISIBLE);
            playerBinding.mSideOptions1.setVisibility(View.VISIBLE);
        } else {
            playerBinding.mSideOptions.setVisibility(View.GONE);
            playerBinding.mSideOptions1.setVisibility(View.GONE);
            if (playerBinding.mOptionLL.getVisibility() == View.GONE) {
                playerBinding.mTopLayer.setVisibility(View.GONE);
            } else {
                playerBinding.mTopLayer.setVisibility(View.VISIBLE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void EqualizerDialog() {
        deleteDialog = new AlertDialog.Builder(PlayerActivity.this).create();
        LayoutInflater factory = LayoutInflater.from(PlayerActivity.this);
        final View view = factory.inflate(R.layout.equilizer_dialog, null);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        deleteDialog.setView(view);
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTabLayout = view.findViewById(R.id.tab_layout1);
        mBassSp = view.findViewById(R.id.mBassSB);
        mVirtulizerSp = view.findViewById(R.id.mVirtualizerSB);
        mBassPer = view.findViewById(R.id.mBassPer);
        mVirPer = view.findViewById(R.id.mVirPer);
        mSwitch = view.findViewById(R.id.mSwitch);
        mBlankRl = view.findViewById(R.id.mBlankRl);
        eText = view.findViewById(R.id.eText);
        mSpinner = view.findViewById(R.id.mSpinner);

        mEqualizer = new Equalizer(0, playerBinding.videoView.getPlayer().getAudioSessionId());
        mEqualizer.setEnabled(true);
        mTabLayout.addTab(mTabLayout.newTab().setText("Custom"));
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mEqualizer.getPresetName(i)));
        }
        getControls(view);
        setEqualizerDesign(view);
        setupEqualizerFxAndUI(view);
        getEquilizerModel(view);

        if (Util.IsEnable) {
            mSwitch.setChecked(true);
            mBlankRl.setVisibility(View.GONE);
            eText.setText("ON");
        } else {
            mSwitch.setChecked(false);
            mBlankRl.setVisibility(View.VISIBLE);
            eText.setText("OFF");
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Util.IsEnable = true;
                    mBlankRl.setVisibility(View.GONE);
                    eText.setText("ON");
                } else {
                    Util.IsEnable = false;
                    mBlankRl.setVisibility(View.VISIBLE);
                    eText.setText("OFF");
                }
            }
        });

        bassBoost = new BassBoost(0, playerBinding.videoView.getPlayer().getAudioSessionId());
        bassBoost.setEnabled(true);
        BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
        BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
        bassBoostSetting.strength = (1000 / 19);
        bassBoost.setProperties(bassBoostSetting);
        try {
            presetReverb = new PresetReverb(0, playerBinding.videoView.getPlayer().getAudioSessionId());
            presetReverb.setPreset(PresetReverb.PRESET_NONE);
            presetReverb.setEnabled(true);
            virtualizer = new Virtualizer(0, playerBinding.videoView.getPlayer().getAudioSessionId());
            virtualizer.setEnabled(true);
        } catch (Exception e) {
            presetReverb.setEnabled(false);
            virtualizer.setEnabled(true);
            Log.e("Virtualizer:", e.getMessage());
        }

        mBassSp.setMax(100);
        mVirtulizerSp.setMax(100);

        mBassSp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bassBoostSetting.strength = (short) (((float) 1000 / 19) * (progress));
                bassBoost.setStrength(bassBoostSetting.strength);
                mBassPer.setText(progress + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mVirtulizerSp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                short reverbPreset = (short) ((progress * 6) / 19);
                virtualizer.setStrength(reverbPreset);
                mVirPer.setText(progress + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.show();

        deleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setEqualizerModel(view);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setEqualizerDesign(View view) {


        ViewGroup slidingTabStrip = (ViewGroup) mTabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = betweenSpace;
            params.leftMargin = betweenSpace;
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {

                    if (mTabLayout.getSelectedTabPosition() != 0) {
                        mEqualizer.usePreset((short) (mTabLayout.getSelectedTabPosition() - 1));
                        short numberOfFreqBands = 5;
                        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
                        for (short i = 0; i < numberOfFreqBands; i++) {
                            seekBarFinal[i].setProgress(mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel);
                        }

                    }
                } catch (Exception e) {
                    Toasty.error(getBaseContext(), "Error while updating Equalizer", Toast.LENGTH_SHORT, true).show();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        String[] items = new String[]{"None", "Small room", "Medium room", "Large room", "Medium hall", "Large hall", "Plate"};


        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                R.layout.spinner_item,
                items);

        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);


        mSpinner.setAdapter(ad);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinItem = position;
                switch (mSpinItem) {
                    case 0:
                        presetReverb.setPreset(PresetReverb.PRESET_NONE);
                        break;
                    case 1:
                        presetReverb.setPreset(PresetReverb.PRESET_SMALLROOM);
                        break;
                    case 2:
                        presetReverb.setPreset(PresetReverb.PRESET_MEDIUMROOM);
                        break;
                    case 3:
                        presetReverb.setPreset(PresetReverb.PRESET_LARGEROOM);
                        break;
                    case 4:
                        presetReverb.setPreset(PresetReverb.PRESET_MEDIUMHALL);
                        break;
                    case 5:
                        presetReverb.setPreset(PresetReverb.PRESET_LARGEHALL);
                        break;
                    case 6:
                        presetReverb.setPreset(PresetReverb.PRESET_PLATE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpinner.setSelection(mSpinItem);
            }
        });


    }

    public void getControls(View view) {
        seekBar1 = view.findViewById(R.id.seekBar1);
        seekBar2 = view.findViewById(R.id.seekBar2);
        seekBar3 = view.findViewById(R.id.seekBar3);
        seekBar4 = view.findViewById(R.id.seekBar4);
        seekBar5 = view.findViewById(R.id.seekBar5);
    }

    public void setEqualizerModel(View view) {
        EqualizerModel model = new EqualizerModel();
        model.setEnable(Util.IsEnable);
        model.setPreset(mTabLayout.getSelectedTabPosition());

        if (mTabLayout.getSelectedTabPosition() == 0) {
            model.setVs1(seekBar1.getProgress());
            model.setVs2(seekBar2.getProgress());
            model.setVs3(seekBar3.getProgress());
            model.setVs4(seekBar4.getProgress());
            model.setVs5(seekBar5.getProgress());
        }
        model.setReverb(mSpinner.getSelectedItemPosition());
        model.setBass(mBassSp.getProgress());
        model.setVirtualizer(mVirtulizerSp.getProgress());
        SharedPref.setEqualizer(PlayerActivity.this, model);
    }

    public void getEquilizerModel(View view) {
        EqualizerModel model = SharedPref.getEqualizer(PlayerActivity.this);
        if (model != null) {
            Util.IsEnable = model.isEnable();

            mTabLayout.getTabAt(model.getPreset()).select();
            if (model.getPreset() == 0) {
                seekBar1.setProgress(model.getVs1());
                seekBar2.setProgress(model.getVs2());
                seekBar3.setProgress(model.getVs3());
                seekBar4.setProgress(model.getVs4());
                seekBar5.setProgress(model.getVs5());
            } else {
                if (mTabLayout.getSelectedTabPosition() != 0) {
                    mEqualizer.usePreset((short) (mTabLayout.getSelectedTabPosition() - 1));
                    short numberOfFreqBands = 5;
                    final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
                    for (short i = 0; i < numberOfFreqBands; i++) {
                        seekBarFinal[i].setProgress(mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel);
                    }
                }
            }

            mSpinner.setSelection(model.getReverb());
            mBassSp.setProgress(model.getBass());
            mBassPer.setText(mBassSp.getProgress() + "%");
            mVirtulizerSp.setProgress(model.getVirtualizer());
            mVirPer.setText(mVirtulizerSp.getProgress() + "%");
        }

    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("TAG_SHOW")) {
                show = true;
            } else if (intent.getAction().equals("TAG_HIDE")) {
                show = false;
            } else if (intent.getAction().equals("FORWARD")) {
                foreward();
            } else if (intent.getAction().equals("BACKWARD")) {
                backward();
            }
            showTopControl(show);

        }
    }
}