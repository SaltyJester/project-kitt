package com.project.kitt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recCards = (RecyclerView)root.findViewById(R.id.recyclerCardView);
        recCards.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recCards.setLayoutManager(llm);
        FoodDetail [] items = new SQLiteDBHelper(getActivity()).getAllFood();
        if(items.length != 0){
            TextView ph0 = root.findViewById(R.id.placeholder0);
            ph0.setVisibility(View.GONE);
            TextView ph1 = root.findViewById(R.id.placeholder1);
            ph1.setVisibility(View.GONE);
        }

        CardViewAdapter myCardView = new CardViewAdapter(items);
        recCards.setAdapter(myCardView);
        return root;
    }
}
