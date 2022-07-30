package com.rizwan.secondtry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private final Context context;

    private static final DecimalFormat df2 = new DecimalFormat("0.00");


    private List<FavoriteRecyclerItem> favoriteRecyclerItems;

    private String name;

    public ChildRecyclerAdapter(Context context, List<FavoriteRecyclerItem> favoriteRecyclerItems, String name) {
        this.context = context;
        this.favoriteRecyclerItems = favoriteRecyclerItems;
        this.name = name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRecyclerItem currentItem = favoriteRecyclerItems.get(position);
        String portfolioTicker = currentItem.getTicker();
        String portfolioName = currentItem.getNameOrShares();
        String portfolioCurrent = currentItem.getCurrentPrice();
        String portfolioChange = currentItem.getChange();

        if (Double.parseDouble(portfolioChange) > 0) {
            holder.portfolioChange.setTextColor(context.getResources().getColor(R.color.myGreen));
        } else if(Double.parseDouble(portfolioChange) < 0){
            holder.portfolioChange.setTextColor(context.getResources().getColor(R.color.myRed));
        }
        else{
            holder.portfolioChange.setTextColor(context.getResources().getColor(R.color.textGray));
        }

        holder.portfolioTicker.setText(portfolioTicker);
        holder.portfolioName.setText(portfolioName);
        holder.portfolioCurrent.setText(df2.format(Double.parseDouble(portfolioCurrent)));
        holder.portfolioChange.setText(df2.format(Double.parseDouble(portfolioChange)));

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable myDrawable = null;
        if (Double.parseDouble(portfolioChange) > 0) {
            myDrawable = context.getResources().getDrawable(R.drawable.ic_twotone_trending_up_24);
        } else if (Double.parseDouble(portfolioChange) < 0){
            myDrawable = context.getResources().getDrawable(R.drawable.ic_baseline_trending_down_24);
        }
        else{
            holder.upDownArrow.setVisibility(View.GONE);
        }
        holder.upDownArrow.setImageDrawable(myDrawable);

        myDrawable = context.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_24);
        holder.forwardArrow.setImageDrawable(myDrawable);
    }

    @Override
    public int getItemCount() {
//        return 30;
        return favoriteRecyclerItems.size();
    }

    public List<FavoriteRecyclerItem> getData() {
        return favoriteRecyclerItems;
    }

    public void removeItem(int position) {
        favoriteRecyclerItems.remove(position);
        notifyItemRemoved(position);
    }


    //    For drag to reorder functionality
//    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        String mySharedPreference = "mySharedPreference";
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(mySharedPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String stringArray = sharedPreferences.getString(name, null);
        String [] tickerArray = stringArray.split(",");
        List<String> tickerArrayList = new ArrayList<String>();
        tickerArrayList = Arrays.asList(tickerArray);

        System.out.println("Helloooooooooooo from moved from " + fromPosition + " to " + toPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(favoriteRecyclerItems, i, i + 1);
                Collections.swap(tickerArrayList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(favoriteRecyclerItems, i, i - 1);
                Collections.swap(tickerArrayList, i, i - 1);
            }
        }

        stringArray = String.join(",", tickerArrayList);
        editor.putString(name, stringArray);
        editor.commit();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView portfolioTicker;
        TextView portfolioName;
        TextView portfolioCurrent;
        TextView portfolioChange;
        ImageView upDownArrow;
        ImageView forwardArrow;

//        private TextView mTitle;
        View rowView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            portfolioTicker = itemView.findViewById(R.id.portfolioTicker);
            portfolioName = itemView.findViewById(R.id.portfolioName);
            portfolioCurrent = itemView.findViewById(R.id.portfolioCurrent);
            portfolioChange = itemView.findViewById(R.id.portfolioChange);
            upDownArrow = itemView.findViewById(R.id.upDownArrow);
            forwardArrow = itemView.findViewById(R.id.forwardArrow);
            rowView = itemView;
//            mTitle = itemView.findViewById(R.id.txtTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();
                    System.out.println(pos);

                    // check if item still exists
                    if (pos != RecyclerView.NO_POSITION) {
                        FavoriteRecyclerItem clickedDataItem = favoriteRecyclerItems.get(pos);
                        String ticker = clickedDataItem.getTicker();
                        Intent detailsIntent = new Intent(context, DetailsActivity.class);
                        detailsIntent.putExtra("ticker", ticker);
                        context.startActivity(detailsIntent);
                    }
                }
            });

        }
    }

}
