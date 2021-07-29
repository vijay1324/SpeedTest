package agr.dvara.speedtest.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class NetworkConnectivity {
    public static boolean networkAvailable(Context mContext) {
        NetworkInfo netInfo;
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return netInfo != null && netInfo.isConnected();
    }

    @SuppressLint("SwitchIntDef")
    public static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = mTelephonyManager.getSimOperatorName();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (NetworkConnectionType(context).startsWith("WIFI"))
            return NetworkConnectionType(context);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return String.format("%s - %s 2G", NetworkConnectionType(context), operator);
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return String.format("%s - %s 3G", NetworkConnectionType(context), operator);
            case TelephonyManager.NETWORK_TYPE_LTE:
                return String.format("%s - %s 4G", NetworkConnectionType(context), operator);
            default:
                //return "Unknown";
                return NetworkConnectionType(context);
        }
    }

    private static String NetworkConnectionType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    @SuppressLint("WifiManagerPotentialLeak") WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifiManager.getConnectionInfo();
                    return "WIFI " + info.getSSID();
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    return "MOBILE DATA";
        }
        return "UNKNOWN";
    }
}