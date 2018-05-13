package vn.letmewash.android.libs;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by camel on 12/4/17.
 */

public class HTTPHelper {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 120000; // 2 minutes

    public static String makeHTTPRequest(String method, String url, JSONObject params, String apiKey) {
        HttpURLConnection conn;
        OutputStreamWriter outputStream;
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader;
        String line;
        Long startTime = Calendar.getInstance().getTimeInMillis();
        String responseText = "";
        try {
            method = method.toUpperCase();
            if (method.equals("GET")) {
                // GET
                if (params == null) {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                } else {
                    url += "?" + getRequestParamsFromJson(params);
                    conn = (HttpURLConnection) new URL(url).openConnection();
                }
            } else {
                // POST
                conn = (HttpURLConnection) new URL(url).openConnection();
            }

            conn.setRequestMethod(method);
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT); // 2 minutes

            // Set basic authorization key
            if (apiKey != null) {
                conn.setRequestProperty("Authorization", "Basic " + apiKey);
            }

            conn.connect();
            if (params != null && conn.getRequestMethod().equals("POST")) {
                outputStream = new OutputStreamWriter(conn.getOutputStream());
                outputStream.write(params.toString());
                outputStream.flush();
                outputStream.close();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            responseText = sb.toString();
            return responseText;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            String logText = "Request: " + url + " finised in ";
            Long duration = Calendar.getInstance().getTimeInMillis() - startTime;
            logText += duration > 1000 ?
                    String.valueOf(duration / 1000) + " secoond(s)" :
                    String.valueOf(duration) + " millisecond(s)";
            Log.d("DBG", logText);
            Log.d("DBG", responseText);
        }
        return null;
    }

    public static String getRequestParamsFromJson(JSONObject params) {

        String urlParams = "";
        try {
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.getString(key);
                if (urlParams.equals("")) {
                    urlParams += key + "=" + URLEncoder.encode(value, "UTF-8");
                } else {
                    urlParams += "&" + key + "=" + URLEncoder.encode(value, "UTF-8");
                }
            }
            return urlParams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
