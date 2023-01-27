package com.palim.blinstapp.core;

import com.palim.blinstapp.data.FirebaseHandler;

public class Jammer {
    private final String id;
    private String name;
    private Long score;

    private static final FirebaseHandler db = new FirebaseHandler();

    OnJammerUpdate listener;

    public void setUpdateListener(OnJammerUpdate listener) {
        this.listener = listener;
    }

    public interface OnJammerUpdate {
        void sync();
    }

    public static class Builder {
        private String name;
        private Long score;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setScore(Long score) {
            this.score = score;
            return this;
        }

        public Jammer build(String userId) {
            Jammer jammer = new Jammer(userId, this);
            if (jammer.name == null) jammer.name = "Unknown Jammer";
            if (jammer.score == null) jammer.score = 0L;

            db.syncJammerWithDB(jammer);
            return jammer;
        }
    }

    private Jammer(String userID, final Builder builder) {
        this.id = userID;
        this.name = builder.name;
        this.score = builder.score;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public int getScore() {
        return score.intValue();
    }

    public void triggerSync() {
        if (listener == null) return;
        listener.sync();
    }
}
