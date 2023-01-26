package com.palim.blinstapp.core;

public class Jammer {
    private final String reference;
    private String name;
    private Long score;

    OnJammerUpdate listener;

    public Jammer(String ref) {
        reference = ref;
    }

    public void setUpdateListener(OnJammerUpdate listener) {
        this.listener = listener;
    }

    public interface OnJammerUpdate {
        void sync();
    }

    public String getReference() {
        return reference;
    }
}
