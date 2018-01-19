package com.lostandfound.lostandfound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Ayush on 21/05/2017.
 */

public class Open_card_layout_item extends AppCompatActivity {

    TextView CardName;
    TextView CardDescription;
    TextView LostFound;
    String from;
    ImageView CardImage;
    String category;
    String type;
    String location;
    String userEmail;

    String itemName;
    UserInformation uInfo;

    private static final String TAG = "ViewDatabase";

    public static int sCorner = 15;
    public static int sMargin = 2;
    Button ViewProfile;
    private FirebaseDatabase mFirebaseDatabase;
    TextView name;
    TextView Viewcount;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.open_cardlayout_item);
        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        appbar.setTitle("");
        setSupportActionBar(appbar);
        uInfo = new UserInformation();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LostFound = (TextView) findViewById(R.id.textviewLostorfound);
        Viewcount = (TextView) findViewById(R.id.textviewViewscount);

        name = (TextView) findViewById(R.id.textviewName);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
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

        myRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((!userEmail.equalsIgnoreCase("")) && (!userEmail.equalsIgnoreCase("null"))) {
                    showData(dataSnapshot);


                } else {
                    name.setText("Unknown");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });





        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        ViewProfile = (Button) findViewById(R.id.visitprofile);
        CardDescription = (TextView) findViewById(R.id.tv_open_cardDescription);
        CardImage = (ImageView) findViewById(R.id.open_cardimageView);
        if (getIntent().hasExtra("CardName")) {
            String data = getIntent().getExtras().getString("CardName");
            appbar.setTitle(data);
        }
        if (getIntent().hasExtra("Type")) {
             type = getIntent().getExtras().getString("Type");
        }
        if (getIntent().hasExtra("Location")) {
             location = getIntent().getExtras().getString("Location");
        }
        if (getIntent().hasExtra("Category")) {
             category = getIntent().getExtras().getString("Category");
        }
        if (getIntent().hasExtra("CardDescription")) {
            String data = "Category : "+category+"\n"+"Type : "+type+"\n"+
                   getIntent().getExtras().getString("CardDescription")+"\n"+"Location : "+location+"\n";
            CardDescription.setText(data);
        }
        if (getIntent().hasExtra("Views")) {
            int count = getIntent().getExtras().getInt("Views");
            String data= ""+count;
            Viewcount.setText(data);
        }
        if (getIntent().hasExtra("Email")) {
            userEmail = "" + getIntent().getExtras().getString("Email");

        }

        if (getIntent().hasExtra("Name")) {
            itemName = getIntent().getExtras().getString("Name");

        }
        if (getIntent().hasExtra("From")) {
            if ( from== getIntent().getExtras().getString("Objects Lost"))
                LostFound.setText("Lost");
            else if (from == getIntent().getExtras().getString("Objects Found"))
                LostFound.setText("Found");
            else
                LostFound.setText("Lost/Found");



        }
        if (getIntent().hasExtra("Images")) {
            String data = getIntent().getExtras().getString("Images");
            String getUri = data;

            Uri myuri = Uri.parse(getUri);

            Glide.with(getApplicationContext()).load(myuri).bitmapTransform(new RoundedCornersTransformation(Open_card_layout_item.this, sCorner, sMargin)).fitCenter()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(CardImage);
        }

        ViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((!userEmail.equalsIgnoreCase("")) && (!userEmail.equalsIgnoreCase("null"))) {
                    Intent i = new Intent(Open_card_layout_item.this, View_profile.class);
                    i.putExtra("Email", userEmail);
                    startActivity(i);

                } else {
                    Toast.makeText(Open_card_layout_item.this, "Sorry user information unavailable. :(", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue(UserInformation.class) != null) {
                if (userEmail.equalsIgnoreCase(ds.getValue(UserInformation.class).getEmail())) {
                    uInfo.setName(ds.getValue(UserInformation.class).getName()); //set the name

                    //display all the information
                    Log.d(TAG, "showData: name: " + uInfo.getName());

                    name.setText(uInfo.getName());


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
}
