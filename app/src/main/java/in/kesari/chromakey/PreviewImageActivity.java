package in.kesari.chromakey;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PreviewImageActivity extends AppCompatActivity {

    PhotoView setImage;
    FFmpeg ffmpeg;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private final static int FILE_REQUEST_CODE = 1;
    String backgroundPath,displayName,clickedImagePath;
    CustomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private  RecyclerView recyclerView;
    int[] myImageList = new int[]{R.drawable.blue, R.drawable.brown,R.drawable.dark_blue, R.drawable.grey,R.drawable.maroon,R.drawable.pink,R.drawable.purple,R.drawable.red,R.drawable.voilet,R.drawable.white};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CustomAdapter(myImageList,PreviewImageActivity.this);
        recyclerView.setAdapter(adapter);

        setImage = (PhotoView) findViewById(R.id.setImage);

        clickedImagePath = getIntent().getStringExtra("ImagePath");

        File imgFile = new File(clickedImagePath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            setImage.setImageBitmap(myBitmap);
        }

        recyclerView.addOnItemTouchListener(
                new SubItemRecyclerListener(getApplicationContext(), new SubItemRecyclerListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Bitmap bitmap = BitmapFactory.decodeResource( getResources(), myImageList[position]);

                        File sd = Environment.getExternalStorageDirectory();
                        File dest = new File(sd, getResources().getResourceEntryName(myImageList[position]) + ".jpg");

                        if(!dest.exists())
                        {
                            try {
                                FileOutputStream out = new FileOutputStream(dest);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        backgroundPath = dest.getPath();
                        initialize(PreviewImageActivity.this,backgroundPath);

                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        try {

            if (id == android.R.id.home) {
                finish();
            }
            else if(id == R.id.menuImage)
            {
                mediaFiles.clear();
                Intent intent = new Intent(PreviewImageActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setSelectedMediaFiles(mediaFiles)
                        .setShowFiles(false)
                        .enableImageCapture(false)
                        .setShowImages(true)
                        .setShowVideos(false)
                        //.setSingleChoiceMode(true)
                        .setMaxSelection(1)
                        //.setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                        .build());
                startActivityForResult(intent, FILE_REQUEST_CODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mediaFiles.clear();
            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

            MediaFile mediaFile = mediaFiles.get(0);
            backgroundPath = mediaFile.getPath();
            displayName = mediaFile.getName();

            initialize(PreviewImageActivity.this,backgroundPath);
        }
    }


    public void initialize(final Activity context, final String backgroundPath) {
        try {
            ffmpeg = FFmpeg.getInstance(context);
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                        Log.i("FFMPEG","initialize onStart");
                    }

                    @Override
                    public void onFailure() {
                        Log.i("FFMPEG","initialize onFailure");
                    }

                    @Override
                    public void onSuccess() {
                        // FFmpeg is supported by device
                        Log.i("FFMPEG","initialize Success");
                        try {
                            Log.i("FFMPEG",ffmpeg.getDeviceFFmpegVersion() + " " + ffmpeg.getLibraryFFmpegVersion());
                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }

                        //executeCmd("-y -i /storage/emulated/0/Spin_1.mp4 -vf chromakey=0x49FB00:0.1:0.2 -c copy -c:v png /storage/emulated/0/outputVideo.mp4",context);

                        File imgFile = new File(backgroundPath);
                        Bitmap myBitmap = null;
                        if(imgFile.exists()){
                            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            Log.i("ClickedImageHeight", String.valueOf(myBitmap.getHeight()));
                            executeScalingCmd("-y -i " + clickedImagePath +" -vf scale=-1:" + String.valueOf(myBitmap.getHeight()) + " /storage/emulated/0/" + "ScaledOutput.jpg",context);
                        }

                    }

                    @Override
                    public void onFinish() {
                        Log.i("FFMPEG","initialize onFinish");
                    }
                });
            } catch (FFmpegNotSupportedException e) {
                // Handle if FFmpeg is not supported by device
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle if FFmpeg is not supported by device
        }
    }

    public void executeScalingCmd(final String command, final Activity activity) {
        try {
            String[] cmd = command.split(" ");

            FFmpeg ffmpeg = FFmpeg.getInstance(activity);

            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.i("FFMPEG","executeCmd onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.i("FFMPEG","executeCmd onProgress " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.i("FFMPEG","executeCmd onFailure " + message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.i("FFMPEG","executeCmd onSuccess " + message);

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String imagePath = timeStamp +".jpg";

                    File sd = Environment.getExternalStorageDirectory();
                    File dest = new File(sd, imagePath);

                    executeChromaKeyCmd("-y -i " + backgroundPath+ " -i /storage/emulated/0/ScaledOutput.jpg -filter_complex [1:v]chromakey=green:0.13:0.2[ckout];[0:v][ckout]overlay[o] -map [o] -map 1:a? -c:a copy " + dest.getPath(),activity,dest.getPath());
                }

                @Override
                public void onFinish() {
                    Log.i("FFMPEG","executeCmd onFinish");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            //There is a command already running
        }
    }

    public void executeChromaKeyCmd(final String command, Activity activity, final String OutputImagePath) {
        try {
            String[] cmd = command.split(" ");

            FFmpeg ffmpeg = FFmpeg.getInstance(activity);

            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.i("FFMPEG","executeCmd onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.i("FFMPEG","executeCmd onProgress " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.i("FFMPEG","executeCmd onFailure " + message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.i("FFMPEG","executeCmd onSuccess " + message);

                    File imgFile = new File(OutputImagePath);
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        setImage.setImageBitmap(myBitmap);
                    }
                }

                @Override
                public void onFinish() {
                    Log.i("FFMPEG","executeCmd onFinish");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            //There is a command already running
        }
    }
}