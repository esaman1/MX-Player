package hdvideoplayerallformat.gallery.videoplayer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hdvideoplayerallformat.gallery.videoplayer.Activity.PlayerActivity;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Interface.BottomOptionInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;

public class RecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Activity activity;
    ArrayList<BaseModel> arrayVideoList = new ArrayList<>();

    BottomOptionInterface anInterface;

    public RecentAdapter(Activity activity, BottomOptionInterface anInterface) {
        this.activity = activity;
        this.anInterface = anInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        RecyclerView.ViewHolder viewHolder = null;

        if (Util.VIEW_TYPE.equals("GRID")) {
            itemView = LayoutInflater.from(activity).inflate(R.layout.home_all_video_grid, parent, false);
            viewHolder = new MyClassView(itemView);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (params != null) {
                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                params.height = width / 2;
            }
        } else {
            itemView = LayoutInflater.from(activity).inflate(R.layout.home_all_video_list, parent, false);
            viewHolder = new MyClassViewList(itemView);
        }

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holders, int position) {
        RequestOptions options = new RequestOptions();

        BaseModel file = arrayVideoList.get(position);
        File file1 = new File(file.getBucketPath());

        if (Util.VIEW_TYPE.equals("GRID")) {
            MyClassView holder = (MyClassView) holders;
            holder.mThumbnail.setClipToOutline(true);
            holder.mThumbnail.setAdjustViewBounds(true);
            Glide.with(activity)
                    .load(file.getBucketPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(holder.mThumbnail);
            if (Util.NameField) {
                holder.mVideoTitle.setVisibility(View.VISIBLE);
                String fileNameWithOutExt = FilenameUtils.removeExtension(file1.getName());
                holder.mVideoTitle.setText(fileNameWithOutExt);
            } else {
                holder.mVideoTitle.setText("Video");
            }
            holder.mThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.HISTORY_BACK = true;
                    PlayerActivity playerActivity = new PlayerActivity();
                    playerActivity.SetList(arrayVideoList, position);
                    Intent in = new Intent(activity, PlayerActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(in);
                }
            });
            holder.mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anInterface.onMoreClick(file, position);
                }
            });
        } else {
            MyClassViewList holder = (MyClassViewList) holders;
            holder.mThumbnail.setClipToOutline(true);
            holder.mThumbnail.setAdjustViewBounds(true);
            Glide.with(activity)
                    .load(file.getBucketPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(holder.mThumbnail);

            if (Util.NameField) {
                holder.mVideoTitle.setVisibility(View.VISIBLE);
                String fileNameWithOutExt = FilenameUtils.removeExtension(file1.getName());
                holder.mVideoTitle.setText(fileNameWithOutExt);
            } else {
                holder.mVideoTitle.setText("Video");
            }

            if (Util.ExtensionField) {
                holder.mVideoExt.setVisibility(View.VISIBLE);
                holder.mVideoExt.setText("Type : " + FilenameUtils.getExtension(file1.getName()));
            } else {
                holder.mVideoExt.setVisibility(View.GONE);
            }

            if (Util.PathField) {
                holder.mVideoPath.setVisibility(View.VISIBLE);
                holder.mVideoPath.setText(file1.getParentFile().getPath());
            } else {
                holder.mVideoPath.setVisibility(View.GONE);
            }

            if (Util.DateField) {
                holder.mVideoDate.setVisibility(View.VISIBLE);
                String dateString = new SimpleDateFormat("dd LLL yyyy").format(new Date(file1.lastModified()));
                holder.mVideoDate.setText(dateString);
            } else {
                holder.mVideoDate.setVisibility(View.GONE);
            }

            if (Util.LengthField) {
                holder.mVideoDuration.setVisibility(View.VISIBLE);
                try {
                    holder.mVideoDuration.setText(Util.generateTime(file1));
                } catch (Exception e) {
                    holder.mVideoDuration.setVisibility(View.GONE);
                }
            } else {
                holder.mVideoDuration.setVisibility(View.GONE);
            }

            if (Util.SizeField) {
                holder.mVideoSize.setVisibility(View.VISIBLE);
                holder.mVideoSize.setText(Util.getSize(file1.length()));
            } else {
                holder.mVideoSize.setVisibility(View.GONE);
            }

            holder.cardList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.HISTORY_BACK = true;
                    PlayerActivity playerActivity = new PlayerActivity();
                    playerActivity.SetList(arrayVideoList, position);
                    Intent in = new Intent(activity, PlayerActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(in);
                }
            });
            holder.mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anInterface.onMoreClick(file, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {

        return arrayVideoList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void Addall(ArrayList<BaseModel> list) {

        arrayVideoList = new ArrayList<>();
        arrayVideoList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        ShapeableImageView mThumbnail;
        TextView mVideoTitle;
        ImageView mPlay, mMore;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.mThumbnail);
            mPlay = itemView.findViewById(R.id.mPlayBtn);
            mVideoTitle = itemView.findViewById(R.id.mVideoTitle);
            mMore = itemView.findViewById(R.id.mMore);

        }
    }

    public class MyClassViewList extends RecyclerView.ViewHolder {

        ShapeableImageView mThumbnail;
        TextView mVideoTitle, mVideoDate, mVideoDuration, mVideoPath, mVideoSize, mVideoExt;
        ImageView mMore;
        CardView cardList;

        public MyClassViewList(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.mThumbnail);
            mVideoTitle = itemView.findViewById(R.id.mVideoTitle);
            mVideoDate = itemView.findViewById(R.id.mVideoDate);
            mMore = itemView.findViewById(R.id.mMore);
            cardList = itemView.findViewById(R.id.cardList);
            mVideoDuration = itemView.findViewById(R.id.mVideoDuration);
            mVideoPath = itemView.findViewById(R.id.mVideoPath);
            mVideoSize = itemView.findViewById(R.id.mVideoSize);
            mVideoExt = itemView.findViewById(R.id.mVideoExt);
        }
    }

}

