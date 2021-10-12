package hdvideoplayerallformat.gallery.videoplayer.Activity;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import hdvideoplayerallformat.gallery.videoplayer.Adapter.DuplicateAdapter;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Class.Database_Helper;
import hdvideoplayerallformat.gallery.videoplayer.Class.ParentModel;
import hdvideoplayerallformat.gallery.videoplayer.Interface.DeleteInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;
import hdvideoplayerallformat.gallery.videoplayer.databinding.ActivityDuplicateBinding;

import static hdvideoplayerallformat.gallery.videoplayer.Activity.AsyncActivity.mFinalList;

public class DuplicateActivity extends AppCompatActivity {


    public ArrayList<String> mSelectedDuplicateList = new ArrayList<>();
    ActivityDuplicateBinding duplicateBinding;
    DuplicateAdapter duplicateAdapter;
    DeleteInterface anInterface;
    BaseModel baseModel;
    int pos;
    TextView mDeleteFile;
    Database_Helper database;
    CardView mDialogDelete, mDialogCancel;
    List<String> MainList = new ArrayList<>();
    List<BaseModel> DuplicateList = new ArrayList<>();
    HashMap<String, Long> mPairlist = new HashMap<>();
    HashMap<String, Long> mCommenlist = new HashMap<>();
    List<ParentModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        duplicateBinding = DataBindingUtil.setContentView(DuplicateActivity.this, R.layout.activity_duplicate);
        database = new Database_Helper(DuplicateActivity.this);
        if (Util.VIEW_TYPE.equals("GRID")) {
            duplicateBinding.mVideoRecycler.setLayoutManager(new GridLayoutManager(DuplicateActivity.this, 2));
            duplicateBinding.mVideoRecycler.setLayoutAnimation(null);
        } else {
            duplicateBinding.mVideoRecycler.setLayoutManager(new LinearLayoutManager(DuplicateActivity.this, RecyclerView.VERTICAL, false));
        }
        anInterface = new DeleteInterface() {
            @Override
            public void onDeleteClick(BaseModel model, int i) {
                baseModel = model;
                pos = i;
                DeleteDialog("One");
            }
        };
        duplicateAdapter = new DuplicateAdapter(DuplicateActivity.this, anInterface);
        duplicateBinding.mVideoRecycler.setAdapter(duplicateAdapter);
        ParentItemList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFinalList != null && mFinalList.size() > 0) {

//            for (int i = 0; i < mFinalList.size(); i++) {
//                Log.e("Name " + mFinalList.get(i).getName() ,"Dup_size:" + mFinalList.get(i).getSize());
//            }

            Collections.sort(mFinalList, new Comparator<BaseModel>() {
                @Override
                public int compare(BaseModel mediaFileListModel, BaseModel t1) {
                    return mediaFileListModel.getName().toLowerCase().compareTo(t1.getName().toLowerCase()); // Name wise
                }
            });
            duplicateBinding.noData.setVisibility(View.GONE);
            duplicateBinding.mVideoRecycler.setVisibility(View.VISIBLE);

            duplicateAdapter.Addall(mFinalList);
            duplicateAdapter.notifyDataSetChanged();
        } else {
            duplicateBinding.noData.setVisibility(View.VISIBLE);
            duplicateBinding.mVideoRecycler.setVisibility(View.GONE);
        }

        duplicateBinding.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialog("All");
            }
        });
    }

    public void DeleteDialog(String mFrom) {
        AlertDialog deleteDialog = new AlertDialog.Builder(DuplicateActivity.this).create();
        LayoutInflater factory = LayoutInflater.from(DuplicateActivity.this);
        final View view = factory.inflate(R.layout.delete_dialog, null);
        deleteDialog.setView(view);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDeleteFile = view.findViewById(R.id.txt_filename);
        TextView text = view.findViewById(R.id.txt_text);
        mDialogCancel = view.findViewById(R.id.mCancel);
        mDialogDelete = view.findViewById(R.id.mDelete);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.show();

        if (mFrom.equals("All")) {
            text.setText("All files can be delete permanently.");
            mDeleteFile.setText(" ");
        } else {
            text.setText("This file can be delete permanently.");
            mDeleteFile.setText(baseModel.getName());
        }

        mDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                if (mFrom.equals("All")) {
//                    Log.e("Tag", "All");

                    for (int i = 0; i < itemList.size(); i++) {
                        List<BaseModel> childList = new ArrayList<>();
                        childList = itemList.get(i).getChildItemList();
                        for (int j = 0; j < childList.size(); j++) {
                            if (j > 0) {
                                mSelectedDuplicateList.add(childList.get(j).getPath());
//                                Log.e("selected File",childList.get(j).getPath());
                            }
                        }
                    }
                    DeleteAll(mSelectedDuplicateList);
                } else {
                    File file = new File(baseModel.getPath());
                    DeleteAction(file);
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

        boolean isDelete = Util.delete(DuplicateActivity.this, file);
        if (!isDelete)
            isDelete = file.delete();
        if (isDelete) {

            database.getHistoryCheck(file.getAbsolutePath());
            if (mFinalList.size() == 0) {
                duplicateBinding.noData.setVisibility(View.VISIBLE);
                duplicateBinding.mVideoRecycler.setVisibility(View.GONE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFinalList.remove(pos);
                    Util.IsUpdate = true;
                    onResume();
                }
            });
            Toasty.success(DuplicateActivity.this, "Video successfully deleted!!!", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(DuplicateActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT, true).show();
        }
    }

    public void DeleteAll(ArrayList<String> fileList) {
        int count = 0;
        for (int i = 0; i < fileList.size(); i++) {
            File file = new File(fileList.get(i));
            boolean isDelete = Util.delete(DuplicateActivity.this, file);
            if (!isDelete)
                isDelete = file.delete();
            if (isDelete) {
                count++;
                database.getHistoryCheck(file.getAbsolutePath());
            }
        }

        if (count == fileList.size()) {
            mFinalList.size();
            duplicateBinding.noData.setVisibility(View.VISIBLE);
            duplicateBinding.mVideoRecycler.setVisibility(View.GONE);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Util.IsUpdate = true;
                    onResume();
                }
            });
            Toasty.success(DuplicateActivity.this, "All video successfully deleted!!!", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(DuplicateActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT, true).show();
        }
        onBackPressed();
    }

    private List<ParentModel> ParentItemList() {
        DuplicateList.addAll(mFinalList);
        for (int i = 0; i < DuplicateList.size(); i++) {
            if (!MainList.contains(DuplicateList.get(i).getName())) {
                MainList.add(DuplicateList.get(i).getName());
//                File file=new File(String.valueOf(DuplicateList.get(i)));
                mCommenlist.put(DuplicateList.get(i).getName(), DuplicateList.get(i).getSize());
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
            String key = name;
            Long value = mPairlist.get(name);
//            Log.e("Pair ",key + " " + value);
            ParentModel item = new ParentModel(key, ChildItemList(value));
//            Log.e("Item ",item.getParentItemTitle() + " : " + item.getChildItemList());
            itemList.add(item);
        }

        return itemList;
    }


    private List<BaseModel> ChildItemList(Long size) {
        List<BaseModel> ChildItemList = new ArrayList<>();
        for (int i = 0; i < DuplicateList.size(); i++) {
            if (!ChildItemList.contains(DuplicateList.get(i))) {
                if (size == DuplicateList.get(i).getSize()) {
                    Log.e("File content:", DuplicateList.get(i).getName());
                    ChildItemList.add(DuplicateList.get(i));
                }
            }
        }
        return ChildItemList;
    }

}