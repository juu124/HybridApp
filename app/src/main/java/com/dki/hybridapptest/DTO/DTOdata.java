package com.dki.hybridapptest.DTO;

import com.google.gson.annotations.SerializedName;

public class DTOdata {
    @SerializedName("id")
    String id;

    @SerializedName("email")
    String email;

    @SerializedName("first_name")
    String first_Name;

    @SerializedName("last_name")
    String last_Name;

    @SerializedName("avatar")
    String avatar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return first_Name;
    }

    public void setFirstName(String firstName) {
        this.first_Name = firstName;
    }

    public String getLastName() {
        return last_Name;
    }

    public void setLastName(String lastName) {
        this.last_Name = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
