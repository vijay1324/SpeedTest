package agr.dvara.speedtest.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.internet_speed_testing.InternetSpeedBuilder;
import com.example.internet_speed_testing.ProgressionModel;
import com.github.anastr.speedviewlib.SpeedView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import agr.dvara.speedtest.R;
import agr.dvara.speedtest.utils.NetworkConnectivity;

import static agr.dvara.speedtest.utils.Utils.getDbRef;
import static agr.dvara.speedtest.utils.Utils.getSQLDateFormat;
import static agr.dvara.speedtest.utils.Utils.getStr;

public class SpeedTest extends AppCompatActivity {

    SpeedView speedView;
    TextView downloadSpeed, uploadSpeed, avgSpeed, netStatus, netType;
    Button test;
    private static String mobileNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
        speedView = findViewById(R.id.speedViewIndicator);
        downloadSpeed = findViewById(R.id.download);
        uploadSpeed = findViewById(R.id.upload);
        avgSpeed = findViewById(R.id.avg_speed);
        test = findViewById(R.id.btnTest);
        netStatus = findViewById(R.id.internetStatus);
        netType = findViewById(R.id.ConnectionType);
        checkNetwork(null);
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public void checkNetwork(View view) {
        test.setEnabled(false);
        if (!NetworkConnectivity.networkAvailable(this)) {
            speedView.setVisibility(View.GONE);
            netType.setVisibility(View.GONE);
            downloadSpeed.setVisibility(View.GONE);
            uploadSpeed.setVisibility(View.GONE);
            avgSpeed.setVisibility(View.GONE);
            netStatus.setVisibility(View.VISIBLE);
            findViewById(R.id.llShowSpeed).setVisibility(View.GONE);
            return;
        }
        speedView.setVisibility(View.VISIBLE);
        netType.setVisibility(View.VISIBLE);
        downloadSpeed.setVisibility(View.VISIBLE);
        uploadSpeed.setVisibility(View.VISIBLE);
        avgSpeed.setVisibility(View.VISIBLE);
        netStatus.setVisibility(View.GONE);
        findViewById(R.id.llShowSpeed).setVisibility(View.VISIBLE);
        netType.setText(NetworkConnectivity.getNetworkClass(this));
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mobileNo = tMgr.getLine1Number();

        InternetSpeedBuilder builder = new InternetSpeedBuilder(SpeedTest.this);
        builder.setOnEventInternetSpeedListener(new InternetSpeedBuilder.OnEventInternetSpeedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadProgress(int count, @NotNull final ProgressionModel progressModel) {
                java.math.BigDecimal bd = progressModel.getDownloadSpeed();

                final double d = bd.doubleValue();
                runOnUiThread(() -> {
                    downloadSpeed.setText(getValueWithUnit(d));
                    speedView.setUnit(getUnitValue(d));
                    speedView.speedTo(getSpeedValue(d));
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onUploadProgress(int count, @NotNull final ProgressionModel progressModel) {
                java.math.BigDecimal bd = progressModel.getUploadSpeed();

                final double d = bd.doubleValue();
                runOnUiThread(() -> {
                    uploadSpeed.setText(getValueWithUnit(d));
                    speedView.setUnit(getUnitValue(d));
                    speedView.speedTo(getSpeedValue(d));
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTotalProgress(int count, @NotNull final ProgressionModel progressModel) {
                java.math.BigDecimal downloadDecimal = progressModel.getDownloadSpeed();
                final double downloadFinal = downloadDecimal.doubleValue();

                java.math.BigDecimal uploadDecimal = progressModel.getUploadSpeed();
                final double uploadFinal = uploadDecimal.doubleValue();
                final double avgSpeedCount = (downloadFinal + uploadFinal) / 2;
                runOnUiThread(() -> {
                    if (progressModel.getProgressTotal() == 100f) {
                        avgSpeed.setText("Avg Speed: " + getValueWithUnit(avgSpeedCount));
                        speedView.speedTo(0);
                        test.setEnabled(true);
                    }
                });
            }
        });
        test.setOnClickListener(v -> {
            test.setEnabled(false);
            uploadToFirebase();
        });
        builder.start("http://ipv4.ikoula.testdebit.info/1M.iso", 1);
    }

    public static String getUnitValue(double size) {

        String hrSize;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        if (t > 1) {
            hrSize = " tb/s";
        } else if (g > 1) {
            hrSize = " gb/p";
        } else if (m > 1) {
            hrSize = " mb/s";
        } else if (k > 1) {
            hrSize = " kb/s";
        } else {
            hrSize = " b/s";
        }

        return hrSize;
    }

    public static Float getSpeedValue(double size) {

        String hrSize;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t);
        } else if (g > 1) {
            hrSize = dec.format(g);
        } else if (m > 1) {
            hrSize = dec.format(m);
        } else if (k > 1) {
            hrSize = dec.format(k);
        } else {
            hrSize = dec.format(size);
        }

        return Float.valueOf(hrSize);
    }

    public static String getValueWithUnit(double size) {

        String hrSize;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" tb/p");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" gb/s");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" mb/s");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" kb/s");
        } else {
            hrSize = dec.format(size);
        }
        return hrSize;
    }

    private void uploadToFirebase() {
        Map<String, String> map = new HashMap<>();
        map.put("download", getStr(downloadSpeed));
        map.put("upload", getStr(uploadSpeed));
        map.put("avg", getStr(avgSpeed));
        getDbRef().child(mobileNo.substring(mobileNo.length() - 10)).child(getSQLDateFormat()).setValue(map);
        Toast.makeText(getApplicationContext(), "Submitted Successfully", Toast.LENGTH_LONG).show();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetwork(null);
        }
    };

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchResult.class));
        }
        return super.onOptionsItemSelected(item);
    }
}