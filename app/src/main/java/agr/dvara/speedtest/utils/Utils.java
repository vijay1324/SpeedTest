package agr.dvara.speedtest.utils;

import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static String getStr(TextView tv) {
        if (tv == null)
            return "";
        return tv.getText().toString().trim();
    }

    public static String getStr(EditText tv) {
        if (tv == null)
            return "";
        return tv.getText().toString().trim();
    }

    public static String getSQLDateFormat() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss", Locale.ENGLISH);
        return df.format(Calendar.getInstance().getTime());
    }

    public static DatabaseReference getDbRef() {
        return FirebaseDatabase.getInstance().getReference("NetworkHistory");
    }
}
