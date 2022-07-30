package com.rizwan.secondtry;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    List<MainRecyclerViewSection> mainRecyclerViewSectionList;
    Context context;
    DecimalFormat df2 = new DecimalFormat("#.00");
//    ChildRecyclerAdapter childRecyclerAdapter;

    public MainRecyclerAdapter(List<MainRecyclerViewSection> mainRecyclerViewSectionList, Context context) {
        this.mainRecyclerViewSectionList = mainRecyclerViewSectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.section_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainRecyclerViewSection mainRecyclerViewSection = mainRecyclerViewSectionList.get(position);
        String sectionName = mainRecyclerViewSection.getSectionName();
        String networthHeading =mainRecyclerViewSection.getNetworthHeading();
        String networthAmount = df2.format(Double.parseDouble(mainRecyclerViewSection.getNetworthAmount()));
        List<FavoriteRecyclerItem> items = mainRecyclerViewSection.getSectionItems();

        holder.sectionNameText.setText(sectionName);

        if(position == 0){
            holder.networthHeading.setText(networthHeading);

            holder.networthAmount.setText(networthAmount);
        }
        else{
            holder.networthHeading.setVisibility(View.GONE);
            holder.networthAmount.setVisibility(View.GONE);
        }
//Commenting it as I am declaring it in Main, for implementation of swipe to delete function

        String name = "";
        if(position == 0){
            name = "portfolio";
        }
        else{
            name = "favorites";
        }
        ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(context, items, name);
        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.childRecyclerView.setAdapter(childRecyclerAdapter);
        holder.childRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        ItemTouchHelper.Callback callback = new ItemMoveCallback(childRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(holder.childRecyclerView);

        if(position != 0){
            enableSwipeToDeleteAndUndo(holder.childRecyclerView);
        }

    }

    @Override
    public int getItemCount() {
        return mainRecyclerViewSectionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView sectionNameText;
        TextView networthHeading;
        TextView networthAmount;
        RecyclerView childRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionNameText = itemView.findViewById(R.id.sectionNameText);
            networthHeading = itemView.findViewById(R.id.networthHeading);
            networthAmount = itemView.findViewById(R.id.networthAmount);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);


        }
    }

    private void enableSwipeToDeleteAndUndo(RecyclerView childRecyclerView) {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                ChildRecyclerAdapter childRecyclerAdapter = (ChildRecyclerAdapter) childRecyclerView.getAdapter();
                final int position = viewHolder.getAdapterPosition();
                final FavoriteRecyclerItem item = childRecyclerAdapter.getData().get(position);
                childRecyclerAdapter.removeItem(position);

                String mySharedPreference = "mySharedPreference";
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(mySharedPreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String favoritesStringArray = sharedPreferences.getString("favorites", null);
                String [] favoritesTickerArray = favoritesStringArray.split(",");
                StringBuilder favoritesTickerItem = new StringBuilder();
                for (int j = 0; j < favoritesTickerArray.length; j++) {
                    String[] tickerDetails = favoritesTickerArray[j].split(":");
                    if(item.getTicker().equals(tickerDetails[0])){
                        continue;
                    }
                    if(favoritesTickerItem.toString().equals("")){
                        favoritesTickerItem.append(tickerDetails[0]).append(":").append(tickerDetails[1]).append(":").append(tickerDetails[2]);
                    }
                    else {
                        favoritesTickerItem.append(",").append(tickerDetails[0]).append(":").append(tickerDetails[1]).append(":").append(tickerDetails[2]);
                    }
                }
                System.out.println("Favorite after remove " + favoritesTickerItem);
                editor.putString("favorites", String.valueOf(favoritesTickerItem));
                editor.commit();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(childRecyclerView);
    }


}
