package com.project.kitt;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment{
    ArrayList<String> settingsItems = new ArrayList<>();
    ListView listview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsItems.add("Account Settings");
        settingsItems.add("Notifications");
        settingsItems.add("Appearance");
        listview = view.findViewById(R.id.listSettings);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(getActivity(), R.layout.listrow, settingsItems);

        listview.setAdapter(listViewAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {

                    Intent myIntent= new Intent(view.getContext(), SettingsNotifications.class);
                    startActivityForResult(myIntent, 0);
                    myIntent.putExtra("notif", i);
                    startActivity(myIntent);
                }


            }
        });

        return view;

    }

}


