
package com.lostandfound.lostandfound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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


/**
 * Created by Ayush on 15/05/2017.
 */
public class Enter_User_Info extends AppCompatActivity {
    TextView Address;
    EditText Fullname;
    EditText MobileNo;
    Button LocateAddress;
    Button Save;
    ImageButton ProfilePhoto;
    public static Uri DownloadUri;
    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;

    TextInputLayout signupinputlayoutMobileNo, signupinputlayoutFullname;
    private static final String TAG = "AddToDatabase";
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1098;

    private static int RESULT_LOAD_IMAGE = 1;
    AlertDialog.Builder builderImageGetter;
    private FirebaseAuth auth;
    private String userID;
    private Uri filePath;
    private StorageReference storageReference;
    private String ProfilePic;

    private String UserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_user_info);
        auth = FirebaseAuth.getInstance();
        Address = (TextView) findViewById(R.id.address);
        Fullname = (EditText) findViewById(R.id.editTextFull_name);
        MobileNo = (EditText) findViewById(R.id.mobile_no);
        Save = (Button) findViewById(R.id.buttonSave);
        ProfilePhoto = (ImageButton) findViewById(R.id.profile_photo);
        LocateAddress = (Button) findViewById(R.id.btnLocateAddress);
        signupinputlayoutFullname = (TextInputLayout) findViewById(R.id.signupinputlayoutFullname);
        signupinputlayoutMobileNo = (TextInputLayout) findViewById(R.id.signupinputlayoutMobileNo);
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = user.getUid();
        UserEmail=user.getEmail();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
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

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit pressed.");
                String name = Fullname.getText().toString();
                String address = Address.getText().toString();
                String phoneNum = MobileNo.getText().toString();
                String email = UserEmail;
                if(DownloadUri!=null){
                    ProfilePic=DownloadUri.toString();

                }

                Log.d(TAG, "onClick: Attempting to submit to database: \n" +
                        "name: " + name + "\n" +
                        "address: " + address + "\n" +
                        "phone number: " + phoneNum + "\n"+
                        "email: " + email + "\n"
                );

                //handle the exception if the EditText fields are null
                if(!name.equals("") && !address.equals("") && !phoneNum.equals("")&& !ProfilePic.equals("")&& !email.equals("")){
                    UserInformation userInformation = new UserInformation(address,name,phoneNum,ProfilePic,email);
                    myRef.child("users").child(userID).setValue(userInformation);
                    toastMessage("New Information has been saved.");
                    Fullname.setText("");
                    Address.setText("");
                    MobileNo.setText("");

                    Intent i = new Intent(Enter_User_Info.this,Qrcode_generate.class);

                    i.putExtra("name",name);
                    i.putExtra("phoneNum",phoneNum);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else{
                    toastMessage("Fill out all the fields");
                }






            }
        });



   
        LocateAddress = (Button) findViewById(R.id.btnLocateAddress);
        LocateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Enter_User_Info.this, SetLocation.class);
                i.putExtra("FromFormPage", "");
                startActivityForResult(i, SECOND_ACTIVITY_RESULT_CODE);
            }
        });

        ProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderImageGetter = new AlertDialog.Builder(Enter_User_Info.this);
                // Get the layout inflater

                builderImageGetter.setTitle("Select an option");

                // Add action buttons
                builderImageGetter.setNegativeButton(
                        "Camera",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                                dialog.cancel();
                            }
                        });
                builderImageGetter.setPositiveButton(
                        "Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (Build.VERSION.SDK_INT >= 23) {
                                    // Here, thisActivity is the current activity
                                    if (ContextCompat.checkSelfPermission(Enter_User_Info.this,
                                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {

                                        // Should we show an explanation?
                                        if (ActivityCompat.shouldShowRequestPermissionRationale(Enter_User_Info.this,
                                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                                            // Show an expanation to the user *asynchronously* -- don't block
                                            // this thread waiting for the user's response! After the user
                                            // sees the explanation, try again to request the permission.

                                        } else {

                                            // No explanation needed, we can request the permission.

                                            ActivityCompat.requestPermissions(Enter_User_Info.this,
                                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                                            // app-defined int constant. The callback method gets the
                                            // result of the request.
                                        }
                                    } else {
                                        ActivityCompat.requestPermissions(Enter_User_Info.this,
                                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                    }
                                } else {

                                    Intent i = new Intent(
                                            Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                                }
                                dialog.cancel();
                            }
                        });

                builderImageGetter.create();
                final AlertDialog alert11 = builderImageGetter.create();
                alert11.show();
            }
        });
    }




    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
            ProfilePhoto.setImageBitmap(photo);
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


            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(filePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ProfilePhoto.setImageBitmap(bmp);
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

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
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
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = storageReference.child("images/"+userID+".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                           DownloadUri= taskSnapshot.getDownloadUrl();
                            progressDialog.dismiss();

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

}