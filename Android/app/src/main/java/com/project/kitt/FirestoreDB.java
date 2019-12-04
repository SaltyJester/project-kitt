package com.project.kitt;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreDB
{
    private Context mContext;

    public FirestoreDB(Context context)
    {
        mContext = context;
    }

    private static final String TAG = "FirestoreDB";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addFood(FoodDetail food, int id) // test writing to database
    {
        // Check to see if user is logged in, otherwise prompt user to login (NOT FINISHED)
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("users").document(user.getUid()).collection("foods").document(Integer.toString(id))
                    .set(food)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
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

    public void getAllFood()
    {
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            final ArrayList<FoodDetail> foodList = new ArrayList<FoodDetail>();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            db.collection("users").document(user.getUid()).collection("foods")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                    System.out.println(document.getData());
                                    FoodDetail food = document.toObject(FoodDetail.class);
                                    SQLiteDBHelper dbHelper = new SQLiteDBHelper(mContext);
                                    dbHelper.addFood(food);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void deleteFood(int id)
    {
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            db.collection("users")
                    .document(user.getUid())
                    .collection("foods")
                    .document(Integer.toString(id))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    }
}