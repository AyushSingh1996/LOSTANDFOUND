package com.lostandfound.lostandfound;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Ayush on 18/05/2017.
 */

public class Lost_activity extends AppCompatActivity implements ItemClickListener, OnNavigationItemSelectedListener {
    private Context mContext;
    FloatingActionButton fab1dialog;

    AlertDialog.Builder builder;
    RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    Button Dialoglost;
    Button DialogFound;
    Button DialogScanQR;
    DrawerLayout drawer;
    private CardViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth auth;
    private static final String TAG = "ViewDatabase";
    ArrayList<String> Objects = new ArrayList<String>();
    ArrayList<String> Descriptions = new ArrayList<String>();
    ArrayList<String> Images = new ArrayList<String>();
    ArrayList<String> Email = new ArrayList<String>();
    ArrayList<String> Name = new ArrayList<String>();
    ArrayList<String> ObjectID = new ArrayList<String>();
    ArrayList<String> Category = new ArrayList<String>();
    ArrayList<String> Type = new ArrayList<String>();
    ArrayList<String> Location = new ArrayList<String>();

    ArrayList<Integer> Views = new ArrayList<Integer>();





    UserInformation uInfo;
    ObjectInformation objInfo;
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_activity);
        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        Toolbar appbar_bottom = (Toolbar) findViewById(R.id.appbar_bottom);

        auth = FirebaseAuth.getInstance();
        fab1dialog = (FloatingActionButton) findViewById(R.id.fab1dialog);
        uInfo = new UserInformation();

        appbar.setTitle("Lost");
        appbar.setTitleTextColor(R.color.QRCodeWhiteColor);
                setSupportActionBar(appbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu24);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        appbar_bottom.inflateMenu(R.menu.appbar_bottom_menu);
        // Add 10 spacing on either side of the toolbar
        appbar_bottom.setContentInsetsAbsolute(20, 20);
        // Get the ChildCount of your Toolbar, this should only be 1
        int childCount = appbar_bottom.getChildCount();
        // Get the Screen Width in pixels
        int screenWidth = metrics.widthPixels;

        // Create the Toolbar Params based on the screenWidth
        Toolbar.LayoutParams toolbarParams = new Toolbar.LayoutParams(screenWidth, Toolbar.LayoutParams.WRAP_CONTENT);

        // Loop through the child Items
        for (int i = 0; i < childCount; i++) {
            // Get the item at the current index
            View childView = appbar_bottom.getChildAt(i);
            // If its a ViewGroup
            if (childView instanceof ViewGroup) {
                // Set its layout params
                childView.setLayoutParams(toolbarParams);
                // Get the child count of this view group, and compute the item widths based on this count & screen size
                int innerChildCount = ((ViewGroup) childView).getChildCount();
                int itemWidth = (screenWidth / innerChildCount);
                // Create layout params for the ActionMenuView
                ActionMenuView.LayoutParams params = new ActionMenuView.LayoutParams(itemWidth - 10, ActionMenuView.LayoutParams.WRAP_CONTENT);
                // Loop through the children
                for (int j = 0; j < innerChildCount; j++) {
                    View grandChild = ((ViewGroup) childView).getChildAt(j);
                    if (grandChild instanceof ActionMenuItemView) {
                        // set the layout parameters on each View
                        grandChild.setLayoutParams(params);
                    }
                }
            }
        }
        appbar_bottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                if (arg0.getItemId() == R.id.action_History) {

                }
                if (arg0.getItemId() == R.id.action_notification) {

                }
                if (arg0.getItemId() == R.id.action_Nearby) {
                    Intent i = new Intent(Lost_activity.this, Nearby.class);
                    i.putExtra("TAG","Lost");

                    startActivity(i);

                }
                return false;
            }
        });

        fab1dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(Lost_activity.this);
                // Get the layout inflater
                LayoutInflater inflater = Lost_activity.this.getLayoutInflater();

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(inflater.inflate(R.layout.dialog_box, null));
                builder.setTitle("Select an option");

                // Add action buttons
                builder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder.create();
                final AlertDialog alert11 = builder.create();
                alert11.show();
                DialogFound = (Button) alert11.findViewById(R.id.dialog_found);
                Dialoglost = (Button) alert11.findViewById(R.id.dialog_lost);
                DialogScanQR = (Button) alert11.findViewById(R.id.dialog_ScanQR);
                Dialoglost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Lost_activity.this, Form_page.class);
                        i.putExtra("IntentType", "Lost");
                        startActivity(i);
                        alert11.dismiss();
                    }
                });
                DialogFound.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Lost_activity.this, Form_page.class);
                        i.putExtra("IntentType", "Found");

                        startActivity(i);
                        alert11.dismiss();

                    }
                });
                DialogScanQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Lost_activity.this, Qrcode_main.class);
                        startActivity(i);
                        alert11.dismiss();


                    }
                });
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Get the application context
        mContext = getApplicationContext();


        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

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
        myRef.child("Objects Lost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());


                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbarmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_filter:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case android.R.id.home:
                drawer.openDrawer(Gravity.LEFT);


                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Found) {
            Intent i = new Intent(Lost_activity.this, Found_activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else if (id == R.id.nav_Lost) {
            Intent i = new Intent(Lost_activity.this, Lost_activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(Lost_activity.this, UserProfile.class);
            startActivity(i);

        } else if (id == R.id.nav_qr_code) {
            Intent i = new Intent(Lost_activity.this, Qrcode_main.class);
            startActivity(i);

        } else if (id == R.id.nav_signout) {
            auth.signOut();
            Intent i = new Intent(Lost_activity.this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();


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


    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void showData(DataSnapshot dataSnapshot) {
        Objects.clear();
        Descriptions.clear();
        Images.clear();

        objInfo = new ObjectInformation();


        for (DataSnapshot ds : dataSnapshot.getChildren()) {


            if (ds.getValue(ObjectInformation.class) != null) {
                objInfo.setItemName(ds.getValue(ObjectInformation.class).getItemName()); //set the name
                objInfo.setDescription(ds.getValue(ObjectInformation.class).getDescription()); //set the phone_num
                objInfo.setUri(ds.getValue(ObjectInformation.class).getUri()); //set the phone_num
                objInfo.setUserEmail(ds.getValue(ObjectInformation.class).getUserEmail());
                objInfo.setViews(ds.getValue(ObjectInformation.class).getViews());
                objInfo.setObjectID(ds.getValue(ObjectInformation.class).getObjectID());
                objInfo.setCategory(ds.getValue(ObjectInformation.class).getCategory());
                objInfo.setType(ds.getValue(ObjectInformation.class).getType());
                objInfo.setAddress(ds.getValue(ObjectInformation.class).getAddress());





                //display all the information
                Log.d(TAG, "showData: name: " + objInfo.getItemName());
                Log.d(TAG, "showData: description: " + objInfo.getDescription());
                Log.d(TAG, "showData: Objectid: " + objInfo.getObjectID());


                // Initialize a new String array for objects of staggered view
                Objects.add(objInfo.getItemName());
                Descriptions.add(objInfo.getDescription());
                Images.add(objInfo.getUri());
                Email.add(objInfo.getUserEmail());
                Name.add(objInfo.getItemName());
                Views.add(objInfo.getViews());
                ObjectID.add(objInfo.getObjectID());
                Category.add(objInfo.getCategory());
                Type.add(objInfo.getType());
                Location.add(objInfo.getAddress());





            }

        }

         /*
            StaggeredGridLayoutManager
                A LayoutManager that lays out children in a staggered grid formation. It supports
                horizontal & vertical layout as well as an ability to layout children in reverse.

                Staggered grids are likely to have gaps at the edges of the layout. To avoid these
                gaps, StaggeredGridLayoutManager can offset spans independently or move items
                between spans. You can control this behavior via setGapStrategy(int).
        */
        /*
            public StaggeredGridLayoutManager (int spanCount, int orientation)
                Creates a StaggeredGridLayoutManager with given parameters.

            Parameters
                spanCount : If orientation is vertical, spanCount is number of columns.
                    If orientation is horizontal, spanCount is number of rows.
                orientation : VERTICAL or HORIZONTAL
        */
        // Define a layout for RecyclerView
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialize a new instance of RecyclerView Adapter instance
        mAdapter = new CardViewAdapter(mContext, Objects,Location, Images);

        // Set the adapter for RecyclerView
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new CardViewAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                myRef.child("Objects Lost").child(ObjectID.get(position)).child("views").setValue(Views.get(position)+1);

                // Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Lost_activity.this, Open_card_layout_item.class);
                i.putExtra("CardName", Objects.get(position));
                i.putExtra("CardDescription", Descriptions.get(position));
                i.putExtra("Images", Images.get(position));
                i.putExtra("Email",  Email.get(position));
                i.putExtra("From",  "Objects Lost");
                i.putExtra("Name",  Name.get(position));
                i.putExtra("Views",  Views.get(position)+1);
                i.putExtra("Type",  Type.get(position));
                i.putExtra("Location",  Location.get(position));
                i.putExtra("Category",  Category.get(position));






                startActivity(i);


            }


        });

    }


}
