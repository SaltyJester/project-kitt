package com.project.kitt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    CardViewAdapter mCardView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recCards = (RecyclerView) root.findViewById(R.id.recyclerCardView);
        setRetainInstance(true);

        // Create cardview recyclerview
        recCards.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recCards.setLayoutManager(llm);

        // Get food from DB. If the size of array returned is > 0, remove placeholder text.
        FoodDetail[] items = new SQLiteDBHelper(getActivity()).getAllFood();
        if (items.length != 0) {
            TextView ph0 = root.findViewById(R.id.placeholder0);
            ph0.setVisibility(View.GONE);
            TextView ph1 = root.findViewById(R.id.placeholder1);
            ph1.setVisibility(View.GONE);
        }

        // Instantiate the cardview adapter and swipe listener.
        mCardView = new CardViewAdapter(items);
        recCards.setAdapter(mCardView);
        mCardView.setView(root);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(mCardView, getContext()));
        itemTouchHelper.attachToRecyclerView(recCards);
        return root;
    }

    /*
     * When the fragment is paused/stopped, assume that the snackbar has been dismissed, and remove
     * food permanently.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mCardView.removeFoodPermanently();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mCardView.removeFoodPermanently();
    }
}
