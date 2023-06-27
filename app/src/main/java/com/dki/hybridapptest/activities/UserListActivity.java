package com.dki.hybridapptest.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.adapters.RvUserListAdapter;
import com.dki.hybridapptest.dto.DtoJson;
import com.dki.hybridapptest.dto.DtoUser;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {
    private RecyclerView rvUserList;
    private RvUserListAdapter rvUserListAdapter;
    private ArrayList<DtoUser> mArrUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        rvUserList = findViewById(R.id.rv_user_list);

        RetrofitApiManager.getInstance().requestUserInfoList(new RetrofitInterface() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    GLog.d("잘 넘어왔나요? === " + response.body());

                    DtoJson a = (DtoJson) response.body();
//                    GLog.d("리스트 사이즈" + a.getArrDtoUser().size());
//                    GLog.d("리스트 id ====" + a.getArrDtoUser().get(0).getId());

                    mArrUsers = a.getArrDtoUser();
//                    for (int i = 0; i < a.getArrDtoUser().size(); i++) {
//                        DtoUser mArrUser = new DtoUser(
//                                a.getArrDtoUser().get(i).getId(),
//                                a.getArrDtoUser().get(i).getEmail(),
//                                a.getArrDtoUser().get(i).getFirstName(),
//                                a.getArrDtoUser().get(i).getLastName(),
//                                a.getArrDtoUser().get(i).getAvatar());
//                        mArrUsers.add(mArrUser);
//                        GLog.d("리스트  ====" + a.getArrDtoUser().get(i).getFirstName());
//                    }
                    rvUserListAdapter.setArrUser(mArrUsers);
                    rvUserListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GLog.d("오류 메세지 == " + t.toString());
            }
        });

        rvUserList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvUserListAdapter = new RvUserListAdapter();
        rvUserList.setAdapter(rvUserListAdapter);
    }
}