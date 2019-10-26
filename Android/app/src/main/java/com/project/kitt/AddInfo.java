package com.project.kitt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddInfo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

    }

    public void addItemToDB(View v){
        EditText itemDate;
        EditText itemName;
        itemName = findViewById(R.id.itemName);
        itemDate = findViewById(R.id.clickDate);
        boolean error = false;

        if(itemName.getText().toString().matches("") || itemDate.getText().toString().matches("" )){
            error = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please make sure all fields have valid entries")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (!error) {
            String foodName = itemName.getText().toString();
            int foodDate = Integer.parseInt(itemDate.getText().toString());

            Date cDate = new Date();
            int today = Integer.parseInt( new SimpleDateFormat("yyyyMMdd").format(cDate));
            if (foodDate < today) {
                error = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Looks like your food item has already expired. Time to throw it away!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
            if(error)
                return;

            FoodDetail foodItem = new FoodDetail();
            foodItem.setFoodName(foodName);
            foodItem.setFoodDate(foodDate);

            SQLiteDBHelper db = new SQLiteDBHelper(this);
            db.addFood(foodItem);

            Intent myIntent = new Intent(AddInfo.this, MainActivity.class);
            startActivity(myIntent);
        }
    }
}
