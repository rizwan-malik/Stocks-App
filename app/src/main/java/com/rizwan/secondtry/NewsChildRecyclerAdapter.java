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

public class NewsChildRecyclerAdapter extends RecyclerView.Adapter<NewsChildRecyclerAdapter.ViewHolder> {

    List<DetailsInterfaces.NewsData> newsDataList;
    Context context;

    public NewsChildRecyclerAdapter(List<DetailsInterfaces.NewsData> newsDataList, Context context) {
        this.newsDataList = newsDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.newsItemTitle.setText(newsDataList.get(position).getTitle());

        PrettyTime p = new PrettyTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String prettyfiedTime = "";
        try {
            Date standardDate = format.parse(newsDataList.get(position).getPublishedAt());
            prettyfiedTime = p.format(standardDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.newsItemTime.setText(prettyfiedTime);
        holder.newsItemSource.setText(newsDataList.get(position).getSource());
        Picasso.get().load(newsDataList.get(position).getUrlToImage()).fit().centerInside().into(holder.newsItemImage);


//        OnLongClickListener Implementation
        holder.newsItemCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.news_dialog);
                TextView tv = v.findViewById(R.id.newsItemTitle);
                String newsTitle = tv.getText().toString();
                TextView newsDialogText = dialog.findViewById(R.id.newsDialogText);
                newsDialogText.setText(newsTitle);

                ImageView image = (ImageView) dialog.findViewById(R.id.newsDialogImage);
                Picasso.get().load(newsDataList.get(position).getUrlToImage()).fit().centerInside().into(image);

                ImageButton dialogTwitterButton = dialog.findViewById(R.id.dialogTwitterButton);

//              Twitter button on click function
                dialogTwitterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String titleText = newsDataList.get(position).getTitle();
                        String urlText = newsDataList.get(position).getUrl();
                        System.out.println(newsDialogText.getText().toString());
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
                        String url = newsDataList.get(position).getUrl();
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
        return newsDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView newsItemSource;
        TextView newsItemTime;
        TextView newsItemTitle;
        ImageView newsItemImage;
        CardView newsItemCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newsItemSource = itemView.findViewById(R.id.newsItemSource);
            newsItemTime = itemView.findViewById(R.id.newsItemTime);
            newsItemTitle = itemView.findViewById(R.id.newsItemTitle);
            newsItemImage = itemView.findViewById(R.id.newsItemImage);
            newsItemCard = itemView.findViewById(R.id.newsItemCard);


        }
    }
}
