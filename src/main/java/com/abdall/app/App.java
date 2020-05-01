package com.abdall.app;

import processing.core.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import de.androidpit.colorthief.ColorThief;

/**
 * Hello world!
 */
public class App extends PApplet {

    PImage img;
    String imageString = "./resources/Images/IMG_0140.jpg";
    int[][] pal;
    int majorAxis = 1080;
    int squareAxis = 1200;
    int nonMajorAxis; 

    public static void main() {
        PApplet.main("App");
        App pt = new App();
        PApplet.runSketch(new String[] { "ProcessingTest" }, pt);
    }

    public void settings() {
        size(squareAxis, squareAxis);

        BufferedImage source = null;
        try {
            source = ImageIO.read(new File(imageString));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        pal = ColorThief.getPalette(source, 10, 1, true);
        // System.out.println("******Before grooming*************");
        // for (int[] a : pal) {
        //     for (int i : a) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.println();
        // }
        // thinArrayTwo(pal);
        //pal = thinArray(pal);

        int[][] finalColorSet = new int[10][3];

        int count = 0;
        for (int i = 0; i < pal.length; i++) {
            if (pal[i][0] + pal[i][1] + pal[i][2] != (255 * 3)) {
                finalColorSet[count][0] = pal[i][0];
                finalColorSet[count][1] = pal[i][1];
                finalColorSet[count][2] = pal[i][2];
                count++;
            }
        }

        System.out.println(count);
        pal = finalColorSet;

        img = loadImage(imageString);
        if(img.width > img.height){
            img.resize(majorAxis, 0);
            nonMajorAxis = img.height;
        }
        else{
            img.resize(0, majorAxis);
            nonMajorAxis = img.width;
        }
        
    }

    public void setup() {
        background(255);

    }

    public void draw() {
        noLoop();
 
        imageMode(CENTER);
        if(img.width > img.height){
            image(img, squareAxis/2, (squareAxis/2)-80);
        }
        else{
            image(img, (squareAxis/2)-80, squareAxis/2);
        }

        noStroke();
        rectMode(CORNER);

        //major axis minus the excess minus the padding between each rectangle
        //this is the size of the entire block next to the image (minus one rectangle)
        //so divide by # of colors
        int rectangleVariableDim = (majorAxis - (10*(pal.length-1))) /pal.length; 

        //center of the image is 80 pixels up from the half
        //add half the image non-major axis to the middle of it
        //give a 10 pixel buffer
        int rectangleVariablPos =  ((squareAxis/2)-80) + (nonMajorAxis / 2) + 10;

        int fixedDim = 150;
        for (int i = 0; i < pal.length; i++) {
            fill(pal[i][0], pal[i][1], pal[i][2]);

            //start from the edge of the image and build out (+10 for the spacing)
            int movingPos = ((squareAxis - majorAxis) / 2)+((rectangleVariableDim+10)*i);
            
            if(img.width > img.height){
                rect(movingPos, rectangleVariablPos, rectangleVariableDim, fixedDim);
            }
            else{
                rect(rectangleVariablPos, movingPos, 150, rectangleVariableDim);
            }
            
        }

        save("yee-yee.jpg");
    }

    public int[][] thinArray(int[][] arr) {
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
        return arr;
    }

    public int[][] thinArrayTwo(int[][] arr) {
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

        TreeMap<Double, Integer> myNewMap = map.entrySet().stream()
        .limit(10)
        .collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

        System.out.println(myNewMap);

       List<Integer> newList = new ArrayList<Integer>(myNewMap.values());

       int count = 0;
       for(Integer i : newList){
           smallerArray[count][0] = arr[i][0];
           smallerArray[count][1] = arr[i][1];
           smallerArray[count][2] = arr[i][2];
           count++;
       }

       return smallerArray; 
    }
}
