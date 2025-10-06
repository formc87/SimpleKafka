package com.example.producer.sso;

public class SamlUser {

    private final String nameId;
    private final String displayName;
    private final String email;

    public SamlUser(String nameId, String displayName, String email) {
        this.nameId = nameId;
        this.displayName = displayName;
        this.email = email;
    }

    public String getNameId() {
        return nameId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }
}
