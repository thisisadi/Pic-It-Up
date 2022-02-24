package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import com.example.photoeditor.databinding.ActivityCropBinding;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class CropActivity extends AppCompatActivity {
    Bitmap mBitmap;
    Uri uri;
    ActivityCropBinding binding;
    private SomeView mSomeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        uri= Uri.parse(extras.getString("imageUri"));
        try
        {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            mSomeView = findViewById(R.id.drawing_view);
            mSomeView.mContext = CropActivity.this;
            mSomeView.bitmap = mBitmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        binding.freeHandHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FreeHandGuidelinesFragment fragment = new FreeHandGuidelinesFragment();
                fragment.show(getSupportFragmentManager(),fragment.getTag());
            }
        });
    }

    public Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public void cropImage() {

        Bitmap fullScreenBitmap =
                Bitmap.createBitmap(mSomeView.getWidth(), mSomeView.getHeight(), mBitmap.getConfig());

        Canvas canvas = new Canvas(fullScreenBitmap);

        Path path = new Path();
        List<Point> points = mSomeView.getPoints();
        for (int i = 0; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }

        // Cut out the selected portion of the image...
        Paint paint = new Paint();
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        // Frame the cut out portion...
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        canvas.drawPath(path, paint);

        // Create a bitmap with just the cropped area.
        Region region = new Region();
        Region clip = new Region(0, 0, fullScreenBitmap.getWidth(), fullScreenBitmap.getHeight());
        region.setPath(path, clip);
        Rect bounds = region.getBounds();
        Bitmap croppedBitmap =
                Bitmap.createBitmap(fullScreenBitmap, bounds.left, bounds.top,
                        bounds.width(), bounds.height());

        Intent intent = new Intent(CropActivity.this,ResultActivity.class);
        Uri uri = getUri(this,croppedBitmap);
        if (uri!=null) {
            intent.putExtra("freeHand", uri.toString());
            startActivityForResult(intent, 500);
            finishAffinity();
        }
        else{
            mSomeView.resetView();
        }
    }

}