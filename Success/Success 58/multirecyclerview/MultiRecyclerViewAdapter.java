package com.deffe.max.multirecyclerview;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class MultiRecyclerViewAdapter extends RecyclerView.Adapter
{
    private ArrayList<Model> dataSet;
    private Context context;
    private int total_types;

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;

        TextTypeViewHolder(View itemView)
        {
            super(itemView);

            this.textView = itemView.findViewById(R.id.text_view);
        }
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        ImageTypeViewHolder(View itemView)
        {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.image_view);
        }
    }

    public static class VideoTypeViewHolder extends RecyclerView.ViewHolder
    {
        VideoView videoView;

        VideoTypeViewHolder(View itemView)
        {
            super(itemView);

            this.videoView = itemView.findViewById(R.id.video_view);
        }
    }

    MultiRecyclerViewAdapter(ArrayList<Model> dataSet, Context context)
    {
        this.dataSet = dataSet;
        this.context = context;
        this.total_types = dataSet.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        if (viewType == Model.TEXT_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_items,parent,false);
            return new TextTypeViewHolder(view);
        }
        else if (viewType == Model.IMAGE_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_items,parent,false);
            return new ImageTypeViewHolder(view);
        }
        else if (viewType == Model.VIDEO_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_items,parent,false);
            return new VideoTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Model object = dataSet.get(position);

        if (object != null)
        {
            switch (object.type)
            {
                case Model.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).textView.setText(object.text);
                    break;

                case Model.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).imageView.setImageResource(object.data);
                    break;

                    case Model.VIDEO_TYPE:
                        ((VideoTypeViewHolder) holder).videoView.setVideoURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + object.data));
                        MediaController mediaController = new MediaController(context);
                        mediaController.setAnchorView(((VideoTypeViewHolder) holder).videoView);
                        ((VideoTypeViewHolder) holder).videoView.setMediaController(mediaController);
                        ((VideoTypeViewHolder) holder).videoView.seekTo(100);
                        break;
            }
        }
    }

    @Override
    public int getItemViewType(int position)
    {

        switch (dataSet.get(position).type)
        {
            case 0:
                return Model.TEXT_TYPE;
            case 1:
                return Model.IMAGE_TYPE;
            case 2:
                return Model.VIDEO_TYPE;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount()
    {
        return dataSet.size();
    }
}
