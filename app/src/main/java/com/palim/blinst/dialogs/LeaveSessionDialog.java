package com.palim.blinst.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class LeaveSessionDialog extends DialogFragment {

    public interface LeaveSessionDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    LeaveSessionDialogListener listener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LeaveSessionDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                + " must implement LeaveSessionDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Receive event callbacks
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Etes-vous sûr de vouloir quitter la session ?")
            .setPositiveButton("Confirmer", (dialogInterface, i) -> {
                // Leave session
                listener.onDialogPositiveClick(LeaveSessionDialog.this);
            })
            .setNegativeButton("Annuler", ((dialogInterface, i) -> {
                // Cancel dialog
                listener.onDialogNegativeClick(LeaveSessionDialog.this);
            })
        );
        return builder.create();
    }
}
