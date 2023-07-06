package com.dki.hybridapptest.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dki.hybridapptest.Interface.InputUserInfoListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.adapters.RvUserListAdapter;
import com.dki.hybridapptest.dialog.InputUserDialog;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.dto.UsersList;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {
    // 리사이클러뷰 리스트
    private RecyclerView userRecyclerView;
    private Button btnUserListMore;
    private RvUserListAdapter rvUserListAdapter;
    private ArrayList<UserResponse> mUserList = new ArrayList<>();
    private UsersList usersDetail;
    private int page;
    private int totalPages;

    // 프로그래스 바
    private ProgressBar mProgressBar;

    // add 입력
    private Button btnUserListAdd;
    private InputUserDialog mInputUserDialog;
    private String Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        userRecyclerView = findViewById(R.id.rv_user_list);
        btnUserListMore = findViewById(R.id.btn_user_list_more);
        btnUserListAdd = findViewById(R.id.btn_user_list_add);
        mProgressBar = findViewById(R.id.indeterminate_progressbar);

        btnUserListMore.setVisibility(View.GONE);
        btnUserListAdd.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        // Divider 리스트 아이템 구별
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        userRecyclerView.addItemDecoration(dividerItemDecoration);

        RetrofitApiManager.getInstance().requestUserInfoList(new RetrofitInterface() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    GLog.d("response Successful == " + response.body());
                    usersDetail = (UsersList) response.body();
                    page = usersDetail.getPage();
                    totalPages = usersDetail.getTotalPages();
                    mUserList = usersDetail.getArrDtoUser();

//                     for문 처리 (for each)
//                    for (UserResponse mArrUser2 : usersDetail.getArrDtoUser()) {
//                        mUserList.add(mArrUser2);
//                    }

                    // for문 처리 (for i)
//                    for (int i = 0; i < usersDetail.getArrDtoUser().size(); i++) {
//                        DtoUser mUser = new DtoUser(
//                                usersDetail.getArrDtoUser().get(i).getId(),
//                                usersDetail.getArrDtoUser().get(i).getEmail(),
//                                usersDetail.getArrDtoUser().get(i).getFirstName(),
//                                usersDetail.getArrDtoUser().get(i).getLastName(),
//                                usersDetail.getArrDtoUser().get(i).getAvatar());
//                        mUserList.add(mUser);
//                    }
                    rvUserListAdapter.addArrUser(mUserList);
                    rvUserListAdapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.GONE);
                    if (page >= totalPages) {
                        btnUserListMore.setVisibility(View.GONE);
                        btnUserListAdd.setVisibility(View.VISIBLE);
                    } else {
                        btnUserListMore.setVisibility(View.VISIBLE);
                        btnUserListAdd.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GLog.d("오류 메세지 == " + t.toString());
            }
        });

        // List More 클릭
        btnUserListMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                GLog.d("btnUserListMore onClick");
                RetrofitApiManager.getInstance().requestUserNextList(page + 1, new RetrofitInterface() {
                    @Override
                    public void onResponse(Response response) {
                        if (response.isSuccessful() && response.body() != null) {
                            usersDetail = (UsersList) response.body();
                            page = usersDetail.getPage();
                            totalPages = usersDetail.getTotalPages();
                            if (page <= totalPages) {
                                mUserList = usersDetail.getArrDtoUser();
                            }
                            rvUserListAdapter.addArrUser(mUserList);
                            rvUserListAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                            if (page >= totalPages) {
                                btnUserListMore.setVisibility(View.GONE);
                                btnUserListAdd.setVisibility(View.VISIBLE);
                            } else {
                                btnUserListMore.setVisibility(View.VISIBLE);
                                btnUserListAdd.setVisibility(View.GONE);
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

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvUserListAdapter = new RvUserListAdapter();
        userRecyclerView.setAdapter(rvUserListAdapter);

        // List Add 클릭
        btnUserListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GLog.d("btnUserListAdd onClick");
                mInputUserDialog = new InputUserDialog(UserListActivity.this, new InputUserInfoListener() {
                    @Override
                    public void onInputPositiveClick(UserResponse userResponse) {
                        UserResponse mUser = new UserResponse(userResponse.getId(), userResponse.getEmail(), userResponse.getFirstName(), userResponse.getLastName(), Image, true);
                        GLog.d("저장 == " + userResponse.getId() + ", " + userResponse.getEmail() + ", " + userResponse.getFirstName() + ", " + userResponse.getLastName());
                        rvUserListAdapter.addUser(mUser);
                        rvUserListAdapter.sortUser();
                        rvUserListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onInputNegativeClick() {
                        Toast.makeText(UserListActivity.this, "취소", Toast.LENGTH_SHORT).show();
                    }
                });
                mInputUserDialog.show();
            }
        });
    }
}