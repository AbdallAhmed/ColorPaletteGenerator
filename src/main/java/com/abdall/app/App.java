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
    String imageString = "./resources/Images/IMG_3038.jpg";
    int[][] pal;

    public static void main() {
        PApplet.main("App");
        App pt = new App();
        PApplet.runSketch(new String[] { "ProcessingTest" }, pt);
    }

    public void settings() {
        size(1200, 1200);

        BufferedImage source = null;
        try {
            source = ImageIO.read(new File(imageString));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        pal = ColorThief.getPalette(source, 10, 1, true);
        System.out.println("******Before grooming*************");
        for (int[] a : pal) {
            for (int i : a) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
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
        img.resize(1080, 0);
        // System.out.println(img.height);
    }

    public void setup() {
        background(255);

    }

    public void draw() {
        noLoop();
        // rectMode(CENTER);
        // rect(width/2,height/2,500,500+160);
        
        // fill(235, 143, 136);
        // rect(width/2,(height/2)-80,500,500);

        // line(width/2, 0,width/2, height);
        // line(0, height/2, width, height/2);

        imageMode(CENTER);
        image(img, width/2, (height/2)-80);
        
        rectMode(CORNER);
        for (int i = 0; i < pal.length; i++) {
            fill(pal[i][0], pal[i][1], pal[i][2]);
            int rectangleWidth =  (width-(10*(pal.length-1))-120)/pal.length;
            int xPosition = 60+((rectangleWidth+10)*i);

            int yPosition = ((height/2)-75)+(img.height/2) + 10;
          
            rect(xPosition, yPosition, rectangleWidth, 150);
            
        }

        save("yee-yee.jpg");
        // for (int i = 0; i < pal4.length; i++) {
        //     fill(pal4[i][0], pal4[i][1], pal4[i][2]);
        //     rect(50 + (i * 75), 500, 75, 100);
        // }
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
