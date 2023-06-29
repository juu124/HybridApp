package com.dki.hybridapptest.adapters;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.UserDataSupport;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

import retrofit2.Response;

public class RvUserListAdapter extends RecyclerView.Adapter<RvUserListAdapter.ViewHolder> {
    private ArrayList<UserResponse> mUserList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 뷰 홀더
        private ImageView userAvatar;
        private TextView userIdType;
        private TextView userId;
        private TextView userName;
        private TextView userFirstName;
        private TextView userLastName;
        private TextView userEmailType;
        private TextView userEmail;

        // 다이얼로그
        private Dialog dialog;
        private TextView userInfo;
        private Button dialogNoBtn;
        private Button dialogYesBtn;
        private String text = null;

        public void userInfoSetting(String id, String email, String firstName, String lastName) {
            text = "Id : " + id + "\n"
                    + "Email : " + email + "\n"
                    + "Name : " + firstName + " " + lastName;
            GLog.d("text 잘 나왔나" + text);
        }

        public void showDialog() {
            dialog.show();
            userInfo = dialog.findViewById(R.id.user_info);
            dialogNoBtn = dialog.findViewById(R.id.noButton);
            dialogYesBtn = dialog.findViewById(R.id.yesButton);

            RetrofitApiManager.getInstance().requestOneUserInfo((getAdapterPosition() + 1) + "", new RetrofitInterface() {
                @Override
                public void onResponse(Response response) {
                    GLog.d("onResponse");
                    UserDataSupport userResponse = (UserDataSupport) response.body();
                    userInfoSetting(userResponse.getDtoUser().getId(), userResponse.getDtoUser().getEmail(), userResponse.getDtoUser().getFirstName(), userResponse.getDtoUser().getLastName());
                    userInfo.setText(text);
                }

                @Override
                public void onFailure(Throwable t) {
                    GLog.d("오류 메세지 == " + t.toString());
                }
            });

            dialogNoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialogYesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.item_user_avatar);
            userIdType = itemView.findViewById(R.id.item_user_id_type);
            userId = itemView.findViewById(R.id.item_user_id_tv);
            userName = itemView.findViewById(R.id.item_user_name_type);
            userFirstName = itemView.findViewById(R.id.item_user_first_name_tv);
            userLastName = itemView.findViewById(R.id.item_user_last_name_tv);
            userEmailType = itemView.findViewById(R.id.item_user_email_type);
            userEmail = itemView.findViewById(R.id.item_user_email_tv);

            dialog = new Dialog(itemView.getContext());
            dialog.setContentView(R.layout.dialog_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GLog.d("아이템을 클릭했습니다.");
                    showDialog();
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GLog.d("onBindViewHolder");
        holder.userIdType.getText();
        holder.userId.setText(mUserList.get(position).getId());
        Glide.with(holder.itemView)
                .load(mUserList.get(position).getAvatar())
                .into(holder.userAvatar);
        holder.userName.getText();
        holder.userFirstName.setText(mUserList.get(position).getFirstName());
        holder.userLastName.setText(" " + mUserList.get(position).getLastName());
        holder.userEmailType.getText();
        holder.userEmail.setText(mUserList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void addArrUser(ArrayList<UserResponse> UserList) {
        mUserList.addAll(UserList);
    }
}
