package com.example.qmma.featuredetection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by qmma on 16/4/5.
 * Converts a currency to USD.
 */
public class CurrencyToUSD extends AsyncTask<String, Integer, Double> {
    // queries Bloomberg quotes
    // return the USD value of the given currency. -1 if there is error.
    private double value;
    public double result = 1.0;
    public CurrencyToUSD(int value) {
        this.value = value;
    }

    @Override
    protected Double doInBackground(String... symbol) {
        if (symbol[0].equals("USD")) return value;
        String url = "http://www.bloomberg.com/quote/" + symbol[0] + "USD:CUR";
        Log.e("CARD", url);
        try {
            URL bb = new URL(url);
            URLConnection conn = bb.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder buf = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                buf.append(inputLine);
            }
            in.close();
            String html = buf.toString();
            int index = html.indexOf("<div class=\"price\">");
            if (index < 0) {
                Log.e("CARD", "BLOOMBERG: TAG NOT FOUND!");
                return -1.0;
            }
            StringBuffer sb = new StringBuffer();
            for(int i = index + "<div class=\"price\">".length(); html.charAt(i) != '<'; i++) sb.append("" + html.charAt(i));
            return Double.parseDouble(sb.toString()) * value;
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }

    protected void onProgressUpdate(Integer progress) {
        //
    }

    protected void onPostExecute(Double result) {
        //
        this.result = result;
    }

}
