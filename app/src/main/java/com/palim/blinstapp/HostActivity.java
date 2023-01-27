package com.palim.blinstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.palim.blinstapp.core.Jammer;
import com.palim.blinstapp.core.Session;
import com.palim.blinstapp.dialogs.ConfirmationDialog;
import com.palim.blinstapp.dialogs.LeaveSessionDialog;

import java.util.Objects;

public class HostActivity extends AppCompatActivity implements LeaveSessionDialog.LeaveSessionDialogListener {

  TextView user_name_view;
  TextView user_token_view;
  TextView session_id_view;
  TextView session_state_view;
  TextView artist_view;
  TextView title_view;
  TextView voting_jammer_view;

  Button correct_button;
  Button wrong_button;
  ImageButton player_button;
  ImageButton next_button;
  Button leave_button;

  FirebaseAuth mAuth;
  Jammer me;
  Session session;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_host);

    user_name_view = findViewById(R.id.user_name_view_host);
    user_token_view = findViewById(R.id.user_token_host);
    session_id_view = findViewById(R.id.session_id_view_host);
    session_state_view = findViewById(R.id.session_state_view_host);
    artist_view = findViewById(R.id.artist_view);
    title_view = findViewById(R.id.title_view);
    voting_jammer_view = findViewById(R.id.voting_jammer_view);

    correct_button = findViewById(R.id.true_button);
    wrong_button = findViewById(R.id.false_button);
    player_button = findViewById(R.id.toggle_play_button);
    next_button = findViewById(R.id.next_song_button);
    leave_button = findViewById(R.id.leave_host_btn);

    mAuth = FirebaseAuth.getInstance();
    setupButtons();
  }

  @Override
  protected void onStart() {
    super.onStart();
    setupAnonymousUser();
    updateUI();
  }

  private void setupButtons() {
    leave_button.setOnClickListener(view -> {
      new LeaveSessionDialog().show(getSupportFragmentManager(), "BLINST");
    });
    correct_button.setOnClickListener(view -> {
      if (session == null) return;
      session.vote(true);
    });
    wrong_button.setOnClickListener(view -> {
      if (session == null) return;
      session.vote(false);
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
        Toast.makeText(HostActivity.this, "Authentication failed.",
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
        HostActivity.this.onDestroy();
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
      session = new Session.Builder().setHost(me.getId()).build();
      session.setUpdateListener(this::updateUI);

    } catch (Exception e) {
      Log.w("BLINST", e.getMessage());
      ConfirmationDialog dialog = new ConfirmationDialog("Une erreur innatendue c'est produite");
      dialog.setConfirmListener(() -> {
        startActivity(new Intent(this, MainActivity.class));
        HostActivity.this.onDestroy();
      });
      dialog.show(getSupportFragmentManager(), "BLINST");
    }
  }

  private void updateUI() {
    if (me != null) {
      user_name_view.setText(me.getName());
      user_token_view.setText(mAuth.getCurrentUser().getUid());
    }
    if (session != null) {
      session_id_view.setText(session.getId());
      session_state_view.setText(session.getState());

      wrong_button.setEnabled(!Objects.equals(session.getState(), "VOTE"));
      correct_button.setEnabled(!Objects.equals(session.getState(), "VOTE"));
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