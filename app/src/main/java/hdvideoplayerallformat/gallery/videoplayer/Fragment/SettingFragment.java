package hdvideoplayerallformat.gallery.videoplayer.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import es.dmoral.toasty.Toasty;
import hdvideoplayerallformat.gallery.videoplayer.Activity.PolicyActivity;
import hdvideoplayerallformat.gallery.videoplayer.BuildConfig;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.SharedPrefrences.SharedPref;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.FragmentSettingBinding;

import static hdvideoplayerallformat.gallery.videoplayer.Fragment.FolderFragment.folderBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentSettingBinding settingBinding;
    View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        settingBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_setting, container, false);
        view = settingBinding.getRoot();
        Util.IsRecentPlay = SharedPref.getRecentPlay(getActivity());
        Util.IsAutoPlay = SharedPref.getAutoPlay(getActivity());
        Util.Orientation = SharedPref.getOrientation(getActivity());

        settingBinding.mOrientationMode.setText(Util.Orientation);
        settingBinding.mSwitch2.setChecked(Util.IsRecentPlay);
        if (Util.IsRecentPlay) {
            settingBinding.mRecentMode.setText("On");
        } else {
            settingBinding.mRecentMode.setText("Off");
        }
        settingBinding.mSwitch3.setChecked(Util.IsAutoPlay);
        if (Util.IsAutoPlay) {
            settingBinding.mAutoPlayMode.setText("On");
        } else {
            settingBinding.mAutoPlayMode.setText("Off");
        }

        settingBinding.mRecentRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingBinding.mSwitch2.isChecked()) {
                    settingBinding.mSwitch2.setChecked(false);
                    settingBinding.mRecentMode.setText("Off");
                    Util.IsRecentPlay = false;
                    folderBinding.mRecentRL.setVisibility(View.GONE);
                } else {
                    settingBinding.mSwitch2.setChecked(true);
                    settingBinding.mRecentMode.setText("On");
                    Util.IsRecentPlay = true;
                    folderBinding.mRecentRL.setVisibility(View.VISIBLE);
                }
                folderBinding.mScrollView.fullScroll(ScrollView.FOCUS_UP);
                SharedPref.setRecentPlay(getActivity(), Util.IsRecentPlay);
            }
        });

        settingBinding.mAutoPlayRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingBinding.mSwitch3.isChecked()) {
                    settingBinding.mSwitch3.setChecked(false);
                    settingBinding.mAutoPlayMode.setText("Off");
                    Util.IsAutoPlay = false;
                } else {
                    settingBinding.mSwitch3.setChecked(true);
                    settingBinding.mAutoPlayMode.setText("On");
                    Util.IsAutoPlay = true;
                }
                SharedPref.setAutoPlay(getActivity(), Util.IsAutoPlay);
            }
        });

        settingBinding.mOrientationRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrientationDialog();
            }
        });

        settingBinding.mRateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        settingBinding.mFeedbackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

        settingBinding.mUpdateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        settingBinding.mPrivacyPolicyRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PolicyActivity.class);
                startActivity(intent);
            }
        });

        settingBinding.mVersion.setText(BuildConfig.VERSION_NAME);

        return view;
    }


    public void sendFeedback() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + "admin@hotmail.com"));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toasty.error(getActivity().getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT, true).show();

        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getActivity().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public void OrientationDialog() {
        Util.Orientation = SharedPref.getOrientation(getActivity());

        AlertDialog alertadd = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.orientation_dialog, null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TextView cancel = view.findViewById(R.id.mCancel);
        RadioGroup group = view.findViewById(R.id.mRadioGroup);
        RadioButton btn1 = view.findViewById(R.id.btn1);
        RadioButton btn2 = view.findViewById(R.id.btn2);
        RadioButton btn3 = view.findViewById(R.id.btn3);
        if (btn1.getText().equals(Util.Orientation)) {
            btn1.setChecked(true);
        } else if (btn2.getText().equals(Util.Orientation)) {
            btn2.setChecked(true);
        } else {
            btn3.setChecked(true);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                Util.Orientation = String.valueOf(radioButton.getText());
                SharedPref.setOrientation(getActivity(), Util.Orientation);
                settingBinding.mOrientationMode.setText(Util.Orientation);
                alertadd.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertadd.dismiss();
            }
        });

        alertadd.show();
    }
}