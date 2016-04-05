package com.example.qmma.featuredetection;

/**
 * Created by qmma on 16/4/5.
 */
public class ImageData {
    // reference to files
    public static int[] files = new int[] {R.drawable.usd_100, R.drawable.usd_1, R.drawable.rmb_100};
    // name of the image
    public static String[] files_names = {"100 USD bill", "1 USD bill", "100 RMB bill"};
    // type of the image. If the image is a bill, the type must match Bloomberg symbols.
    public static String[] types = {"USD", "USD", "CNY"};
    // value of the bill. If the image is not a bill, set it to 0.
    public static int[] values = new int[] {100, 1, 100};
}
