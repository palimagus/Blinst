package com.palim.blinstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.palim.blinstapp.core.Jammer;
import com.palim.blinstapp.core.Session;
import com.palim.blinstapp.dialogs.ConfirmationDialog;
import com.palim.blinstapp.dialogs.LeaveSessionDialog;

import java.util.Objects;

public class JammerActivity extends AppCompatActivity implements LeaveSessionDialog.LeaveSessionDialogListener {

  TextView user_name_view;
  TextView user_token_view;
  TextView user_score_view;
  TextView session_token_view;
  TextView session_state;

  Button blinst_button;
  Button quit_button;

  String session_token;

  FirebaseAuth mAuth;
  Jammer me;
  Session session;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_jammer);

    session_token = getIntent().getStringExtra("session_token");

    user_name_view = findViewById(R.id.user_name_jam_view);
    user_token_view = findViewById(R.id.user_token_jam_view);
    user_score_view = findViewById(R.id.user_score_jam_view);
    session_token_view = findViewById(R.id.session_token_jame_view);

    session_state = findViewById(R.id.session_state_jam);

    blinst_button = findViewById(R.id.blinst_button_jam);
    quit_button = findViewById(R.id.leave_button_jam);

    mAuth = FirebaseAuth.getInstance();
    setupButtons();
  }

  @Override
  protected void onStart() {
    super.onStart();
    setupAnonymousUser();
    updateUI();
  }

  private void updateUI() {
    session_token_view.setText(session_token);
    if (me != null) {
      user_token_view.setText(me.getId());
      user_name_view.setText(me.getName());
      user_score_view.setText(String.valueOf(me.getScore()));
    }
    if (session != null) {
      session_state.setText(session.getState());
      blinst_button.setEnabled(!Objects.equals(session.getState(), "VOTE"));
    }
  }

  private void setupButtons() {
    blinst_button.setOnClickListener(view -> {
      if (session == null) return;
      session.blinst(me);
    });
    quit_button.setOnClickListener(view -> {
      new LeaveSessionDialog().show(getSupportFragmentManager(), "BLINST");
    });
  }

  private void setupAnonymousUser() {
    mAuth.signInAnonymously().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        try {
          setupJammer();
          if (me != null) {
            setupJamSession();
          }

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        updateUI();

      } else {
        Toast.makeText(JammerActivity.this, "Authentication failed.",
            Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void setupJammer() throws InterruptedException {
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String user_name = getIntent().getStringExtra("user_name");
    if (currentUser == null || user_name == null) {
      ConfirmationDialog dialog = new ConfirmationDialog("Connexion impossible.");
      dialog.setConfirmListener(() -> {
        startActivity(new Intent(this, MainActivity.class));
        JammerActivity.this.onDestroy();
      });
      dialog.show(getSupportFragmentManager(), "BLINST");
    }

    assert currentUser != null;
    me = new Jammer.Builder().setName(user_name).build(currentUser.getUid());
    me.setUpdateListener(this::updateUI);

    new ConfirmationDialog("Connexion réussie!")
        .show(getSupportFragmentManager(), "BLINST");
  }

  private void setupJamSession() {
    try {
      session = new Session.Builder().setId(session_token).build();
      session.setUpdateListener(this::updateUI);

    } catch (Exception e) {
      Log.w("BLINST", e.getMessage());
      ConfirmationDialog dialog = new ConfirmationDialog("Code invalide.");
      dialog.setConfirmListener(() -> {
        startActivity(new Intent(this, MainActivity.class));
        JammerActivity.this.onDestroy();
      });
      dialog.show(getSupportFragmentManager(), "BLINST");
    }
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