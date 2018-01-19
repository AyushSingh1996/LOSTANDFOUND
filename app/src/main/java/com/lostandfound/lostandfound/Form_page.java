package com.lostandfound.lostandfound;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Random;

import static android.R.attr.max;
import static android.R.attr.name;


/**
 * Created by Ayush on 21/05/2017.
 */

public class Form_page extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int CAMERA_REQUEST = 1888;
    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1098;
    private ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;
    Button gallery;
    Button camera;
    Button LocateAddress;
    Button Submit;
    TextView Address;
    TextView Tag;
    EditText Description;
    EditText Type;
    private static final String TAG = "AddToDatabase";
    String[] category;
    String ObjectID;
    public Uri DownloadUri;

    private static String ObjectTag;
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseAuth auth;
    private String userID;
    private Uri filePath;
    private StorageReference storageReference;
    private String ObjectPic;
    Spinner spinnerCategory;
    String Category;
    EditText ItemName;
    String UserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_page);
        Description = (EditText) findViewById(R.id.EnterDescription);
        ItemName = (EditText) findViewById(R.id.ItemName);
        Type = (EditText) findViewById(R.id.Type);
        auth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = user.getUid();
        Tag = (TextView) findViewById(R.id.Tag);
        ObjectPic = "gs://lostandfound-eca02.appspot.com/FormImages/" + userID + ".jpg";
        UserEmail = user.getEmail();
        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        appbar.setTitle("Fill the Details");
        setSupportActionBar(appbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Random rand = new Random();


        Toast.makeText(Form_page.this, UserEmail, Toast.LENGTH_SHORT).show();

        if (getIntent().hasExtra("IntentType")) {


            String data = getIntent().getExtras().getString("IntentType");
            Tag.setText(data);
            ObjectTag = Tag.getText().toString();
        }

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
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        // Spinner element

        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        Submit = (Button) findViewById(R.id.Submit);
        LocateAddress = (Button) findViewById(R.id.btnformLocateAddress);
        Address = (TextView) findViewById(R.id.formaddress);

        // Spinner click listener
        spinnerCategory.setOnItemSelectedListener(this);
        spinnerCategory.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        category = new String[]{"Electronics", "Accessories", "Clothes", "Documents", "Miscellaneous"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, category);
        spinnerCategory.setAdapter(adapter);


        imageView = (ImageView) findViewById(R.id.getimage);
        camera = (Button) this.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        gallery = (Button) findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(Form_page.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(Form_page.this,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(Form_page.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        ActivityCompat.requestPermissions(Form_page.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                } else {

                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });


        LocateAddress = (Button) findViewById(R.id.btnformLocateAddress);
        LocateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Form_page.this, SetLocation.class);
                i.putExtra("FromFormPage", "");
                startActivityForResult(i, SECOND_ACTIVITY_RESULT_CODE);
            }
        });
        int randomNum = rand.nextInt((100 - 1) + 1) + 1;
        ObjectID = userID + randomNum;
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: Submit pressed.");
                String objecttag = ObjectTag;
                String address = Address.getText().toString();
                String type = Type.getText().toString();
                String category = Category;

                String itemname = ItemName.getText().toString();
                String description =  "Description : " + Description.getText().toString();
                String uri = DownloadUri.toString();


                Log.d(TAG, "onClick: Attempting to submit to database: \n" +
                        "objecttag: " + objecttag + "\n" +
                        "address: " + address + "\n" +
                        "type: " + type + "\n" +
                        "category: " + category + "\n"  +
                        "description: " + description + "\n"
                );
                int views = 0;
                //handle the exception if the EditText fields are null
                if (!objecttag.equals("") && !address.equals("") && !type.equals("") && !ObjectPic.equals("")
                        && !type.equals("") && !category.equals("") && !itemname.equals("") && !description.equals("") && !UserEmail.equals("")) {
                    if (ObjectTag.equalsIgnoreCase("Lost")) {

                        ObjectInformation objectInformation = new ObjectInformation(objecttag, category, type, ObjectPic, description, address, itemname, uri, UserEmail, views,ObjectID);
                        myRef.child("Objects Lost").child(ObjectID).setValue(objectInformation);
                        toastMessage("New Information has been saved.");
                        Address.setText("");
                        Type.setText("");
                        ObjectPic = "";
                        ItemName.setText("");
                        Description.setText("");
                    } else if (ObjectTag.equalsIgnoreCase("Found")) {

                        ObjectInformation objectInformation = new ObjectInformation(objecttag, category, type, ObjectPic, description, address, itemname, uri, UserEmail, views,ObjectID);
                        myRef.child("Objects Found").child(ObjectID).setValue(objectInformation);

                        toastMessage("New Information has been saved.");
                        Address.setText("");
                        Type.setText("");
                        ObjectPic = "";
                        ItemName.setText("");
                        Description.setText("");
                    }


                    Intent i = new Intent(Form_page.this, Lost_activity.class);
                    startActivity(i);
                    finish();
                } else {
                    toastMessage("Fill out all the fields");
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            filePath = data.getData();
            uploadFile();

        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            filePath = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(filePath,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.getimage);

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(filePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);
            uploadFile();


        }
        if (requestCode == SECOND_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                String returnString = data.getStringExtra("Address");

                // set text view with string

                Address.setText(returnString);
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


    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category = category[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Category = "";
    }

    private void uploadFile() {
        //if there is a file to upload
        Random rand = new Random();
        int randomNum = rand.nextInt((100 - 1) + 1) + 1;
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = storageReference.child("FormImages/" + userID + randomNum + ".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            DownloadUri = taskSnapshot.getDownloadUrl();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
