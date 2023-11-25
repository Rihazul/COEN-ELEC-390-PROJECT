package com.example.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class Face_Scan extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference myStorage;
    private ImageView imageView;
    private Button captureButton;
    private Button uploadButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);

        myStorage = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.image_display);
        captureButton = findViewById(R.id.scan_pic);

       captureButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent,101);
           }
       });

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Activity.RESULT_OK)
        {
            if (requestCode == 101)
            {
                onCaptureResult(data);
            }
        }

    }

    private void onCaptureResult(Intent data)
    {
        Bitmap thumbnail =(Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes= new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,90,bytes);
        byte bb[] = bytes.toByteArray();
        String file = Base64.encodeToString(bb, Base64.DEFAULT);
        imageView.setImageBitmap(thumbnail);
        
        uploadToFirebase(bb);
    }

    private void uploadToFirebase(byte[] bb) {

        StorageReference sr = myStorage.child("myimages/a.jpg");
        sr.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Face_Scan.this,"Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Face_Scan.this,"Upload Failed 👎", Toast.LENGTH_SHORT).show();
            }
        });
    }


}