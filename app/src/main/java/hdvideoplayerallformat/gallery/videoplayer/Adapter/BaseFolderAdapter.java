package hdvideoplayerallformat.gallery.videoplayer.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hdvideoplayerallformat.gallery.videoplayer.Activity.AlbumViewActivity;
import hdvideoplayerallformat.gallery.videoplayer.Class.BaseModel;
import hdvideoplayerallformat.gallery.videoplayer.Interface.BottomOptionInterface;
import hdvideoplayerallformat.gallery.videoplayer.R;

public class BaseFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    Activity activity;
    ArrayList<BaseModel> arrayList = new ArrayList<>();
    ArrayList<BaseModel> arrayFilterList = new ArrayList<>();
    BottomOptionInterface anInterface;

    public BaseFolderAdapter(Activity activity, BottomOptionInterface anInterface) {
        this.activity = activity;
        this.anInterface = anInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        RecyclerView.ViewHolder viewHolder = null;

        itemView = LayoutInflater.from(activity).inflate(R.layout.folder_all_list, parent, false);
        viewHolder = new MyClassViewList(itemView);


        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holders, int position) {

        BaseModel file = arrayFilterList.get(position);

        MyClassViewList holder = (MyClassViewList) holders;

        holder.mVideoTitle.setText(file.getBucketName());
        if (file.getPathlist().size() == 1) {
            holder.mVideoTotal.setText(file.getPathlist().size() + " Video");
        } else {
            holder.mVideoTotal.setText(file.getPathlist().size() + " Videos");
        }
        holder.mcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(activity, AlbumViewActivity.class);
                in.putExtra("BUCKET_ID", arrayFilterList.get(position).getBucketId());
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

    @Override
    public int getItemCount() {
        return arrayFilterList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void Addall(ArrayList<BaseModel> list) {

        arrayList = new ArrayList<>();
        arrayFilterList = new ArrayList<>();
        arrayList.addAll(list);
        arrayFilterList.addAll(list);

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    arrayFilterList = arrayList;
                } else {
                    ArrayList<BaseModel> filteredList1 = new ArrayList<>();
                    for (BaseModel row : arrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBucketName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList1.add(row);
                        }
                    }

                    arrayFilterList = filteredList1;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arrayFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                arrayFilterList = (ArrayList<BaseModel>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public class MyClassViewList extends RecyclerView.ViewHolder {

        ImageView mThumbnail;
        TextView mVideoTitle, mVideoTotal;
        ImageView mMore;
        CardView mcard;

        public MyClassViewList(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.mThumbnail);
            mVideoTitle = itemView.findViewById(R.id.mVideoTitle);
            mVideoTotal = itemView.findViewById(R.id.mVideoTotal);
            mMore = itemView.findViewById(R.id.mMore);
            mcard = itemView.findViewById(R.id.mCard);
        }
    }

}

