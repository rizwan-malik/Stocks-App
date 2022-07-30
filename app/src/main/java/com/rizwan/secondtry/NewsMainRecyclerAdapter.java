package com.rizwan.secondtry;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsMainRecyclerAdapter extends RecyclerView.Adapter<NewsMainRecyclerAdapter.ViewHolder> {

    List<NewsSection> newsSectionList;
    Context context;

    public NewsMainRecyclerAdapter(List<NewsSection> newsSectionList, Context context) {
        this.newsSectionList = newsSectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.news_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsSection section = newsSectionList.get(position);

        DetailsInterfaces.NewsData sectionName = section.getSectionName();
        List<DetailsInterfaces.NewsData> sectionItemsList = section.getSectionItem();

        holder.newsSectionHeadingTitle.setText(sectionName.getTitle());

        PrettyTime p = new PrettyTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String prettyfiedTime = "";
        try {
            Date standardDate = format.parse(sectionName.getPublishedAt());
            prettyfiedTime = p.format(standardDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.newsSectionHeadingTime.setText(prettyfiedTime);
        holder.newsSectionHeadingSource.setText(sectionName.getSource());
        Picasso.get().load(sectionName.getUrlToImage()).fit().centerInside().into(holder.newsSectionHeadingImage);

        NewsChildRecyclerAdapter newsChildRecyclerAdapter = new NewsChildRecyclerAdapter(sectionItemsList, context);
        holder.newsSectionChildRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.newsSectionChildRecyclerView.setAdapter(newsChildRecyclerAdapter);
        newsChildRecyclerAdapter.notifyDataSetChanged();

//        OnLongClickListener Implementation
        holder.newsSectionHeadingCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.news_dialog);
                TextView tv = v.findViewById(R.id.newsSectionHeadingTitle);
                String newsTitle = tv.getText().toString();
                TextView newsDialogText = dialog.findViewById(R.id.newsDialogText);
                newsDialogText.setText(newsTitle);

                ImageView image = (ImageView) dialog.findViewById(R.id.newsDialogImage);
                Picasso.get().load(sectionName.getUrlToImage()).fit().centerInside().into(image);

                ImageButton dialogTwitterButton = dialog.findViewById(R.id.dialogTwitterButton);

//              Twitter button on click function
                dialogTwitterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String titleText = sectionName.getTitle();
                        String urlText = sectionName.getUrl();
                        String UTFTitle = "";
                        String UTFUrl = "";
                        try {
                            UTFTitle = URLEncoder.encode(titleText, "UTF-8");
                            UTFUrl = URLEncoder.encode(urlText, "UTF-8");

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String url = "http://www.twitter.com/intent/tweet?text=" + UTFTitle + "&url=" + UTFUrl;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);
                    }
                });

                ImageButton dialogChromeButton = dialog.findViewById(R.id.dialogChromeButton);
                dialogChromeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = sectionName.getUrl();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);
                    }
                });

                dialog.show();
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return newsSectionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView newsSectionHeadingCard;
        ImageView newsSectionHeadingImage;
        TextView newsSectionHeadingSource;
        TextView newsSectionHeadingTime;
        TextView newsSectionHeadingTitle;
        RecyclerView newsSectionChildRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newsSectionHeadingImage = itemView.findViewById(R.id.newsSectionHeadingImage);
            newsSectionHeadingSource = itemView.findViewById(R.id.newsSectionHeadingSource);
            newsSectionHeadingTime = itemView.findViewById(R.id.newsSectionHeadingTime);
            newsSectionHeadingTitle = itemView.findViewById(R.id.newsSectionHeadingTitle);
            newsSectionChildRecyclerView = itemView.findViewById(R.id.newsSectionChildRecyclerView);
            newsSectionHeadingCard = itemView.findViewById(R.id.newsSectionHeadingCard);

        }
    }


}
