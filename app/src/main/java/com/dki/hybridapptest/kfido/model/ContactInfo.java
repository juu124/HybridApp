package com.dki.hybridapptest.kfido.model;

import com.dki.hybridapptest.utils.Utils;

import java.util.Objects;

public class ContactInfo {

    public long id;
    public long photoId;
    public String displayName;
    public String phoneNumber;
    public String initial;

    public ContactInfo() {

    }

    public ContactInfo(long id, long photoId, String displayName, String phoneNumber) {
        this.id = id;
        this.photoId = photoId;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;

        String initial = Utils.getHangulInitialSound(displayName);

        this.initial = initial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactInfo that = (ContactInfo) o;
        return Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }
}