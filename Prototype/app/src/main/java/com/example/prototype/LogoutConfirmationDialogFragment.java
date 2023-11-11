package com.example.prototype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutConfirmationDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    goToStartActivity();
                } )
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .create();
    }

    private void goToStartActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), StartActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
