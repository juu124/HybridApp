package com.dki.hybridapptest.DTO;

import com.google.gson.annotations.SerializedName;

public class DTORetrofit {
    @SerializedName("data")
    private DTOdata data;

    @SerializedName("support")
    private DTORetrofitSupport support;

    public DTOdata getData() {
        return data;
    }

    public void setData(DTOdata data) {
        this.data = data;
    }

    public DTORetrofitSupport getSupport() {
        return support;
    }

    public void setSupport(DTORetrofitSupport support) {
        this.support = support;
    }
}
