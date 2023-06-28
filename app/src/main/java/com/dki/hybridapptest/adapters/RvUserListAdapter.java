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
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.utils.GLog;

import java.util.ArrayList;

public class RvUserListAdapter extends RecyclerView.Adapter<RvUserListAdapter.ViewHolder> {
    ArrayList<UserResponse> mArrUser = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userId;
        TextView userFirstName;
        TextView userLastName;
        TextView userEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.item_user_avatar);
            userId = itemView.findViewById(R.id.item_user_id);
            userFirstName = itemView.findViewById(R.id.item_user_first_name);
            userLastName = itemView.findViewById(R.id.item_user_last_name);
            userEmail = itemView.findViewById(R.id.item_user_email);
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
        holder.userId.setText(mArrUser.get(position).getId());
        Glide.with(holder.itemView)
                .load(mArrUser.get(position).getAvatar())
                .into(holder.userAvatar);
        holder.userFirstName.setText(mArrUser.get(position).getFirstName());
        holder.userLastName.setText(mArrUser.get(position).getLastName());
        holder.userEmail.setText(mArrUser.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mArrUser.size();
    }

    public void setArrUser(ArrayList<UserResponse> arrUser) {
        mArrUser.addAll(arrUser);
    }
}
