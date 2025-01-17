package com.example.qmma.featuredetection;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qmma on 16/3/30.
 * Use this class to compare two pictures.
 */

public class CVCompare {
    // this method takes 2 input files and returns a similarity value between the two pictures.
    // it will also save a visualized comparison in outputDir.
    public static double rate = 0.75;
    public static int compareWithOutput(String inputFilePath1, String inputFilePath2, String outputDir) {
        String outputFileName="out";
        String outputExtension = "png";
        String outputFilePath = outputDir + File.separator + outputFileName + "." + outputExtension;
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        //first image
        Mat img1 = Imgcodecs.imread(inputFilePath1);
        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();

        detector.detect(img1, keypoints1);
        descriptor.compute(img1, keypoints1, descriptors1);
        //second image
        Mat img2 = Imgcodecs.imread(inputFilePath2);
        Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

        detector.detect(img2, keypoints2);
        descriptor.compute(img2, keypoints2, descriptors2);

        List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
        matcher.knnMatch(descriptors1, descriptors2, matches, 2);

        List<DMatch> good_matches = new ArrayList<DMatch>();
        for(int i = 0; i < matches.size(); i++) {
            if (matches.get(i).toList().get(0).distance < rate * matches.get(i).toList().get(1).distance)
                good_matches.add(matches.get(i).toList().get(0));
            //Log.e("TEST", String.valueOf(matches.get(i).toList().get(0).distance) + " " + String.valueOf(matches.get(i).toList().get(1).distance));
        }
        //

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);
        //feature and connection colors
        Scalar RED = new Scalar(255,0,0);
        Scalar GREEN = new Scalar(0,255,0);
        //output image
        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        //this will draw all matches
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, goodMatches,
                outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
        Imgcodecs.imwrite(outputFilePath, outputImg);
        Log.e("CARD", outputFilePath);
        return good_matches.size();
    }


    public static int compare(Mat descriptors1, Mat descriptors2) {
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
        matcher.knnMatch(descriptors1, descriptors2, matches, 2);

        List<DMatch> good_matches = new ArrayList<DMatch>();
        for(int i = 0; i < matches.size(); i++) {
            if (matches.get(i).toList().get(0).distance < 0.75 * matches.get(i).toList().get(1).distance)
                good_matches.add(matches.get(i).toList().get(0));
        }
        return good_matches.size();
    }

    public static String matToJson(Mat mat){
        JsonObject obj = new JsonObject();

        if(mat.isContinuous()){
            int cols = mat.cols();
            int rows = mat.rows();
            int elemSize = (int) mat.elemSize();

            byte[] data = new byte[cols * rows * elemSize];

            mat.get(0, 0, data);

            obj.addProperty("rows", mat.rows());
            obj.addProperty("cols", mat.cols());
            obj.addProperty("type", mat.type());

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            String dataString = new String(Base64.encode(data, Base64.DEFAULT));

            obj.addProperty("data", dataString);

            Gson gson = new Gson();
            String json = gson.toJson(obj);

            return json;
        } else {
            Log.e("CARD", "Mat not continuous.");
        }
        return "{}";
    }

    public static Mat matFromJson(String json){
        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        String dataString = JsonObject.get("data").getAsString();
        byte[] data = Base64.decode(dataString.getBytes(), Base64.DEFAULT);

        Mat mat = new Mat(rows, cols, type);
        mat.put(0, 0, data);

        return mat;
    }
}
