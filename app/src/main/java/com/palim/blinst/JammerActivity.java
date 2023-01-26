package com.palim.blinst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.palim.blinst.dialogs.LeaveSessionDialog;

public class JammerActivity extends AppCompatActivity implements LeaveSessionDialog.LeaveSessionDialogListener {

    TextView user_name_view;
    TextView user_token_view;
    TextView user_score_view;
    TextView session_token_view;
    TextView session_state;

    Button blinst_button;
    Button quit_button;

    String session_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jammer);

        Intent here = getIntent();
        session_token = here.getStringExtra("session_token");

        user_name_view = findViewById(R.id.user_name_jam_view);
        user_token_view = findViewById(R.id.user_token_jam_view);
        user_score_view = findViewById(R.id.user_score_jam_view);
        session_token_view = findViewById(R.id.session_token_jame_view);

        session_state = findViewById(R.id.session_state_jam);

        blinst_button = findViewById(R.id.blinst_button_jam);
        quit_button = findViewById(R.id.leave_button_jam);

        setupButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {
        session_token_view.setText(session_token);
    }

    private void setupButtons() {
        blinst_button.setOnClickListener(view -> {

        });
        quit_button.setOnClickListener(view -> {
            new LeaveSessionDialog().show(getSupportFragmentManager(), "BLINST");
        });
    }

    private void quit() {
        Intent dest = new Intent(this, MainActivity.class);
        startActivity(dest);
        this.onDestroy();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
        quit();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}