package com.jxhobo.http.dataservice.http;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import org.apache.http.HttpHost;

public class NetworkInfoHelper {
    private ConnectivityManager connectivityManager;
    private Context context;

    public NetworkInfoHelper(Context context) {
        this.context = context;
    }

    protected ConnectivityManager connectivityManager() {
        if (this.connectivityManager == null) ;
        try {
            this.connectivityManager = ((ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE));
        } catch (Exception localException) {
            Log.w("network", "cannot get connectivity manager, maybe the permission is missing in AndroidManifest.xml?", localException);
        }
        return this.connectivityManager;
    }

    public String getNetworkInfo() {
        String res;
        ConnectivityManager connectivityManager = connectivityManager();
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null)
                switch (networkInfo.getType()) {

                    case 0:
                        res = "mobile(" + networkInfo.getSubtypeName() + "," + networkInfo.getExtraInfo() + ")";
                        break;
                    case 1:
                        res = "wifi";
                        break;
                    default:
                        res = networkInfo.getTypeName();
                        break;
                }
            else
                res = "unknown";
        } else {
            res = "unknown";
        }
        return res;
    }

    public HttpHost getProxy() {
        ConnectivityManager manager = connectivityManager();
        if (manager == null) return null;
        HttpHost host = null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        String hostName = null;
        if (networkInfo == null) {
            hostName = null;
        } else if (networkInfo.getType() == 1) {
            hostName = null;
        } else if (networkInfo.getType() == 0) {
            hostName = networkInfo.getExtraInfo();
        }
        if (hostName != null) {
            String s = hostName.toLowerCase();
            if (s.contains("cmnet")) {
                host = null;
            } else if (s.contains("cmwap")) {
                host = new HttpHost("10.0.0.172");
            } else if (s.contains("3gnet")) {
                host = null;
            } else if (s.contains("3gwap")) {
                host = new HttpHost("10.0.0.172");
            } else if (s.contains("uninet")) {
                host = null;
            } else if (s.contains("uniwap")) {
                host = new HttpHost("10.0.0.172");
            } else if (s.contains("ctnet")) {
                host = null;
            } else if (s.contains("ctwap")) {
                host = new HttpHost("10.0.0.200");
            }
            if (s.contains("#777")) {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://telephony/carriers/preferapn");
                String fields[] = new String[]{"proxy", "port"};
                Cursor cursor = resolver.query(uri, fields, null, null, null);
                if (cursor.moveToFirst() && cursor.getString(0).length() > 3) {
                    int port = Integer.parseInt(cursor.getString(1));
                    if (port <= 0) {
                        port = 80;
                    }
                    host = new HttpHost(cursor.getString(0), port);
                }
            }
        }
        return host;
    }
}
