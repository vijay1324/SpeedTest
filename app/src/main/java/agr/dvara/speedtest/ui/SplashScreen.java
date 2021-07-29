package agr.dvara.speedtest.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import agr.dvara.speedtest.R;

public class SplashScreen extends AppCompatActivity {

    Context mContext = this;
    private static final int REQUEST = 112;
    static boolean permissionGrandCheck = true;
    static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getAllPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            permissionGrandCheck = true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    permissionGrandCheck = false;
                    break;
                }
            }
            if (permissionGrandCheck) {
                Log.d(TAG, "@@@ PERMISSIONS grant");
                gotoNextPage();
            } else {
                Log.d(TAG, "@@@ PERMISSIONS Denied");
                final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                builder.setCancelable(false);
                builder.setTitle("PERMISSIONS Denied");
                builder.setMessage("If you do not give me permission, this app will not work");
                builder.setPositiveButton("Allow Permissions", (dialog, id) -> getAllPermission())
                        .setNeutralButton("Open in Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Exit", (dialog, which) -> SplashScreen.this.finish());

                // Create the AlertDialog object and return it
                builder.create().show();
            }
        }
    }

    private void getAllPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d(TAG, "@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE
            };

            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d(TAG, "@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);
            } else {
                Log.d(TAG, "@@@ IN ELSE hasPermissions");
                gotoNextPage();
            }
        } else {
            Log.d(TAG, "@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            gotoNextPage();
        }
    }

    private void gotoNextPage() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, SpeedTest.class));
            SplashScreen.this.finish();
        }, 2000);
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}