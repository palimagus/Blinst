package com.palim.blinstapp.core;

import java.util.ArrayList;

public class Session {
    private final String reference;
    private String token;
    private ArrayList<String> jammers;
    private SessionState state;

    OnSessionUpdate listener;

    public Session(String ref) {
        reference = ref;
    }

    public void setUpdateListener(OnSessionUpdate listener) {
        this.listener = listener;
    }

    public interface OnSessionUpdate {
        void sync();
    }

    public String getReference() {
        return reference;
    }
}
