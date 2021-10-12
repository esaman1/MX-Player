package hdvideoplayerallformat.gallery.videoplayer.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import hdvideoplayerallformat.gallery.videoplayer.Adapter.ScreenSlidePagerAdapter;
import hdvideoplayerallformat.gallery.videoplayer.Fragment.FolderFragment;
import hdvideoplayerallformat.gallery.videoplayer.Fragment.HistoryFragment;
import hdvideoplayerallformat.gallery.videoplayer.Fragment.SettingFragment;
import hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.SharedPrefrences.SharedPref;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.ActivityMainBinding;

import static hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment.hideKeyboard;
import static hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment.mBottomSheetBehavior1;
import static hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment.mp;
import static hdvideoplayerallformat.gallery.videoplayer.Fragment.VideoFragment.videoAdapter;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static ActivityMainBinding mainBinding;
    ScreenSlidePagerAdapter pagerAdapter;
    boolean IsSet = false;
    ImageView mList, mGrid;
    ImageView mTitle, mDate, mLength;
    ImageView mPath, mType, mSize;
    SegmentedButtonGroup segmentedButtonGroup;
    LinearLayout mFieldName, mFieldLength, mFieldExtension;
    LinearLayout mFieldPath, mFieldSize, mFieldDate;
    ImageView mfName, mfLength, mfExtension;
    ImageView mfPath, mfSize, mfDate;

    public static void startAnim() {
//        Log.e("start ","anim");
        mainBinding.rlProgress.setVisibility(View.VISIBLE);
        mainBinding.avi.show();
    }

    public static void stopAnim() {
//        Log.e("stop ","anim");
        mainBinding.rlProgress.setVisibility(View.GONE);
        mainBinding.avi.hide();
    }

    @Override
    public void permissionGranted() {
        if (!IsSet)
            SetFragments();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        mainBinding.mSearch.setOnClickListener(this);
        mainBinding.mClose.setOnClickListener(this);
        mainBinding.mMore.setOnClickListener(this);
        mainBinding.mRefresh.setOnClickListener(this);
        mainBinding.mViewMenu.setOnClickListener(this);

        mainBinding.blankRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainBinding.blankRL.setVisibility(View.GONE);
                mainBinding.mHomeOptionRL.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.FOLDER_BACK) {
            Util.FOLDER_BACK = false;
            mainBinding.viewPager.setCurrentItem(1, true);
        }
        if (Util.HISTORY_BACK) {
            Util.HISTORY_BACK = false;
            mainBinding.viewPager.setCurrentItem(2, true);
        }
    }

    public void SetFragments() {
        IsSet = true;
        getPrefrences();

        ArrayList<Fragment> fragList = new ArrayList<>();
        fragList.add(VideoFragment.newInstance(getString(R.string.Video), null));
        fragList.add(FolderFragment.newInstance(getString(R.string.Folder), null));
        fragList.add(HistoryFragment.newInstance(getString(R.string.History), null));
        fragList.add(SettingFragment.newInstance(getString(R.string.Setting), null));
        pagerAdapter = new ScreenSlidePagerAdapter(fragList, getSupportFragmentManager());

        mainBinding.viewPager.setAdapter(pagerAdapter);
        mainBinding.bottomNavigationViewLinear.setCurrentActiveItem(0);

        mainBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mainBinding.bottomNavigationViewLinear.setCurrentActiveItem(i);
                generalTask();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mainBinding.bottomNavigationViewLinear.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                mainBinding.viewPager.setCurrentItem(position, true);
                generalTask();
            }
        });

        mainBinding.viewPager.setOffscreenPageLimit(4);
    }

    public void generalTask() {
        if (mainBinding.viewPager.getCurrentItem() == 3) {
            mainBinding.mMore.setVisibility(View.GONE);
            mainBinding.mSearch.setVisibility(View.GONE);
        } else {
            mainBinding.mMore.setVisibility(View.VISIBLE);
            mainBinding.mSearch.setVisibility(View.VISIBLE);
        }
        if (mainBinding.viewPager.getCurrentItem() == 1) {
            mainBinding.mViewMenu.setVisibility(View.GONE);
        } else {
            mainBinding.mViewMenu.setVisibility(View.VISIBLE);
        }
        if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (FolderFragment.mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            FolderFragment.mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (HistoryFragment.mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            HistoryFragment.mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (mainBinding.viewPager.getCurrentItem() == 2) {
            mainBinding.mSearch.setVisibility(View.GONE);
        }

        videoAdapter.getFilter().filter("");
        FolderFragment.videoAdapter.getFilter().filter("");
        mainBinding.searchBar.setText("");
        Util.collapse(mainBinding.searchRL);
    }

    @Override
    public void onBackPressed() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
                Toasty.info(MainActivity.this, "Play in background stop.", Toast.LENGTH_SHORT, true).show();
            }
        } else {
            if (mainBinding.viewPager.getCurrentItem() != 0) {
                mainBinding.viewPager.setCurrentItem(0);
                mainBinding.bottomNavigationViewLinear.setCurrentActiveItem(0);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSearch: {
                mainBinding.searchBar.setText("");
                if (mainBinding.searchRL.getVisibility() == View.VISIBLE) {
                    Util.collapse(mainBinding.searchRL);
                    hideKeyboard(MainActivity.this);
                } else {
                    Util.expand(mainBinding.searchRL);
                }
            }
            break;
            case R.id.mMore: {
                mainBinding.mHomeOptionRL.setVisibility(View.VISIBLE);
                mainBinding.blankRL.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.mRefresh: {
                mainBinding.mHomeOptionRL.setVisibility(View.GONE);
                mainBinding.blankRL.setVisibility(View.GONE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetFragments();
                    }
                });
            }
            break;
            case R.id.mViewMenu: {
                mainBinding.mHomeOptionRL.setVisibility(View.GONE);
                mainBinding.blankRL.setVisibility(View.GONE);
                ViewMenu();
            }
            break;
        }
    }

    public void ViewMenu() {
        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.home_view_menu_layout, null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setListners(view);
        TextView apply = view.findViewById(R.id.mApply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.NameField == false && Util.LengthField == false && Util.ExtensionField == false && Util.PathField == false
                        && Util.SizeField == false && Util.DateField == false) {
                    Util.VIEW_TYPE = "GRID";
                }

                SharedPref.setViewType(MainActivity.this, Util.VIEW_TYPE);
                SharedPref.setSortingType(MainActivity.this, Util.SORT_BY);
                SharedPref.setSortingType1(MainActivity.this, Util.SORTING_TYPE);
                SharedPref.setFieldName(MainActivity.this, Util.NameField);
                SharedPref.setFieldLength(MainActivity.this, Util.LengthField);
                SharedPref.setFieldExtension(MainActivity.this, Util.ExtensionField);
                SharedPref.setFieldPath(MainActivity.this, Util.PathField);
                SharedPref.setFieldSize(MainActivity.this, Util.SizeField);
                SharedPref.setFieldDate(MainActivity.this, Util.DateField);
                CallBroadcast();
                alertadd.dismiss();
            }
        });

        alertadd.show();
    }

    public void setListners(View view) {
//        **************************************************

        mList = view.findViewById(R.id.mList);
        mGrid = view.findViewById(R.id.mGrid);
        mList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.VIEW_TYPE = "LIST";
                mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_selected));
                mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_deselect));
            }
        });
        mGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.VIEW_TYPE = "GRID";
                Util.NameField = true;
                Util.LengthField = false;
                Util.ExtensionField = false;
                Util.PathField = false;
                Util.SizeField = false;
                Util.DateField = false;
                mfName.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
                mfLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                mfExtension.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                mfPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                mfSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                mfDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_deselect));
                mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_select));
            }
        });

//        **************************************************

        mTitle = view.findViewById(R.id.mTitle);
        mDate = view.findViewById(R.id.mDate);
        mLength = view.findViewById(R.id.mLength);
        mPath = view.findViewById(R.id.mPath);
        mType = view.findViewById(R.id.mType);
        mSize = view.findViewById(R.id.mSize);

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.SORT_BY = "TITLE";
                mTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_select));
                mDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_deselect));
                mLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_length_deselected));
                mPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_path_deselect));
                mType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_deselect));
                mSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_size_deselect));
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.SORT_BY = "DATE";
                mTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_deselect));
                mDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_select));
                mLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_length_deselected));
                mPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_path_deselect));
                mType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_deselect));
                mSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_size_deselect));
            }
        });

        mLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.SORT_BY = "LENGTH";
                mTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_deselect));
                mDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_deselect));
                mLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_length_selected));
                mPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_path_deselect));
                mType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_deselect));
                mSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_size_deselect));
            }
        });

        mPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.SORT_BY = "PATH";
                mTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_deselect));
                mDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_deselect));
                mLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_length_deselected));
                mPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_path_select));
                mType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_deselect));
                mSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_size_deselect));
            }
        });

        mType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.SORT_BY = "TYPE";
                mTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_deselect));
                mDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_deselect));
                mLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_length_deselected));
                mPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_path_deselect));
                mType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_select));
                mSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_size_deselect));
            }
        });

        mSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.SORT_BY = "SIZE";
                mTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_deselect));
                mDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_deselect));
                mLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_length_deselected));
                mPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_path_deselect));
                mType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_deselect));
                mSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_size_select));
            }
        });

//        **************************************************
        segmentedButtonGroup = view.findViewById(R.id.buttonGroup);
        segmentedButtonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                if (position == 0) {
                    Util.SORTING_TYPE = "ASC";
                } else {
                    Util.SORTING_TYPE = "DESC";
                }
            }
        });
//        **************************************************

        mFieldName = view.findViewById(R.id.mFieldName);
        mFieldLength = view.findViewById(R.id.mFieldLength);
        mFieldExtension = view.findViewById(R.id.mFieldExtension);
        mFieldPath = view.findViewById(R.id.mFieldPath);
        mFieldSize = view.findViewById(R.id.mFieldSize);
        mFieldDate = view.findViewById(R.id.mFieldDate);

        mfName = view.findViewById(R.id.mfName);
        mfLength = view.findViewById(R.id.mfLength);
        mfExtension = view.findViewById(R.id.mfExtension);
        mfPath = view.findViewById(R.id.mfPath);
        mfSize = view.findViewById(R.id.mfSize);
        mfDate = view.findViewById(R.id.mfDate);

        mFieldName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.NameField == false) {
                    Util.NameField = true;
                    mfName.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
                } else {
                    Util.NameField = false;
                    mfName.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                }
            }
        });

        mFieldLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.LengthField == false) {
                    Util.LengthField = true;
                    mfLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
                    Util.VIEW_TYPE = "LIST";
                    mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_selected));
                    mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_deselect));
                } else {
                    Util.LengthField = false;
                    mfLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                }
            }
        });

        mFieldExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.ExtensionField == false) {
                    Util.ExtensionField = true;
                    mfExtension.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
                    Util.VIEW_TYPE = "LIST";
                    mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_selected));
                    mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_deselect));
                } else {
                    Util.ExtensionField = false;
                    mfExtension.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                }
            }
        });

        mFieldPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.PathField == false) {
                    Util.PathField = true;
                    mfPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
                    Util.VIEW_TYPE = "LIST";
                    mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_selected));
                    mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_deselect));
                } else {
                    Util.PathField = false;
                    mfPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                }
            }
        });

        mFieldSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.SizeField == false) {
                    Util.SizeField = true;
                    mfSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
                    Util.VIEW_TYPE = "LIST";
                    mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_selected));
                    mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_deselect));
                } else {
                    Util.SizeField = false;
                    mfSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                }
            }
        });

        mFieldDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.DateField == false) {
                    Util.DateField = true;
                    mfDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
                    Util.VIEW_TYPE = "LIST";
                    mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_selected));
                    mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_deselect));
                } else {
                    Util.DateField = false;
                    mfDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_deselect));
                }
            }
        });


//        **************************************************

        getPrefrences();
        if (Util.SORT_BY.equals("TITLE")) {
            mTitle.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_select));
        }
        if (Util.SORT_BY.equals("DATE")) {
            mDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_select));
        }
        if (Util.SORT_BY.equals("LENGTH")) {
            mLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_length_selected));
        }
        if (Util.SORT_BY.equals("PATH")) {
            mPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_path_select));
        }
        if (Util.SORT_BY.equals("TYPE")) {
            mType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_select));
        }
        if (Util.SORT_BY.equals("SIZE")) {
            mSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_size_select));
        }

        if (Util.VIEW_TYPE.equals("LIST")) {
            mList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_selected));
        }
        if (Util.VIEW_TYPE.equals("GRID")) {
            mGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_select));
        }

        if (Util.SORTING_TYPE.equals("ASC")) {
            segmentedButtonGroup.setPosition(0, false);
        }
        if (Util.SORTING_TYPE.equals("DESC")) {
            segmentedButtonGroup.setPosition(1, false);
        }

        if (Util.NameField == true) {
            mfName.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
        }
        if (Util.LengthField == true) {
            mfLength.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
        }
        if (Util.ExtensionField == true) {
            mfExtension.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
        }
        if (Util.PathField == true) {
            mfPath.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
        }
        if (Util.SizeField == true) {
            mfSize.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
        }
        if (Util.DateField == true) {
            mfDate.setImageDrawable(getResources().getDrawable(R.drawable.ic_field_selected));
        }
    }

    public void getPrefrences() {
        Util.VIEW_TYPE = SharedPref.getViewType(MainActivity.this);
        Util.SORT_BY = SharedPref.getSortingType(MainActivity.this);
        Util.SORTING_TYPE = SharedPref.getSortingType1(MainActivity.this);
        Util.NameField = SharedPref.getFieldName(MainActivity.this);
        Util.LengthField = SharedPref.getFieldLength(MainActivity.this);
        Util.ExtensionField = SharedPref.getFieldExtension(MainActivity.this);
        Util.PathField = SharedPref.getFieldPath(MainActivity.this);
        Util.SizeField = SharedPref.getFieldSize(MainActivity.this);
        Util.DateField = SharedPref.getFieldDate(MainActivity.this);
    }

    public void CallBroadcast() {
        Util.IsUpdate = true;
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(MainActivity.this);
        Intent localIn = new Intent("TAG_VIEW_TYPE");
        lbm.sendBroadcast(localIn);
    }


}