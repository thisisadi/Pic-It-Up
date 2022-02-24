package com.example.photoeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.photoeditor.databinding.ActivityResultBinding;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity {
    String result;
    Uri fileUri,resultUri,freeHand;
    ActivityResultBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (getIntent().getData() == null && getIntent().getExtras() == null){
            Intent intent = new Intent(ResultActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        else{
            if (getIntent().getExtras()!=null){
                if (getIntent().getStringExtra("DATA")!=null)
                {
                    result = getIntent().getStringExtra("DATA");
                    fileUri = Uri.parse(result);
                    String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

                    UCrop.Options options = new UCrop.Options();
                    options.setToolbarTitle("Pic-It-Up");
                    options.setFreeStyleCropEnabled(true);
                    options.setStatusBarColor(getResources().getColor(R.color.brown));

                    UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                            .withOptions(options)
                            .withAspectRatio(0, 0)
                            .useSourceImageAspectRatio()
                            .withMaxResultSize(2000, 2000)
                            .start(ResultActivity.this);

                }
                else {
                    freeHand = Uri.parse(getIntent().getStringExtra("freeHand"));
                    binding.image.setImageURI(freeHand);
                    Toast.makeText(ResultActivity.this, "Image Saved to Gallery!", Toast.LENGTH_SHORT).show();
                }

            }
            else{
                binding.image.setImageURI(getIntent().getData());
                Toast.makeText(ResultActivity.this, "Image Saved to Gallery!", Toast.LENGTH_SHORT).show();
            }
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (resultUri != null){
                    try {
                        File file = new File(resultUri.getPath());
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        Uri uri = FileProvider.getUriForFile(ResultActivity.this,getApplicationContext().getPackageName()+".provider",file);
                        intent.setDataAndType(uri,"image/*");
                        intent.putExtra(Intent.EXTRA_STREAM,uri);
                        startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(ResultActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else if (freeHand!= null){
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(ResultActivity.this.getContentResolver(), freeHand);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
                        Uri uri = Uri.parse(path);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.setType("image/*");
                        startActivity(Intent.createChooser(intent, "Share image via..."));
                        ResultActivity.this.getContentResolver().delete(freeHand,null,null);

                    } catch (Exception e) {
                        Toast.makeText(ResultActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("*/*");
                    share.putExtra(Intent.EXTRA_STREAM, getIntent().getData());
                    startActivity(Intent.createChooser(share, "Share image via..."));
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            resultUri = UCrop.getOutput(data);
            binding.image.setImageURI(resultUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                MediaStore.Images.Media.insertImage(ResultActivity.this.getContentResolver(), bitmap, "CroppedImage", "Cropped");
                Toast.makeText(ResultActivity.this, "Image Saved to Gallery!", Toast.LENGTH_SHORT).show();

            }
            catch (Exception e){
                Toast.makeText(ResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Intent intent = new Intent(ResultActivity.this,MainActivity.class);
            startActivity(intent);
            finishAffinity();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResultActivity.this,MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

}