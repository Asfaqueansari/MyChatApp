package com.example.mychatapp.ui.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.mychatapp.R;
import com.example.mychatapp.ui.profile.ProfileActivity;
import com.example.mychatapp.utils.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;
    private ProgressDialog mProgressDialog;
    String searching_friend;

    private DatabaseReference mUsersDatabase;
    private AppCompatEditText mUsersSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mProgressDialog =  new ProgressDialog(UsersActivity.this);
        mProgressDialog.setTitle("Loading userlist..");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        mUsersSearch = findViewById(R.id.search_user_text);

        mUserList = findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.user_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searching_friend = s.toString();
                onStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searching_friend = s.toString();
                onStart();
            }

            @Override
            public void afterTextChanged(Editable s) {
                searching_friend = s.toString();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mProgressDialog.show();

        Query query = mUsersDatabase.orderByChild("Users")
                .startAt(searching_friend)
                .endAt(searching_friend + "\uf8ff");


        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query,Users.class)
                        .build();



        FirebaseRecyclerAdapter<Users,UserViewHolder> adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, final int position, @NonNull Users users) {

                userViewHolder.setName(users.getName());
                userViewHolder.setStatus(users.getStatus());
                userViewHolder.setImage(users.getImage());
                mProgressDialog.dismiss();

                // Identifying User profile On Which   Clicked
                userViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userId = getRef(position).getKey();
                        Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id",userId);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            public boolean onFailedToRecycleView(@NonNull UserViewHolder holder) {

                Toast.makeText(UsersActivity.this, "Failed`", Toast.LENGTH_SHORT).show();
                return super.onFailedToRecycleView(holder);

            }
        };
        mUserList.setAdapter(adapter);

        adapter.startListening();



    }

    public static class  UserViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            AppCompatTextView userNameView = mView.findViewById(R.id.users_name);
            userNameView.setText(name);
        }
        public void setStatus(String status){
            AppCompatTextView userStatusView = mView.findViewById(R.id.users_status);
            userStatusView.setText(status);
        }
        public void setImage(String image){
            CircleImageView userImageView = mView.findViewById(R.id.user_list_image);
            Picasso.get().load(image).placeholder(R.drawable.ic_icon).into(userImageView);
        }

    }

}
