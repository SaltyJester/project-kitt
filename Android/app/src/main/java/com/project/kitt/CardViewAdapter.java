package com.project.kitt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.FoodViewHolder>{

    ArrayList<FoodDetail> foodList;
    int mRecentlyDeletedItemPosition;
    FoodDetail mRecentlyDeletedItem;
    Context context;
    View mRootView;

    public CardViewAdapter(FoodDetail[] food){
        this.foodList = new ArrayList(Arrays.asList(food));
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        FoodViewHolder fvh = new FoodViewHolder(mView);
        context = parent.getContext();
        return fvh;
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        holder.title.setText(foodList.get(position).getFoodName());
        String date = String.valueOf(foodList.get(position).getFoodMon());
        date += "/" + String.valueOf(foodList.get(position).getFoodDay());
        date += "/" + String.valueOf(foodList.get(position).getFoodYr());
        holder.subtitle.setText(date);
        Calendar c = Calendar.getInstance();
        c.set(foodList.get(position).getFoodYr(), foodList.get(position).getFoodMon() - 1, foodList.get(position).getFoodDay());
        if(c.before(Calendar.getInstance())){
            holder.cv.setCardBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView subtitle;

        FoodViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_base);
            title = (TextView)itemView.findViewById(R.id.card_title);
            subtitle = (TextView)itemView.findViewById(R.id.card_subtitle);
        }
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = foodList.get(position);
        mRecentlyDeletedItemPosition = position;
        foodList.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mRootView.findViewById(R.id.coordinator_home);
        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo, v -> undoDelete());
        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                SQLiteDBHelper helper = new SQLiteDBHelper(context);
                helper.removeFood(mRecentlyDeletedItem.getFoodID());
            }

            @Override
            public void onShown(Snackbar snackbar) {
                return;
            }
        });
        snackbar.show();
    }

    public void setView(View view){
        mRootView = view;
    }

    private void undoDelete() {
        foodList.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

}
