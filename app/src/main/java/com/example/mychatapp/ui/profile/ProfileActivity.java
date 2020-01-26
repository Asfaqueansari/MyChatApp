package com.example.mychatapp.ui.profile;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mychatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private AppCompatTextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private AppCompatButton mProfileSendReqBtn,mProfileDeclineReqBtn;
    private DatabaseReference mUsersDatabase,mFriendRequestDatabase,mFriendDatabase,mNotificationDatabase;
    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrentUser;

    private String mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mProfileImage =findViewById(R.id.profile_image);
        mProfileName =findViewById(R.id.profile_name);
        mProfileStatus =findViewById(R.id.profile_status);
        mProfileFriendsCount =findViewById(R.id.total_friend);
        mProfileSendReqBtn =findViewById(R.id.send_friend_request);
        mProfileDeclineReqBtn = findViewById(R.id.decline_friend_request);
        /*mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
        mProfileDeclineReqBtn.setEnabled(false);*/

        mCurrentState = "not_friends";
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
        mProfileDeclineReqBtn.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading..Profile");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notification");
        final String  user_id = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(mProfileImage);
                //Friend List/ Request feature
                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(request_type.equals("Received")){
                                mCurrentState = "request_received";
                                mProfileSendReqBtn.setText("Accept friend Request");
                                mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineReqBtn.setEnabled(true);

                            }else if(request_type.equals("Sent")){
                                mCurrentState = "request_sent";
                                mProfileSendReqBtn.setText("Cancel Friend request");
                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineReqBtn.setEnabled(false);

                            }
                            mProgressDialog.dismiss();
                        }
                        else{
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        mCurrentState = "friends";
                                        mProfileSendReqBtn.setText("UnFriend ");
                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineReqBtn.setEnabled(false);
                                    }
                                    mProgressDialog.dismiss();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileSendReqBtn.setEnabled(false);

                //Not Friend State
                if(mCurrentState.equals("not_friends")){

                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override

                        public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                       .setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {

                                       HashMap<String,String> notificationData = new HashMap<>();
                                       notificationData.put("from",mCurrentUser.getUid());
                                       notificationData.put("type","request");

                                       mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               mCurrentState = "request_sent";
                                               mProfileSendReqBtn.setText("Cancel friend Request");
                                               mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                               mProfileDeclineReqBtn.setEnabled(false);
                                           }
                                       });
                                       //Toast.makeText(ProfileActivity.this, "request Sent successful", Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }else {
                               Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                           }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }
                //Cancel Request state
                if(mCurrentState.equals("request_sent")){
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrentState = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");
                                    mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineReqBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                //Request Received state
                if(mCurrentState.equals("request_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id)
                            .setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid())
                                    .setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    mFriendRequestDatabase.child(mCurrentUser.getUid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrentState = "friends";
                                                    mProfileSendReqBtn.setText("UnFriend ");
                                                    mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                    mProfileDeclineReqBtn.setEnabled(false);
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
        });
    }
}
