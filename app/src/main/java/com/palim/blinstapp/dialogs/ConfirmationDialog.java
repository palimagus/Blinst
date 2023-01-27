package com.palim.blinstapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class ConfirmationDialog extends DialogFragment {

    private String message;

    public ConfirmationDialog(String msg) {
        this.message = msg;
    }

    OnConfirm listener;

    public void setConfirmListener(OnConfirm listener) {
        this.listener = listener;
    }

    public interface OnConfirm {
        void confirm();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.message)
                .setPositiveButton("Ok", ((dialogInterface, i) -> {
                    if (this.listener != null) {
                        this.listener.confirm();
                    }
                    Objects.requireNonNull(ConfirmationDialog.this.getDialog()).cancel();
                }));
        return builder.create();
    }
}
