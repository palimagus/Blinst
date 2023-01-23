package com.palim.blinst;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String user_name = "Unknown Jammer";

    TextView user_token_view;
    TextView user_name_view;

    EditText token_session_editor;
    EditText user_name_editor;

    Button token_session_validator;
    Button user_name_validator;
    Button host_session;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Show the user name in layout.
        // If already set in local storage, get it.
        // Else if setup in db, get it.
        // Else set it to default.
        user_name_view.setText(user_name);
    }
}