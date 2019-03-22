package in.kesari.chromakey;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relativeLay;
    CameraView cameraView;
    FancyButton clickPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Box box = new Box(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, 0, 0, 0);

        //addContentView(box, layoutParams);

        relativeLay = (RelativeLayout) findViewById(R.id.relativeLay);
        clickPic = (FancyButton) findViewById(R.id.clickPic);

        //relativeLay.addView(box,layoutParams);

        cameraView = (CameraView) findViewById(R.id.CameraView);
        //cameraView.addView(box,layoutParams);

        clickPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.progressdialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                cameraView.setFlash(CameraKit.Constants.FLASH_ON);

                cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                    @Override
                    public void callback(CameraKitImage image) {

                        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        MediaActionSound mSound = new MediaActionSound();
                        mSound.play(MediaActionSound.SHUTTER_CLICK);
                        cameraView.setFlash(CameraKit.Constants.FLASH_OFF);

                        Bitmap bitmap = image.getBitmap();

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        String imagePath = timeStamp +".jpg";

                        File sd = Environment.getExternalStorageDirectory();
                        File dest = new File(sd, imagePath);

                        try {
                            FileOutputStream out = new FileOutputStream(dest);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,PreviewImageActivity.class);
                        intent.putExtra("ImagePath",dest.getPath());
                        startActivity(intent);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }


}
