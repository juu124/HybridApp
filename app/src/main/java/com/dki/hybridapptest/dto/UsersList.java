package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UsersList {
    @SerializedName("page")
    private int page;

    @SerializedName("per_page")
    private int perPage;

    @SerializedName("total")
    private int total;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("data")
    private ArrayList<UserResponse> arrDtoUser;

    @SerializedName("support")
    private UserResponseSupport dtoSupport;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public ArrayList<UserResponse> getArrDtoUser() {
        return arrDtoUser;
    }

    public void setArrDtoUser(ArrayList<UserResponse> arrDtoUser) {
        this.arrDtoUser = arrDtoUser;
    }

    public UserResponseSupport getDtoSupport() {
        return dtoSupport;
    }

    public void setDtoSupport(UserResponseSupport dtoSupport) {
        this.dtoSupport = dtoSupport;
    }
}
