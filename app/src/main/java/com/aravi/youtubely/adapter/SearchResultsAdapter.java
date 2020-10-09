package com.aravi.youtubely.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aravi.youtubely.R;
import com.aravi.youtubely.main.DownloadActivity;
import com.aravi.youtubely.model.Video;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.GrowItemViewHolder> {

    private final Context context;
    private final List<Video> searchItemList;


    public SearchResultsAdapter(Context ctx, List<Video> searchItemList) {
        this.context = ctx;
        this.searchItemList = searchItemList;
    }

    @NonNull
    @Override
    public GrowItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new GrowItemViewHolder(inflatedView);
    }


    @Override
    public void onBindViewHolder(@NonNull final GrowItemViewHolder holder, int position) {
        final Video item = searchItemList.get(position);

        Glide.with(context)
                .load(item.getThumbnails().get(0))
                .into(holder.videoThumbnail);
        holder.videoTitle.setText(item.getTitle());
        holder.videoDescription.setText(item.getLongDesc());

        holder.videoItem.setOnClickListener(view -> {
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = context.getSharedPreferences("SELECTION.DATA", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("ITEM", gson.toJson(item)).apply();
            Intent intent = new Intent(context, DownloadActivity.class);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return searchItemList.size();
    }


    class GrowItemViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout videoItem;
        final ImageView videoThumbnail;
        final TextView videoTitle, videoDescription;

        private GrowItemViewHolder(View itemView) {
            super(itemView);
            videoItem = itemView.findViewById(R.id.videoItem);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoDescription = itemView.findViewById(R.id.videoDescription);
        }
    }

}
