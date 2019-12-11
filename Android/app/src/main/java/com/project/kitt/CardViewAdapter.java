package com.project.kitt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/*
 * Source: https://developer.android.com/guide/topics/ui/layout/recyclerview
 */

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.FoodViewHolder>{

    ArrayList<FoodDetail> foodList;             // ArrayList to store the data to be displayed as a CardView
    int mRecentlyDeletedItemPosition;           // Temp variable to store the position of a deleted item
    FoodDetail mRecentlyDeletedItem;            // Temp variable to store a deleted item.
    Context context;                            // Context
    boolean waiting = false;                    // Variable to check if we are waiting for an undo snackbar to be dismissed.
    View mRootView;                             // Store rootView for use in helper functions
    static Snackbar snackbar;

    /*
    * CardViewAdapter constructor, assembles the data holder ArrayList
    *
    * @param: FoodDetail[] food: An array containing all the food items in the db
     */
    public CardViewAdapter(FoodDetail[] food){
        // Convert array to ArrayList to facilitate easier deletion.
        this.foodList = new ArrayList(Arrays.asList(food));
    }

    /*
     * Overridden function from the parent class that is called when the ViewHolder is created.
     *
     * @param: ViewGroup parent: the parent ViewGroup
     *         int viewType: unused, required by Android.
     */
    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        FoodViewHolder fvh = new FoodViewHolder(mView);
        context = parent.getContext();
        View view = mRootView.findViewById(R.id.coordinator_home);
        snackbar = Snackbar.make(view, R.string.snackbar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo, v -> undoDelete());
        snackbar.addCallback(new Snackbar.Callback() {
            // Function that is called when the snackbar times out.
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                removeFoodPermanently();
                waiting = false;
            }

            @Override
            public void onShown(Snackbar snackbar) {
                return;
            }
        });
        return fvh;
    }

    /*
    *   Overridden function from parent class that sets the content of each individual card.
    *   Also changes card colors based on whether the food is expired or not.
    *
     */
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
            holder.cv.setCardBackgroundColor(Color.parseColor("#FF4C4C4C"));
            holder.image.setImageResource(R.drawable.placeholder_food_disabled);
        }
    }

    /*
    *   Overridden function from parent class that returns the size of the data.
    *
     */
    @Override
    public int getItemCount() {
        return foodList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    /*
     *  Static class that holds all information of a single card.
     *  Constructor sets the appropriate UI elements as member variables.
     *
     */
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView subtitle;
        ImageView image;

        FoodViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.card_view_image);
            cv = (CardView)itemView.findViewById(R.id.card_base);
            title = (TextView)itemView.findViewById(R.id.card_title);
            subtitle = (TextView)itemView.findViewById(R.id.card_subtitle);
        }
    }

    /*
     * Function to delete an item when a card is swiped. Stores the deleted item in a global variable
     * in case the user presses undo. This function only removes the item from the RecycleraView, not
     * from the db. It also calls showUndoSnackbar(), on whose timeout the item will be permanently
     * deleted.
     *
     * @param int position: the position of the item that has been deleted.
     * @return: void
     *
     */
    public void deleteItem(int position) {
        if(waiting){
            removeFoodPermanently();
            waiting = false;
        }
        mRecentlyDeletedItem = foodList.get(position);
        mRecentlyDeletedItemPosition = position;
        foodList.remove(position);
        notifyItemRemoved(position);
        waiting = true;
        snackbar.show();
    }

    /*
    *   Helper function to remove a deleted food item permanently, i.e. remove it from the db
    *
    *   @param: void
    *   @return: void
    *
     */

    public void removeFoodPermanently(){
        if(waiting == true) {
            SQLiteDBHelper helper = new SQLiteDBHelper(context);
            helper.removeFood(mRecentlyDeletedItem.getFoodID(), context);
            waiting = false;
        }
    }

    /*
    *   Set the global RootView variable for use in helper functions.
    *
    *   @param: View view: The view to be set as a global variable.
    *   @return: void
    *
     */
    public void setView(View view){
        mRootView = view;
    }

    /*
    *   Undo the delete action when the undo button on the snackbar is pressed.
    *   Put the information in the mRecentlyDeletedItem variables back into the RecyclerView ArrayList
    *
    *   @param: void
    *   @return: void
    *
     */
    private void undoDelete() {
        foodList.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        waiting = false;
    }

}
