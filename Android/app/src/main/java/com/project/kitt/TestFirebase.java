package com.project.kitt;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TestFirebase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase);
    }

    public void signIn(View view)
    {
        Intent intent = new Intent(this, FirebaseUIActivity.class);
        startActivity(intent);
    }

    public void sendData(View view)
    {
        FirestoreDB db = new FirestoreDB(this);
        FoodDetail foodTest = new FoodDetail();
        foodTest.setFoodName("Orange");
//        foodTest.setFoodDate(1);
//        foodTest.setFoodDate(123);
        db.addFood(foodTest, 73);
    }
    public void getUserData(View view)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        System.out.println(user.getUid());
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            System.out.println("NO LOGIN DETECTED");
        }
        else
            {
                System.out.println(user.getUid());
            }
    }

    public void getData(View view)
    {
        FirestoreDB db = new FirestoreDB(this);
        db.getAllFood();
    }
}
