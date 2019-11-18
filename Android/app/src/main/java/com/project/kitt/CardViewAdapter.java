package com.project.kitt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.FoodViewHolder>{

    FoodDetail[] foodList;

    public CardViewAdapter(FoodDetail[] food){
        this.foodList = food;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        FoodViewHolder fvh = new FoodViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        holder.title.setText(foodList[position].getFoodName());
        String date = String.valueOf(foodList[position].getFoodMon());
        date += "/" + String.valueOf(foodList[position].getFoodDay());
        date += "/" + String.valueOf(foodList[position].getFoodYr());
        holder.subtitle.setText(date);
    }

    @Override
    public int getItemCount() {
        return foodList.length;
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

}
