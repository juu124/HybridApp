package com.dki.hybridapptest.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.adapters.RvUserListAdapter;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.dto.UsersDetail;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {
    private RecyclerView rvUserList;
    private Button btnUserListMore;
    private RvUserListAdapter rvUserListAdapter;
    private ArrayList<UserResponse> mArrUser = new ArrayList<>();
    private UsersDetail usersDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        rvUserList = findViewById(R.id.rv_user_list);
        btnUserListMore = findViewById(R.id.btn_user_list_more);

        RetrofitApiManager.getInstance().requestUserInfoList(new RetrofitInterface() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    GLog.d("response Successful == " + response.body());
                    usersDetail = (UsersDetail) response.body();
                    mArrUser = usersDetail.getArrDtoUser();

//                     for문 처리 (for each)
                    for (UserResponse mArrUser2 : usersDetail.getArrDtoUser()) {
                        mArrUser.add(mArrUser2);
                    }

                    // for문 처리 (for i)
//                    for (int i = 0; i < a.getArrDtoUser().size(); i++) {
//                        DtoUser mUser = new DtoUser(
//                                a.getArrDtoUser().get(i).getId(),
//                                a.getArrDtoUser().get(i).getEmail(),
//                                a.getArrDtoUser().get(i).getFirstName(),
//                                a.getArrDtoUser().get(i).getLastName(),
//                                a.getArrDtoUser().get(i).getAvatar());
//                        mArrUser.add(mUser);
//                    }
                    rvUserListAdapter.setArrUser(mArrUser);
                    rvUserListAdapter.notifyDataSetChanged();

                    if (usersDetail.getPage() >= usersDetail.getTotalPages()) {
                        btnUserListMore.setVisibility(View.GONE);
                    } else {
                        btnUserListMore.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GLog.d("오류 메세지 == " + t.toString());
            }
        });

        btnUserListMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GLog.d("btnUserListMore onClick");
                RetrofitApiManager.getInstance().requestUserNextList(usersDetail.getPage() + 1, new RetrofitInterface() {
                    @Override
                    public void onResponse(Response response) {
                        if (response.isSuccessful() && response.body() != null) {
                            usersDetail = (UsersDetail) response.body();
                            if (usersDetail.getPage() <= usersDetail.getTotalPages()) {
                                mArrUser = usersDetail.getArrDtoUser();
                            }
                            rvUserListAdapter.setArrUser(mArrUser);
                            rvUserListAdapter.notifyDataSetChanged();
                            if (usersDetail.getPage() >= usersDetail.getTotalPages()) {
                                btnUserListMore.setVisibility(View.GONE);
                            } else {
                                btnUserListMore.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        GLog.d("오류 메세지 == " + t.toString());
                    }
                });
            }
        });

        rvUserList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvUserListAdapter = new RvUserListAdapter();
        rvUserList.setAdapter(rvUserListAdapter);
    }
}