package com.project.kitt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView logo = findViewById(R.id.imageView2);
        int imageResource = getResources().getIdentifier("@drawable/the_tree2", null, this.getPackageName());
        logo.setImageResource(imageResource);
    }

    public void toHomeScreen(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // TEST CODE FOR FIREBASE UI START
    public void firebaseUI(View view)
    {
        Intent intent = new Intent(this, TestFirebase.class);
        startActivity(intent);
    }

    public void signIn(View view)
    {
        Intent intent = new Intent(this, FirebaseUIActivity.class);
        startActivity(intent);
    }
    // TEST CODE FOR FIREBASE UI END
}
