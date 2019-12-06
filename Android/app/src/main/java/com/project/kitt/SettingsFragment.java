package com.project.kitt;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import com.firebase.ui.auth.AuthUI;


public class SettingsFragment extends PreferenceFragmentCompat{

    Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        context = getActivity();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        Preference button = findPreference(getString(R.string.signOutButton));
        PreferenceCategory nc = (PreferenceCategory) findPreference("notifications category");
        if(account == null){
            nc.removePreference(button);
        }
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AuthUI.getInstance()
                        .signOut(context)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setTitle("Signed Out");
                                alertDialog.setMessage("Sign out successful!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Preference button = findPreference(getString(R.string.signOutButton));
                                                PreferenceCategory nc = (PreferenceCategory) findPreference("notifications category");
                                                nc.removePreference(button);

                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                return true;
            }
        });
    }

}


