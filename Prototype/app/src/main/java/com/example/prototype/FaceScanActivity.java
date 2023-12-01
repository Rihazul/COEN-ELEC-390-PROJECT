package com.example.prototype;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class FaceScanActivity extends BaseActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference myStorage;
    private DatabaseReference mDatabase;
    private ImageView imageView;
    private Button captureButton;
    private ImageButton backButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);
        Objects.requireNonNull(getSupportActionBar()).hide();

        myStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("captured_image");

        imageView = findViewById(R.id.image_display);
        captureButton = findViewById(R.id.scan_pic);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });


        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            onCaptureResult(data);
        }

    }

    private void onCaptureResult(Intent data) {
        int targetHeight = imageView.getHeight();
        int targetWidth = imageView.getWidth();
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        thumbnail = resizeBitmap(thumbnail, targetWidth, targetHeight);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        byte[] bb = bytes.toByteArray();
        String file = Base64.encodeToString(bb, Base64.DEFAULT);
        imageView.setImageBitmap(thumbnail);

        uploadToFirebase(bb);
    }

    private void uploadToFirebase(byte[] bb) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String ownerUID = currentUser.getUid();
        String key = databaseReference.child("images").push().getKey();
        //StorageReference sr = myStorage.child("images").child(key+"_image"+".jpg");
        StorageReference sr = myStorage.child("images").child(ownerUID + ".jpg");
        sr.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(FaceScanActivity.this, (R.string.image_uploaded_successfully), Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FaceScanActivity.this, (R.string.upload_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap resizeBitmap(Bitmap originalBitmap, int targetWidth, int targetHeight) {
        // Calculate the scale factor
        float scaleWidth = ((float) targetWidth) / originalBitmap.getWidth();
        float scaleHeight = ((float) targetHeight) / originalBitmap.getHeight();

        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // Resize the bitmap
        return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}