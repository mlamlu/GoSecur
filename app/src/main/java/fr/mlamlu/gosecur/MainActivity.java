package fr.mlamlu.gosecur;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

   // TextView sampleText;
  //  Button changeButton;
    ImageView carIdExemple;
    ImageView contentImage;
    Button takePhotoButton;
    Button FoureToutButton;
    static int REQUEST_CODE = 1664;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // sampleText = findViewById(R.id.sampleTexteView);

      //  changeButton = findViewById(R.id.buttonFarid);
        carIdExemple = findViewById(R.id.CardView);
        takePhotoButton = findViewById(R.id.buttonTakePhoto);
        contentImage = findViewById(R.id.imageView);
        FoureToutButton = findViewById(R.id.buttonFoureTout);



    /*    changeButton.setOnClickListener(qnew View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sampleText.getText().equals("Farid est un énorme enculé")){


                    sampleText.setText("Farid est un énorme enculé");
                }
                else {
                    sampleText.setText("Farid est un enculé");
                }
            }
        });*/


    }



/*
    public void changeActionButton(View v){
        sampleText.setText("La grosse Farid ");

        contentImage.setImageResource(R.drawable.levrette);
    }*/


    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    public void actionPhotoButton(View v){
        takePhoto(v);
    }


    public void takePhoto(View v){
       dispatchTakePictureIntent();
    }



    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
  /*      if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Bitmap imageBitmap = (Bitmap)data.getExtras().get("data");
            contentImage.setImageBitmap(imageBitmap);
        }*/
    if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){

        int targetW = contentImage.getWidth();
        int targetH = contentImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        contentImage.setImageBitmap(bitmap);

    }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void saveData(View saveDataButton){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
       /* DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World BITCH!");*/

     /*    myRef.push().setValue(new Personne("SuceBoul","Farid"));


        sampleText.setText("Fouuuure TOUUUT");*/

        getDataFromIDCard(saveDataButton);


    }


    public void getDataFromIDCard(View v){

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(((BitmapDrawable) getDrawable(R.drawable.cardid)).getBitmap());
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                for(FirebaseVisionText.TextBlock textBlock : firebaseVisionText.getTextBlocks()){
                    for( FirebaseVisionText.Line line : textBlock.getLines()){
                        Log.d("MLAMLU",line.getText());

                        if (line.getText().contains("Prenom") || line.getText().contains("Prénom")  ) {
                            String prenom = line.getText();

                            //str.indexOf("is", str.indexOf("is") + 1);
                            prenom = prenom.substring(prenom.indexOf(' '),prenom.length());
                           prenom = prenom.replaceAll("\\s","");
                            Log.d("MLAMLU Prenom",prenom);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}