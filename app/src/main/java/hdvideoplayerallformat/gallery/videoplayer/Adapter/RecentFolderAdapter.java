package hdvideoplayerallformat.gallery.videoplayer.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

import hdvideoplayerallformat.gallery.videoplayer.Activity.PlayerActivity;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Interface.PlayerInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;

public class RecentFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Activity activity;
    ArrayList<BaseModel> arrayVideoList = new ArrayList<>();
    String mFrom;
    PlayerInterface anInterface;

    public RecentFolderAdapter(Activity activity, String mFrom) {
        this.activity = activity;
        this.mFrom = mFrom;
    }

    public RecentFolderAdapter(Activity activity, String mFrom, PlayerInterface anInterface) {
        this.activity = activity;
        this.mFrom = mFrom;
        this.anInterface = anInterface;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        RecyclerView.ViewHolder viewHolder = null;
        itemView = LayoutInflater.from(activity).inflate(R.layout.recent_grid, parent, false);
        viewHolder = new MyRecentViewList(itemView);

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holders, int position) {
        RequestOptions options = new RequestOptions();

        BaseModel file = arrayVideoList.get(position);
        File file1 = new File(file.getBucketPath());

        MyRecentViewList holder = (MyRecentViewList) holders;
        holder.mThumbnail.setClipToOutline(true);
        holder.mThumbnail.setAdjustViewBounds(true);
        Glide.with(activity)
                .load(file.getBucketPath())
                .apply(options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.LOW)
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .into(holder.mThumbnail);
        String fileNameWithOutExt = FilenameUtils.removeExtension(file1.getName());
        holder.mVideoTitle.setText(fileNameWithOutExt);
        try {
            holder.mVideoDuration.setText(Util.generateTime(file1));
        } catch (Exception e) {
            holder.mVideoDuration.setVisibility(View.GONE);
        }
        if (mFrom.equals("Folder")) {
            holder.mVideoTitle.setTextColor(activity.getResources().getColor(R.color.black));
        } else if (mFrom.equals("Player")) {
            holder.mVideoTitle.setTextColor(activity.getResources().getColor(R.color.white));
        }

        holder.mThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrom.equals("Folder")) {
                    PlayerActivity playerActivity = new PlayerActivity();
                    playerActivity.SetList(arrayVideoList, position);
                    Intent in = new Intent(activity, PlayerActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(in);
                } else if (mFrom.equals("Player")) {
                    anInterface.onBottomItemClick(file);
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        if (mFrom.equals("Folder") && arrayVideoList.size() > 10) {
            return 10;
        } else {
            return arrayVideoList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void Addall(ArrayList<BaseModel> list) {

        arrayVideoList.clear();
        arrayVideoList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyRecentViewList extends RecyclerView.ViewHolder {

        ShapeableImageView mThumbnail;
        TextView mVideoTitle, mVideoDuration;

        public MyRecentViewList(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.mThumbnail);
            mVideoTitle = itemView.findViewById(R.id.mVideoTitle);
            mVideoDuration = itemView.findViewById(R.id.mVideoDuration);

        }
    }

}
