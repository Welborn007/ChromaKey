package in.kesari.chromakey;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class PermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        checkAndRequestPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public static boolean permStatus = false;
    public void checkAndRequestPermissions(final Activity activity, String... permissions)
    {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(HomeScreen.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                try
                {
                    permStatus = true;
                    Intent intent = new Intent(PermissionsActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                permStatus = false;
            }
        };

        TedPermission.with(activity)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(permissions)
                .check();

        //return permStatus;
    }
}
