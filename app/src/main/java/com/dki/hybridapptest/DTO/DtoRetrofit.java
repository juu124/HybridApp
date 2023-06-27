package com.dki.hybridapptest.DTO;

import com.google.gson.annotations.SerializedName;

public class DtoRetrofit extends DtoCommon {
    @SerializedName("data")
    private DtoUser data;

    @SerializedName("support")
    private DtoSupport support;

    public DtoUser getData() {
        return data;
    }

    public void setData(DtoUser data) {
        this.data = data;
    }

    public DtoSupport getSupport() {
        return support;
    }

    public void setSupport(DtoSupport support) {
        this.support = support;
    }
}
