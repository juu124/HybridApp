package com.dki.hybridapptest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dki.hybridapptest.Interface.RemoveUserListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.UserDialog;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.sorting.SortArrayList;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvUserListAdapter extends RecyclerView.Adapter<RvUserListAdapter.ViewHolder> {
    private ArrayList<UserResponse> mUserList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 뷰 홀더
        private ImageView userAvatar;
        private TextView userId;
        private TextView userFirstName;
        private TextView userLastName;
        private TextView userEmail;

        // 다이얼로그
        private UserDialog userDialog;
        private boolean isAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.item_user_avatar);
            userId = itemView.findViewById(R.id.item_user_id_tv);
            userFirstName = itemView.findViewById(R.id.item_user_first_name_tv);
            userLastName = itemView.findViewById(R.id.item_user_last_name_tv);
            userEmail = itemView.findViewById(R.id.item_user_email_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    UserResponse userResponse = new UserResponse(userId.getText().toString(), userEmail.getText().toString(), userFirstName.getText().toString(), userLastName.getText().toString(), "", isAdd);
                    userDialog = new UserDialog(itemView.getContext(), userResponse, new RemoveUserListener() {
                        @Override
                        public void onRemoveClick() {
                            mUserList.remove(index);
                            notifyItemRemoved(index);
                        }
                    });
                    GLog.d("아이템을 클릭했습니다.");
                    userDialog.show();
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
        holder.userId.setText(mUserList.get(position).getId());
        Glide.with(holder.itemView)
                .load(mUserList.get(position).getAvatar())
                .placeholder(R.drawable.sample_image)
                .into(holder.userAvatar);
        holder.userFirstName.setText(mUserList.get(position).getFirstName());
        holder.userLastName.setText(mUserList.get(position).getLastName());
        holder.userEmail.setText(mUserList.get(position).getEmail());
        holder.isAdd = mUserList.get(position).isAdd();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void addArrUser(ArrayList<UserResponse> userList) {
        mUserList.addAll(userList);
    }

    public void addUser(UserResponse user) {
        if (user != null) {
            mUserList.add(user);
        }
    }

    public void sortUser() {
        mUserList.sort(new SortArrayList());
    }

    public int getIndexUser(UserResponse user) {
        for (int i = 0; i < mUserList.size(); i++) {
//            GLog.d("getIndexUser == " + mUserList.get(i));
            if (user == mUserList.get(i)) {
                return i;
            }
        }
        return 0;
    }
}