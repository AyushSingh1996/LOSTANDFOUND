package com.lostandfound.lostandfound;

import android.app.Activity;

import com.lostandfound.lostandfound.Enter_User_Info;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.FileDescriptor;
import java.io.IOException;

import static com.lostandfound.lostandfound.Enter_User_Info.*;

/**
 * Created by Ayush on 21/05/2017.
 */

public class UserProfile extends AppCompatActivity {
    ImageView UserPic;
    TextView ProfileName;
    TextView ProfilePhone;
    TextView ProfileAddress;
    AlertDialog.Builder builderImageGetter;
    private static final String TAG = "ViewDatabase";
    UserInformation uInfo;
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private StorageReference storageReference;
    private  Uri uri;
    String userID;
    String userEmail;
    String ProfilePic;
    TextView tvEmail;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1098;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        UserPic = (ImageView) findViewById(R.id.SetUserProfilePic);
        ProfileName = (TextView) findViewById(R.id.profile_name);
        ProfileAddress = (TextView) findViewById(R.id.profile_address);
        ProfilePhone = (TextView) findViewById(R.id.profile_Phone);
        uInfo = new UserInformation();
        tvEmail = (TextView) findViewById(R.id.profile_email);
        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        appbar.setTitle("Profile");
        setSupportActionBar(appbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        userEmail = user.getEmail();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // Read from the database
        myRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


      //  StorageReference riversRef = storageReference.child("images/" + userID + ".jpg");



    }


    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue(UserInformation.class) != null) {
                if(userEmail.equalsIgnoreCase(ds.getValue(UserInformation.class).getEmail())){
                    uInfo.setName(ds.getValue(UserInformation.class).getName()); //set the name
                    uInfo.setPhone_num(ds.getValue(UserInformation.class).getPhone_num()); //set the phone_num
                    uInfo.setAddress(ds.getValue(UserInformation.class).getAddress()); //set the phone_num
                    uInfo.setProfile_pic(ds.getValue(UserInformation.class).getProfile_pic()); //set the Profile_pic
                    uInfo.setEmail(ds.getValue(UserInformation.class).getEmail()); //set the Profile_pic

                    //display all the information
                    Log.d(TAG, "showData: name: " + uInfo.getName());
                    Log.d(TAG, "showData: address: " + uInfo.getAddress());
                    Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());

                    ProfileAddress.setText(uInfo.getAddress());
                    ProfileName.setText(uInfo.getName());
                    ProfilePhone.setText(uInfo.getPhone_num());
                    ProfilePic=uInfo.getProfile_pic();
                    uri = Uri.parse(ProfilePic);
                    tvEmail.setText(uInfo.getEmail());

                    Glide.with(getApplicationContext()).load(uri)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(UserPic);
                }


            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}