package com.example.travel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionLatLng;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String currentImagePath;
    ImageView capturedImage;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
//        Monument m = new Monument("Ajoba Temple", "Ajoba Temple- Located at the entrance of Keri (or Querim) beach in Goa. It is a small temple but quite interesting on the sands of the beach. Keri beach is the northernmost beach of Goa. Thereafter Goa ends and Maharastra starts.","15.7077","73.6942","0");
//        HashMap<String, String> monuments = new HashMap<>();
//        monuments = m.toMap();
//        //DatabaseReference ref = mDatabase.child("Monuments").push();
//        DatabaseReference ref = mDatabase.child("Monuments").child("Amboja_Temple").push();
//        ref.setValue(monuments);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                System.out.println("Hellow");;
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.i("loadPost:onCancelled", databaseError.toException().toString());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

    public boolean react(Bitmap bitmap)
    {
        FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("manifest.json")
                .build();
        FirebaseVisionImageLabeler labeler;
        try {
            FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                    new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .build();
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
            //FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);

            //Bitmap bits = BitmapFactory.decodeResource(getResources(),R.drawable.pandava_caves);

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            for (FirebaseVisionImageLabel label: labels) {
                                String text = label.getText();
                                float confidence = label.getConfidence();
                                Log.i(text,Float.toString(confidence));
                                TextView placeName = (TextView)findViewById(R.id.place);
                                placeName.setText(text.toString());
                                Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                                intent.putExtra("Monument name",text);
                                System.out.println("Moving to another activity");
                                startActivity(intent);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Failure","Failure");
                        }
                    });
        } catch (FirebaseMLException e) {
            // ...
        }
//        TextView placeName = (TextView)findViewById(R.id.place);
//        String ex = placeName.getText().toString();
//        System.out.println("Here it comes");
//        if(ex.equals("Please Try Again"))
//        {
//            System.out.println("Returning False");
//            return false;
//        }
//        System.out.println("Returning True");
        return false;
    }

    public void capture(View view)
    {
        Log.i("Here","Here");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager())!=null)
        {
            File imageFile = null;
            try
            {
                imageFile = getImageFile();
            } catch(IOException e)
            {
                e.printStackTrace();
            }
            if(imageFile != null)
            {
                Uri imageUri = FileProvider.getUriForFile(this,"com.example.android.fileProvider",imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                Log.i("Here ","There");
                startActivityForResult(cameraIntent,101);
            }
        }
    }

    private  File getImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Here ","kk");
        if (requestCode == 101 && resultCode == RESULT_OK) {
            capturedImage = findViewById(R.id.image);
            TextView placeName = (TextView)findViewById(R.id.place);
            placeName.setText("Please Try Again");
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
            Bitmap rotatedBitmap = null;
            try {
                ExifInterface ei = new ExifInterface(currentImagePath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                        break;
                    default:
                        rotatedBitmap = bitmap;
                }
            }catch (Exception e){}
            capturedImage.setImageBitmap(rotatedBitmap);
            if(react(rotatedBitmap))
            {
                String ex = placeName.getText().toString();
                Intent intent = new Intent(this,InfoActivity.class);
                intent.putExtra("Monument name",ex);
                startActivity(intent);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
