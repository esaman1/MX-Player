package hdvideoplayerallformat.gallery.videoplayer.Adapter;

import android.app.Activity;
import android.content.Context;
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

import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Interface.DeleteInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.Utils.Util;

public class DuplicateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Activity activity;
    ArrayList<BaseModel> arrayVideoList = new ArrayList<>();
    DeleteInterface anInterface;

    public DuplicateAdapter(Activity activity, DeleteInterface anInterface) {
        this.activity = activity;
        this.anInterface = anInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        RecyclerView.ViewHolder viewHolder = null;

        if (Util.VIEW_TYPE.equals("GRID")) {
            itemView = LayoutInflater.from(activity).inflate(R.layout.duplicate_grid, parent, false);
            viewHolder = new MyRecentViewGrid(itemView);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (params != null) {
                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                params.height = width / 2;
            }
        } else {
            itemView = LayoutInflater.from(activity).inflate(R.layout.duplicate_list, parent, false);
            viewHolder = new MyRecentViewList(itemView);
        }

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holders, int position) {
        RequestOptions options = new RequestOptions();

        BaseModel file = arrayVideoList.get(position);
        File file1 = new File(file.getPath());

        if (file1.exists()) {
            if (Util.VIEW_TYPE.equals("GRID")) {
                MyRecentViewGrid holder = (MyRecentViewGrid) holders;
                holder.mThumbnail.setClipToOutline(true);
                holder.mThumbnail.setAdjustViewBounds(true);
                Glide.with(activity)
                        .load(file.getPath())
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
                holder.mSize.setText(Util.getSize(file1.length()));

                holder.mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        anInterface.onDeleteClick(file, position);
                    }
                });

            } else {
                MyRecentViewList holder = (MyRecentViewList) holders;
                holder.mThumbnail.setClipToOutline(true);
                holder.mThumbnail.setAdjustViewBounds(true);
                Glide.with(activity)
                        .load(file.getPath())
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
                    holder.dateCV.setVisibility(View.VISIBLE);
                    String dateString = new SimpleDateFormat("dd LLL yyyy").format(new Date(file1.lastModified()));
                    holder.mVideoDate.setText(dateString);
                } else {
                    holder.dateCV.setVisibility(View.GONE);
                }

                if (Util.LengthField) {
                    holder.mVideoDuration.setVisibility(View.VISIBLE);
                    holder.mVideoDuration.setText(Util.generateTime(file1));
                } else {
                    holder.mVideoDuration.setVisibility(View.GONE);
                }

                if (Util.SizeField) {
                    holder.mVideoSize.setVisibility(View.VISIBLE);
                    holder.mVideoSize.setText(Util.getSize(file1.length()));
                } else {
                    holder.mVideoSize.setVisibility(View.GONE);
                }

                holder.mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        anInterface.onDeleteClick(file, position);
                    }
                });
            }
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

        arrayVideoList.clear();
        arrayVideoList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyRecentViewGrid extends RecyclerView.ViewHolder {

        ShapeableImageView mThumbnail;
        ImageView mDelete;
        TextView mVideoTitle, mSize;

        public MyRecentViewGrid(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.mThumbnail);
            mVideoTitle = itemView.findViewById(R.id.mVideoTitle);
            mSize = itemView.findViewById(R.id.mSize);
            mDelete = itemView.findViewById(R.id.mDelete);

        }
    }

    public class MyRecentViewList extends RecyclerView.ViewHolder {

        ShapeableImageView mThumbnail;
        TextView mVideoTitle, mVideoDate, mVideoDuration, mVideoPath, mVideoSize, mVideoExt;
        ImageView mDelete;
        CardView cardList, dateCV;

        public MyRecentViewList(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.mThumbnail);
            mVideoTitle = itemView.findViewById(R.id.mVideoTitle);
            mVideoDate = itemView.findViewById(R.id.mVideoDate);
            dateCV = itemView.findViewById(R.id.dateCV);
            mDelete = itemView.findViewById(R.id.mDelete);
            cardList = itemView.findViewById(R.id.cardList);
            mVideoDuration = itemView.findViewById(R.id.mVideoDuration);
            mVideoPath = itemView.findViewById(R.id.mVideoPath);
            mVideoSize = itemView.findViewById(R.id.mVideoSize);
            mVideoExt = itemView.findViewById(R.id.mVideoExt);
        }
    }

}
