package com.palim.blinstapp.data;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.palim.blinstapp.core.Jammer;
import com.palim.blinstapp.core.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseHandler {
  public FirebaseFirestore db = FirebaseFirestore.getInstance();

  public Map<String, Object> mapJammer(Jammer jammer) {
    Map<String, Object> jammerData = new HashMap<>();
    jammerData.put("name", jammer.getName());
    jammerData.put("score", jammer.getScore());

    return jammerData;
  }

  public Jammer updateJammerData(Jammer jammer, Map<String, Object> data) {
    jammer.setName((String) data.get("name"));
    jammer.setScore((Long) data.get("score"));

    return jammer;
  }

  public void setJammerData(String reference, Map<String, Object> data) {
    db.collection("jammers")
        .document(reference)
        .set(data)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            Log.d("BLINST", "Updated db with success");
          }
        });
  }

  private void postJammer(Jammer jammer) {
    db.collection("jammers")
        .document(jammer.getId())
        .set(mapJammer(jammer))
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            syncJammerWithDB(jammer);
          } else {
            Log.w("BLINST", "Cannot create document: " + Objects.requireNonNull(task.getException()).getMessage());
            task.getException().printStackTrace();
          }
        });
  }

  public void syncJammerWithDB(Jammer jammer) {
    final DocumentReference docRef = db.collection("jammers").document(jammer.getId());

    setJammerData(jammer.getId(), mapJammer(jammer));
    docRef.addSnapshotListener((value, error) -> {
      if (error != null) {
        Log.w("BLINST", "Listen failed: " + error.getMessage());
        error.printStackTrace();
        return;
      }
      if (value != null && value.exists()) {
        updateJammerData(jammer, Objects.requireNonNull(value.getData()));
        jammer.triggerSync();

      } else {
        Log.w("BLINST", "Current data is null");
        // Creating a document for Jammer
        postJammer(jammer);
      }
    });
  }

  public Map<String, Object> mapSession(Session session) {
    Map<String, Object> sessionData = new HashMap<>();
    sessionData.put("jammers", session.getJammers());
    sessionData.put("state", session.getState());
    sessionData.put("host", session.getHost());

    return sessionData;
  }

  public Boolean findSession(String sessionReference) {
    return db.collection("sessions").document(sessionReference).get().isSuccessful();
  }

  public void updateSessionData(Session session, Map<String, Object> data) {
    session.setHost((String) data.get("host"));
    session.setState((String) data.get("state"));
    session.setJammers((ArrayList<String>) data.get("jammers"));
  }

  public void setSessionData(String reference, Map<String, Object> data) {
    db.collection("sessions")
        .document(reference)
        .set(data)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            Log.d("BLINST", "Updated db with success");
          }
        });
  }

  public void pushSession(String sessionId, Map<String, Object> data) {
    db.collection("sessions").document(sessionId).update(data);
  }

  private void postSession(Session session) {
    db.collection("sessions")
        .document(session.getId())
        .set(mapSession(session))
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            syncSessionWithDB(session, true);
          } else {
            Log.w("BLINST", "Cannot create document: " + Objects.requireNonNull(task.getException()).getMessage());
            task.getException().printStackTrace();
          }
        });
  }

  public void syncSessionWithDB(Session session, Boolean doUpdate) {
    final DocumentReference docRef = db.collection("sessions").document(session.getId());

    if (doUpdate) setSessionData(session.getId(), mapSession(session));
    docRef.addSnapshotListener((value, error) -> {
      if (error != null) {
        Log.w("BLINST", "List failed: " + error.getMessage());
        error.printStackTrace();
        return;
      }
      if (value != null && value.exists()) {
        updateSessionData(session, Objects.requireNonNull(value.getData()));
        session.triggerSync();

      } else {
        Log.w("BLINST", "Current data is null");
        postSession(session);
      }
    });
  }
}
