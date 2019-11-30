package com.project.kitt;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class FirestoreDB
{
    private static final String TAG = "FirestoreDB";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addFood(FoodDetail food) // test writing to database
    {
        // Check to see if user is logged in, otherwise prompt user to login (NOT FINISHED)
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("users").document(user.getUid()).collection("foods")
                    .add(food)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }

    public FoodDetail[] getAllUsers()
    {
        FoodDetail[] foodList = new FoodDetail[0];
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            FirebaseFirestore.getInstance()
//                    .collection("user")
//                    .document()

//            db.collection("users")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                }
//                            } else {
//                                Log.w(TAG, "Error getting documents.", task.getException());
//                            }
//                        }
//                    });
        }
        return foodList;
    }
}