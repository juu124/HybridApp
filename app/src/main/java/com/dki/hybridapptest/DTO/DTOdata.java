package com.dki.hybridapptest.DTO;

import com.google.gson.annotations.SerializedName;

public class DTOdata {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("first_name")
    private String first_Name;

    @SerializedName("last_name")
    private String last_Name;

    @SerializedName("avatar")
    private String avatar;

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
