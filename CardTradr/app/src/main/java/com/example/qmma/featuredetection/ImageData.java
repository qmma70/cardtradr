package com.example.qmma.featuredetection;

/**
 * Created by qmma on 16/4/5.
 */
public class ImageData {

    // Attention: add new images to the tails of arrays! Do not insert in the front or middle!
    // reference to files
    public static int[] files = new int[]
            {R.drawable.usd_100, R.drawable.usd_1, R.drawable.rmb_100, R.drawable.mxn_50, R.drawable.mxn_100, R.drawable.usd_20};
    // name of the image
    public static String[] files_names =
            {"100 USD bill", "1 USD bill", "100 RMB bill", "50 Mexican Pesos Bill", "100 Mexican Pesos Bill", "20 USD bill"};
    // type of the image.
    public static String[] types =
            {"USD", "USD", "CNY", "MXN", "MXN", "USD"};

}
