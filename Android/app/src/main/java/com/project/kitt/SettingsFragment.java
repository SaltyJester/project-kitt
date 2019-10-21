package com.project.kitt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_settings, container, false);

       String [] settingsItems = {"Notifications", "Themes", "Account Settings"};
       ListView listview = view.findViewById(R.id.listSettings);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, settingsItems
        );

        listview.setAdapter(listViewAdapter);

       return view;
    }


}

