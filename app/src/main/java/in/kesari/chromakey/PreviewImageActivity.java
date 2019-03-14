package in.kesari.chromakey;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
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
    LinearLayout imageHolders;
    ImageView originalImage,cropImage1,cropImage2;
    int[] myImageList = new int[]{R.drawable.blue, R.drawable.brown,R.drawable.dark_blue, R.drawable.grey,R.drawable.maroon,R.drawable.pink,R.drawable.purple,R.drawable.red,R.drawable.voilet,R.drawable.white};
    Bitmap myBitmap;
    boolean crop1Click,crop2Click;
    Uri resultUri1,resultUri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        FirebaseApp.initializeApp(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        imageHolders = (LinearLayout) findViewById(R.id.imageHolders);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CustomAdapter(myImageList,PreviewImageActivity.this);
        recyclerView.setAdapter(adapter);

        setImage = (PhotoView) findViewById(R.id.setImage);
        originalImage = (ImageView) findViewById(R.id.originalImage);
        cropImage1 = (ImageView) findViewById(R.id.cropImage1);
        cropImage2 = (ImageView) findViewById(R.id.cropImage2);

        clickedImagePath = getIntent().getStringExtra("ImagePath");

        File imgFile = new File(clickedImagePath);

        if(imgFile.exists()){
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            setImage.setImageBitmap(myBitmap);
            originalImage.setImageBitmap(myBitmap);
        }

        originalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage.setImageBitmap(myBitmap);
                clickedImagePath = getIntent().getStringExtra("ImagePath");
            }
        });

        cropImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(resultUri1 != null)
                {
                    File file = new File(resultUri1.getPath());
                    clickedImagePath = file.getPath();
                    setImage.setImageURI(resultUri1);
                }

                crop1Click = true;
                crop2Click= false;
                CropImage.activity(Uri.fromFile(new File(getIntent().getStringExtra("ImagePath")))).setCropShape(CropImageView.CropShape.OVAL)
                        .start(PreviewImageActivity.this);
            }
        });

        cropImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(resultUri2 != null)
                {
                    File file = new File(resultUri2.getPath());
                    clickedImagePath = file.getPath();
                    setImage.setImageURI(resultUri2);
                }

                crop1Click = false;
                crop2Click= true;
                CropImage.activity(Uri.fromFile(new File(getIntent().getStringExtra("ImagePath"))))
                        .start(PreviewImageActivity.this);
            }
        });

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
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
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

        recognizeFace();


    }

    public void recognizeFace()
    {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(myBitmap);

        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();


        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts);

        final Dialog dialog = new Dialog(PreviewImageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // ...

                                        for (FirebaseVisionFace face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                            Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                                            Canvas tempCanvas = new Canvas(tempBitmap);
                                            tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                                            Paint myRectPaint = new Paint();
                                            myRectPaint.setStrokeWidth(5);
                                            myRectPaint.setColor(Color.RED);
                                            myRectPaint.setStyle(Paint.Style.STROKE);

                                            tempCanvas.drawRoundRect(new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom), 2, 2, myRectPaint);
                                            setImage.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

                                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                            // nose available):
                                            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                            if (leftEar != null) {
                                                FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                            }

                                            // If contour detection was enabled:
                                            List<FirebaseVisionPoint> leftEyeContour =
                                                    face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                            List<FirebaseVisionPoint> upperLipBottomContour =
                                                    face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float smileProb = face.getSmilingProbability();
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                            }

                                            // If face tracking was enabled:
                                            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                int id = face.getTrackingId();
                                            }
                                        }

                                        dialog.dismiss();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        dialog.dismiss();
                                    }
                                });
    }

   /* public void recognizeFace()
    {
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if(!faceDetector.isOperational()){
            new AlertDialog.Builder(PreviewImageActivity.this).setMessage("Could not set up the face detector!").show();
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
        }
        setImage.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
    }*/

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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                if(crop1Click)
                {

                    try {
                        resultUri1 = result.getUri();
                        File file = new File(resultUri1.getPath());
                        cropImage1.setImageURI(resultUri1);
                        clickedImagePath = file.getPath();
                        setImage.setImageURI(resultUri1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else if(crop2Click)
                {
                    try {
                        resultUri2 = result.getUri();
                        File file = new File(resultUri2.getPath());
                        cropImage2.setImageURI(resultUri2);
                        clickedImagePath = file.getPath();
                        setImage.setImageURI(resultUri2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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

                    executeChromaKeyCmd("-y -i " + backgroundPath+ " -i /storage/emulated/0/ScaledOutput.jpg -filter_complex [1:v]chromakey=green:0.13:0.2[ckout];[0:v][ckout]overlay=(W-w)/2:(H-h)/2[o] -map [o] -map 1:a? -c:a copy " + dest.getPath(),activity,dest.getPath());
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
