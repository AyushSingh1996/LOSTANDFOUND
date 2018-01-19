package com.lostandfound.lostandfound;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Qrcode_generate extends AppCompatActivity {

    ImageView imageView;
    String QRString ;
    public final static int QRcodeWidth = 500 ;
    private static final String TAG = "ViewDatabase";


    String userID;
    Bitmap bitmap ;
    Button SaveQR;
    Button GotoLost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr_code);

        imageView = (ImageView)findViewById(R.id.imageView);
        SaveQR=(Button)findViewById(R.id.btnSaveQR);
        GotoLost=(Button)findViewById(R.id.btngotoLost);

        JSONObject json = new JSONObject();


        if (getIntent().hasExtra("name")) {
            String data = getIntent().getExtras().getString("name");

            try {
                json.put("NAME", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (getIntent().hasExtra("phoneNum")) {
            String data = getIntent().getExtras().getString("phoneNum");

            try {
                json.put("PHONE NUMBER", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        QRString=json.toString();

        try {
            bitmap = TextToImageEncode(QRString);

            imageView.setImageBitmap(bitmap);

            //and displaying a success toast
            Toast.makeText(getApplicationContext(), "QR code generated ", Toast.LENGTH_LONG).show();

        } catch (WriterException e) {
            e.printStackTrace();
        }




        SaveQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView!=null){
                    saveImageToInternalStorage(bitmap,Qrcode_generate.this,userID);
                }

            }
        });

        GotoLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Qrcode_generate.this,Lost_activity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        });

    }


    public boolean saveImageToInternalStorage(Bitmap image,Context context,String UserId)
    {
        try {
            FileOutputStream fos = context.openFileOutput(UserId+".jpg", Context.MODE_WORLD_READABLE);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            // 100 means no compression, the lower you go, the stronger the compression
            fos.close();
            return true;
        }
        catch (Exception e) {
            Toast.makeText(context,"not saved",Toast.LENGTH_SHORT);
            Log.e("saveToInternalStorage()", e.getMessage());
        }
        return false;
    }

     /*  public void SaveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }
*/
    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}