package com.palim.blinstapp.core;

import android.util.Log;

import com.palim.blinstapp.data.FirebaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Session {
    private String id;
    private ArrayList<String> jammers;
    private SessionState state;
    private String host;
    private Jammer blinster;

    private static final FirebaseHandler db = new FirebaseHandler();

    OnSessionUpdate listener;

    public void setUpdateListener(OnSessionUpdate listener) {
        this.listener = listener;
    }

    public interface OnSessionUpdate {
        void sync();
    }

    public static class Builder {
        private String id;
        private ArrayList<String> jammers;
        private SessionState state;
        private String host;
        private Jammer blinster;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public void setState(SessionState state) {
            this.state = state;
        }

        public Builder setJammers(ArrayList<String> jammers) {
            this.jammers = jammers;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public void setBlinster(Jammer blinster) {
            this.blinster = blinster;
        }

        public Session build() throws Exception {
            Session session = new Session(this);
            if (session.state == null) session.state = SessionState.DISCONNECTED;
            if (session.jammers == null) session.jammers = new ArrayList<>();
            if (session.id == null) {
                session.id = Session.makeSessionToken();
                db.syncSessionWithDB(session, true);
            }
            else {
                session.id = session.id.toLowerCase(Locale.ROOT).trim();
                db.syncSessionWithDB(session, false);
            }
            return session;
        }
    }

    private Session(final Builder builder) {
        id = builder.id;
        jammers = builder.jammers;
        state = builder.state;
        blinster = builder.blinster;
    }

    public void blinst(Jammer jammer) {
        blinster = jammer;

        Map<String, Object> data = new HashMap<>();
        data.put("state", SessionState.VOTE.toString());
        data.put("blinster", blinster.getId());
        db.pushSession(this.id, data);
    }

    public void vote(Boolean isCorrect) {
        if (!isCorrect) {
            Map<String, Object> data = new HashMap<>();
            data.put("state", SessionState.PLAY.toString());
            data.put("blinster", "");
            db.pushSession(this.id, data);
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("state", SessionState.NEXT.toString());
            data.put("blinster", "");
            db.pushSession(this.id, data);
            blinster.addScore(1);
        }
        blinster = null;
    }

    public String getId() {
        return id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setState(String state) {
        // PLAY, PAUSE, VOTE, CONNECTED, DISCONNECTED
        switch (state) {
            case "PLAY":
                this.state = SessionState.PLAY;
                break;
            case "PAUSE":
                this.state = SessionState.PAUSE;
                break;
            case "VOTE":
                this.state = SessionState.VOTE;
                break;
            case "NEXT":
                this.state = SessionState.NEXT;
                break;
            case "CONNECTED":
                this.state = SessionState.CONNECTED;
                break;
            default:
                this.state = SessionState.DISCONNECTED;
        }
    }

    public String getState() {
        return state.toString();
    }

    public void setJammers(ArrayList<String> jammers) {
        this.jammers = jammers;
    }

    public ArrayList<String> getJammers() {
        return jammers;
    }

    private static String makeSessionToken() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public void triggerSync() {
        if (listener == null) return;
        listener.sync();
    }
}
