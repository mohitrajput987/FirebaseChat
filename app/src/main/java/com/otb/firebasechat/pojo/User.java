package com.otb.firebasechat.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Mohit Rajput on 11/7/17.
 * TODO: Insert javadoc information here
 */

@IgnoreExtraProperties
public class User {
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
