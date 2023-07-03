package com.dki.hybridapptest.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.UserDialog;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.sorting.SortArrayList;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvUserListAdapter extends RecyclerView.Adapter<RvUserListAdapter.ViewHolder> {
    private ArrayList<UserResponse> mUserList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(UserResponse user);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnItemClickListener mOnItemClickListener;
        private UserResponse mUserResponse;

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
        private UserDialog userDialog;

        public ViewHolder(@NonNull View itemView/*, OnItemClickListener onItemClickListener*/) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.item_user_avatar);
            userIdType = itemView.findViewById(R.id.dialog_user_id_type);
            userId = itemView.findViewById(R.id.item_user_id_tv);
            userName = itemView.findViewById(R.id.dialog_user_name_type);
            userFirstName = itemView.findViewById(R.id.item_user_first_name_tv);
            userLastName = itemView.findViewById(R.id.item_user_last_name_tv);
            userEmailType = itemView.findViewById(R.id.dialog_user_email_type);
            userEmail = itemView.findViewById(R.id.item_user_email_tv);


//            mOnItemClickListener = onItemClickListener;
//            itemView.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GLog.d("아이템을 클릭했습니다.");
                    GLog.d();
                    userDialog = new UserDialog(itemView.getContext(), getAdapterPosition());
                    userDialog.show();
                }
            });
        }

        public void bindData(UserResponse userResponse) {
            mUserResponse = userResponse;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null && mUserResponse != null) {
                mOnItemClickListener.onItemClick(mUserResponse);
            }
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
                .placeholder(R.drawable.sample_image)
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

    public void addArrUser(ArrayList<UserResponse> userList) {
        mUserList.addAll(userList);
    }

    public void addSortUser(int idNum, UserResponse user) {
        mUserList.add(idNum, user);
        mUserList.sort(new SortArrayList());
        for (int i = 0; i < mUserList.size(); i++) {
            GLog.d(mUserList.get(i).getId());
            GLog.d(mUserList.get(i).getEmail());
        }
        notifyDataSetChanged();
    }
}