package com.palim.blinstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.palim.blinstapp.dialogs.ConfirmationDialog;

public class MainActivity extends AppCompatActivity {

    String user_name = "Unknown Jammer";
    String session_token = "";

    TextView user_token_view;
    TextView user_name_view;

    EditText token_session_editor;
    EditText user_name_editor;

    Button token_session_validator;
    Button user_name_validator;
    Button host_session;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_token_view = findViewById(R.id.user_token);
        user_name_view = findViewById(R.id.user_name_view);

        token_session_editor = findViewById(R.id.session_token);
        user_name_editor = findViewById(R.id.user_name_editor);

        token_session_validator = findViewById(R.id.session_token_join);
        user_name_validator = findViewById(R.id.user_name_validator);
        host_session = findViewById(R.id.session_host_button);

        mAuth = FirebaseAuth.getInstance();
        setupButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Show the user name in layout.
        // If already set in local storage, get it.
        // Else if setup in db, get it.
        // Else set it to default.
        setupAnonymousUser();
        updateUI();
    }

    private void setupAnonymousUser() {
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               FirebaseUser currentUser = mAuth.getCurrentUser();
               assert currentUser != null;
               updateUI(currentUser);

           } else {
               Toast.makeText(MainActivity.this, "Authentication failed.",
                       Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void setupButtons() {
        token_session_validator.setOnClickListener(view -> {
            session_token = token_session_editor.getText().toString();
            Intent dest = new Intent(this, JammerActivity.class);
            dest.putExtra("session_token", session_token);

            startActivity(dest);
            this.onDestroy();
        });
        user_name_validator.setOnClickListener(view -> {
            setUserName(user_name_editor.getText().toString());
        });
        host_session.setOnClickListener(view -> {});
    }

    private void setUserName(String newName) {
        user_name = newName;
        new ConfirmationDialog("Nom d'utilisateur mis à jour")
                .show(getSupportFragmentManager(), "BLINST");
        updateUI();
    }

    private void updateUI() {
        user_name_view.setText(user_name);
    }
    private void updateUI(FirebaseUser user) {
        user_token_view.setText(user.getUid());
    }
}