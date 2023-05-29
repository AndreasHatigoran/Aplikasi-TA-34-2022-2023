package com.example.testapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;


import com.example.testapp.ml.ModelUnquant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageView;
    TextView result;
    TextView resultDescription;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        result = findViewById(R.id.result);
       resultDescription = findViewById(R.id.resultDescription);
        imageView = findViewById(R.id.imageView);
        camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }
    public void classifyImage(Bitmap image){
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 224));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 224));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 224));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);
            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }


            String[] classes = {"Pisang","Mangga","Nanas","Jeruk","Kelapa","Manggis","Rambutan","Pepaya","Markisa","Salak","Jambu Biji","Kiwi","Alpukat","Semangka","Leci","Delima","Jeruk Nipis","Jagung"};
            String[] desc =
                    {"Pisang mengandung 85(kcal) kalori, 0.73(g) protein, 18(g) karbohidrat, 78.3(g) air, 9,7(mg) Vitamin C",
                    "Mangga mengandung 250(kcal) kalori, 0.36(g) protein, 6.97(g) karbohidrat, 60(g) air, 43(mg) Vitamin C",
                    "Nanas mengandung 60(kcal) kalori, 0.46(g) protein, 14.1(g) karbohidrat, 85(g) air, 2.5(mg) Vitamin C",
                            "Jeruk mengandung 0.14(kcal) kalori, 0.43(g) protein, 2(g) karbohidrat, 86.7(g) air, 0.068(mg) Vitamin C",
                            "Kelapa mengandung 354(kcal) kalori, 3.33(g) protein, 15.2(g) karbohidrat, 47(g) air, 3.3(mg) Vitamin C",
                            "Manggis mengandung 73(kcal) kalori, 0.41(g) protein, 17.9(g) karbohidrat, 80.9(g) air, 2.9(mg) Vitamin C",
                            "Rambutan mengandung 82(kcal) kalori, 0.65(g) protein, 20.9(g) karbohidrat, 78(g) air, 4.9(mg) Vitamin C",
                            "Pepaya mengandung 179(kcal) kalori, 0.39(g) protein, 0(g) karbohidrat, 43(g) air, 0(mg) Vitamin C",
                            "Markisa mengandung 51(kcal) kalori, 0.39(g) protein, 13.6(g) karbohidrat, 85.6(g) air, 29.8(mg) Vitamin C",
                            "Salak mengandung 51(kcal) kalori, 0.39(g) protein, 13.6(g) karbohidrat, 85.6(g) air, 29.8(mg) Vitamin C",
                            "Jambu Biji mengandung 68(kcal) kalori, 2.55(g) protein, 14.3(g) karbohidrat, 80.8(g) air, 228(mg) Vitamin C",
                            "Kiwi mengandung 0.17(kcal) kalori, 0.63(g) protein, 3(g) karbohidrat, 83.9(g) air, 0.027(mg) Vitamin C",
                            "Alpukat mengandung 670(kcal) kalori, 1.58(g) protein, 0.06(g) karbohidrat, 160(g) air, 81(mg) Vitamin C",
                            "Semangka mengandung 127(kcal) kalori, 0.25(g) protein, 1.21(g) karbohidrat, 30(g) air, 3(mg) Vitamin C",
                            "Leci mengandung 66(kcal) kalori, 0.83(g) protein, 16.5(g) karbohidrat, 81.8(g) air, 71.5(mg) Vitamin C",
                            "Delima mengandung 83(kcal) kalori, 1.67(g) protein, 18.7(g) karbohidrat, 77.9(g) air, 10.2(mg) Vitamin C",
                            "Jeruk Nipis mengandung 126(kcal) kalori, 0.3(g) protein, 33(g) karbohidrat, 30(g) air, 0(mg) Vitamin C",
                            "Jagung mengandung 364(kcal) kalori, 6.2(g) protein, 80.8(g) karbohidrat, 10.8(g) air, 0.13(mg) Vitamin C"};

            result.setText(classes[maxPos]);
            resultDescription.setText(desc[maxPos]);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }
//    private static List<Buah> getListBuah() {
//        List<Buah> listBuah = new ArrayList<Buah>();
//        listBuah.add(new Buah("Pisang", "Description Pisang"));
//
//        return listBuah;
//    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}