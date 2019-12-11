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

    /*
      This function takes a FoodDetail object and sends its fields to the Firestore database. It
      first checks to see if a user is logged in. If not, the function does nothing. If logged in,
      the function attempts to write ot the Firestore. If successful, it will give a "success"
      message in the system log. If there was a problem, it will give a "failure" message in the
      system log instead.
     */
    public void addFood(FoodDetail food, int id) // test writing to database
    {
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

    /*
      This function reads from the user's collection of "foods" from the Firestore database. It
      first checks to see if the user is logged in before proceeding to read from the database. Once
      it successfully reads from the database, it'll update the SQLite database locally stored on
      the device. If unsuccessful an error message will appear in the system logs.
     */
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
                                SQLiteDBHelper dbHelper = new SQLiteDBHelper(mContext);
                                dbHelper.deleteTable("food");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                    System.out.println(document.getData());
                                    FoodDetail food = document.toObject(FoodDetail.class);
                                    dbHelper.addFoodFromFirebase(food);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    /*
      This function deletes a user's food object from the Firestore database. It first checks to see
      if a user is logged in. Each food object was initially given an Int ID before it was uploaded
      to the Firestore. This ID should match the item's ID in the local SQLite database. If deletion
      is successful a message is printed in the system logs. If not, an error message will be given
      in the system logs.
     */
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