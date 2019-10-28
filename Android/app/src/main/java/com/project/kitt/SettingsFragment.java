package com.project.kitt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment{
    ListView listview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        String[] settingsItems = {"Notifications", "Themes", "Account Settings"};
        listview = view.findViewById(R.id.listSettings);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, settingsItems
        );

        listview.setAdapter(listViewAdapter);

        return view;

    }

public void onStart(){
    super.onStart();

    AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // Do something in response to the click
            if (position == 0) {
            //    Intent myIntent= new Intent(view.getContext(), SettingsNotifications.class);
                Intent myIntent= new Intent(view.getContext(), SettingsNotifications.class);
                startActivityForResult(myIntent, 0);
            }

        }
    };
     listview.setOnItemClickListener(messageClickedHandler);

    }


}

