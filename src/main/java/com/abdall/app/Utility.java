package com.abdall.app;

import processing.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class Utility {
    
    public static int[][] thinArray(int[][] arr) {
        int distanceToPrune = 7;
        int removed = 0;
        if (arr.length <= 10)
            return arr;

        do {
            System.out.println("iterating..");
            for (int i = 0; i < arr.length; i++) {
                int r = arr[i][0];
                int g = arr[i][1];
                int b = arr[i][2];
                // System.out.println(r + " " + g + " " + b);
                if (r == 255 && g == 255 && b == 255)
                    continue;

                // System.out.println("inside the loop");
                for (int compare = i + 1; compare < arr.length; compare++) {
                    int thisr = arr[compare][0];
                    int thisg = arr[compare][1];
                    int thisb = arr[compare][2];
                    // System.out.println(thisr + " " + thisg + " " + thisb);

                    if (thisr == 255 && thisg == 255 && thisb == 255)
                        ;
                    else {
                        double distance = Math
                                .sqrt(Math.pow(r - thisr, 2) + Math.pow(g - thisg, 2) + Math.pow(b - thisb, 2));
                        // System.out.println("Calculated distance: " + distance);
                        if (distance <= distanceToPrune && (arr.length - removed) > 10) {
                            arr[compare][0] = 255;
                            arr[compare][1] = 255;
                            arr[compare][2] = 255;

                            removed++;
                        }
                    }
                }
            }
            distanceToPrune += 10;
        } while (arr.length - removed > 10);

        int[][] finalColorSet = new int[10][3];

        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i][0] + arr[i][1] + arr[i][2] != (255 * 3)) {
                finalColorSet[count][0] = arr[i][0];
                finalColorSet[count][1] = arr[i][1];
                finalColorSet[count][2] = arr[i][2];
                count++;
            }
        }
        return finalColorSet;
    }
    
    public static int[][] distanceToWhite(int[][] arr) {
        ArrayList<Double> holdDistances = new ArrayList<Double>();
        Map<Double, Integer> map = new TreeMap<Double, Integer>();

        for (int i = 0; i < arr.length; i++) {
            int r = arr[i][0];
            int g = arr[i][1];
            int b = arr[i][2];
            double distance = Math.sqrt(Math.pow(r - 255, 2) + Math.pow(g - 255, 2) + Math.pow(b - 255, 2));
            holdDistances.add(distance);
            map.put(distance, i);
        }
        System.out.println(map);

        int[][] smallerArray = new int[10][3];

        TreeMap<Double, Integer> myNewMap = map.entrySet().stream().limit(10).collect(TreeMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

        System.out.println(myNewMap);

        List<Integer> newList = new ArrayList<Integer>(myNewMap.values());

        int count = 0;
        for (Integer i : newList) {
            smallerArray[count][0] = arr[i][0];
            smallerArray[count][1] = arr[i][1];
            smallerArray[count][2] = arr[i][2];
            count++;
        }

        return smallerArray;
    }

    public static int[][] ownPaletteApproach(PImage img, int topN) {
        img.loadPixels();
        int[] list = img.pixels;

        Multiset<Integer> numberMap = HashMultiset.create();
        for(int i : list){
            numberMap.add(i);
        }

        List<Integer> test = Multisets.copyHighestCountFirst(numberMap).elementSet().asList().subList(0, topN);
        int[][] smallerArray = new int[topN][3];
        int count = 0;
        for(int i : test){
            float red = i >> 16 & 0xFF;
            float green = i >> 8 & 0xFF;
            float blue = i & 0xFF;

            smallerArray[count][0] = Math.round(red);
            smallerArray[count][1] = Math.round(green);
            smallerArray[count][2] = Math.round(blue);
            count++;
            System.out.println(red + "," + green + "," + blue);
        }
        
        int[][] retArray = thinArray(smallerArray);
        return retArray;
    }

    public static double[][] convertRGBArrayToLABArray(int[] rgbArr){
        double[][] labArray = new double[rgbArr.length][3];

        for(int i = 0; i < rgbArr.length; i++){
            double[] xyzStep = RGBConvert.rgbToXYZ(rgbArr[i]);
            double[] labColor = RGBConvert.xyzToLAB(xyzStep);
            labArray[i][0] = labColor[0];
            labArray[i][1] = labColor[1];
            labArray[i][2] = labColor[2];
        }

        return labArray;
    }
}