package com.example.protik.helpcircle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    private String mCurrentState;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private FirebaseUser mCurrentUser;
    private Button sendButton, declineButton;


    public List<User> user_list;

    public UserRecyclerAdapter(List<User> user_list) {
        this.user_list = user_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String nameData = user_list.get(position).getName();
        String phoneData = user_list.get(position).getPhone();
        String emailData = user_list.get(position).getEmail();
        String userIdData = user_list.get(position).getUser_id();

        holder.setText(nameData, phoneData, emailData, userIdData);
    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView nameView, phoneView, emailView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setText(String nameTxt, String phoneTxt, String emailTxt, String userIdTxt) {

            nameView = mView.findViewById(R.id.nameId);
            phoneView = mView.findViewById(R.id.phoneId);
            emailView = mView.findViewById(R.id.emailId);
//            sendButton = mView.findViewById(R.id.sendBtnId);
//            declineButton = mView.findViewById(R.id.declineBtnId);

            nameView.setText(nameTxt);
            phoneView.setText(phoneTxt);
            emailView.setText(emailTxt);
            final String userId = userIdTxt;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mView.getContext(), "Id: "+ userId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("user_id", userId);
                    v.getContext().startActivity(intent);
                }
            });

//            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
//            mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
//            mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
//            mCurrentState = "not_friend";
//
//            //Friends Part
//            mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    if (dataSnapshot.hasChild(userId)) {
//
//                        String req_type = dataSnapshot.child(userId).child("request_type").getValue().toString();
//
//                        if (req_type.equals("received")) {
//
//                            mCurrentState = "req_received";
//                            sendButton.setText("Accept Request");
//
//                        } else if (req_type.equals("sent")){
//
//                            mCurrentState = "req_sent";
//                            sendButton.setText("Cancel Request");
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            sendButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    createFriends(userId, mView);
//                }
//            });
//
//            declineButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(mView.getContext(), "Decline korsi", Toast.LENGTH_LONG).show();
//                }
//            });

        }
    }

    public void createFriends(final String uId, final View view) {
        Toast.makeText(view.getContext(), "Id: " + uId, Toast.LENGTH_LONG).show();

        sendButton.setEnabled(false);

        if (mCurrentState.equals("not_friend")) {

            mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFriendRequestDatabase.child(uId).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(view.getContext(), "Successfully sent request", Toast.LENGTH_SHORT).show();
                                sendButton.setEnabled(true);
                                mCurrentState = "req_sent";
                                sendButton.setText("Cancel Request");
                            }
                        });

                    } else {
                        Toast.makeText(view.getContext(), "Failed Sending Request", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (mCurrentState.equals("req_sent")) {

            mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mFriendRequestDatabase.child(uId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            sendButton.setEnabled(true);
                            mCurrentState = "not_friend";
                            sendButton.setText("Add Friend");
                        }
                    });
                }
            });
        }

        if (mCurrentState.equals("req_received")) {

            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            mFriendDatabase.child(mCurrentUser.getUid()).child(uId).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mFriendDatabase.child(uId).child(mCurrentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestDatabase.child(uId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            sendButton.setEnabled(true);
                                            mCurrentState = "friends";
                                            sendButton.setText("UnFriend");
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            });
        }
    }
}
