package agr.dvara.speedtest.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import agr.dvara.speedtest.R;
import agr.dvara.speedtest.adapter.ResultAdapter;

import static agr.dvara.speedtest.utils.Utils.getDbRef;
import static agr.dvara.speedtest.utils.Utils.getStr;

public class SearchResult extends AppCompatActivity {

    EditText mNo;
    TextView upload, download, time;
    CheckBox showAll;
    RecyclerView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mNo = findViewById(R.id.mobileEt);
        upload = findViewById(R.id.upload);
        download = findViewById(R.id.download);
        time = findViewById(R.id.timeTv);
        showAll = findViewById(R.id.showAllCB);
        result = findViewById(R.id.showResult);
        result.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @SuppressWarnings("unchecked")
    public void print(View view) {
        if (getStr(mNo).length() != 10) {
            Toast.makeText(this, "Please enter correct Mobile number.", Toast.LENGTH_LONG).show();
            return;
        }
        getDbRef().child(getStr(mNo).substring(getStr(mNo).length() - 10)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                HashMap<String, HashMap<String, String>> list = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                if (list == null || list.size() < 1) {
                    Toast.makeText(SearchResult.this, String.format("%s not available. \nPlease try another mobile number.", getStr(mNo)), Toast.LENGTH_LONG).show();
                    findViewById(R.id.cvDisplay).setVisibility(View.GONE);
                    findViewById(R.id.nrf).setVisibility(View.VISIBLE);
                    result.setVisibility(View.GONE);
                    return;
                }
                ArrayList<Date> keys = new ArrayList<>();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss", Locale.ENGLISH);
                for (String x : list.keySet()) {
                    try {
                        keys.add(df.parse(x));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(keys, Collections.reverseOrder());
                String timeStr = df.format(keys.get(0));
                if (list.get(timeStr) == null) {
                    findViewById(R.id.cvDisplay).setVisibility(View.GONE);
                    findViewById(R.id.nrf).setVisibility(View.VISIBLE);
                    result.setVisibility(View.GONE);
                    return;
                }
                findViewById(R.id.nrf).setVisibility(View.GONE);
                if (showAll.isChecked()) {
                    result.setVisibility(View.VISIBLE);
                    findViewById(R.id.cvDisplay).setVisibility(View.GONE);
                    result.setAdapter(new ResultAdapter(SearchResult.this, list, keys));
                } else {
                    result.setVisibility(View.GONE);
                    findViewById(R.id.cvDisplay).setVisibility(View.VISIBLE);
                    time.setText(String.format("Submit time: %s", timeStr.substring(0, timeStr.length() - 4)));
                    download.setText(String.format("Download speed: %s", Objects.requireNonNull(list.get(timeStr)).get("download")));
                    upload.setText(String.format("Upload speed: %s", Objects.requireNonNull(list.get(timeStr)).get("upload")));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
    }
}