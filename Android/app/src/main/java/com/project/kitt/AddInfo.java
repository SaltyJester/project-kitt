package com.project.kitt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddInfo extends AppCompatActivity {
    Button itemDate;
    Button enter;
    EditText itemName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        itemName = findViewById(R.id.itemName);

        itemDate = (Button) findViewById(R.id.clickDate);


    }

    public void addItemToDB(View v){
        String test = itemName.getText().toString();
        Intent myIntent = new Intent(AddInfo.this, MainActivity.class);
        myIntent.putExtra("add_item", test);
        startActivity(myIntent);
    }
}
