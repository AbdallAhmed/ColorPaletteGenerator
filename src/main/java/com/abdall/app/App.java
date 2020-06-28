package com.abdall.app;

import processing.core.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Hello world!
 */
public class App extends PApplet {

    PImage img;
    String imageString = "./resources/Images/IMG_0335.jpg";
    int[][] pal;
    int[][] pal2;
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

        // pal = ColorThief.getPalette(source, 30, 1, true);
        // pal = Utility.thinArray(pal);
        
        img = loadImage(imageString);
        if (img.width > img.height) {
            img.resize(majorAxis, 0);
            nonMajorAxis = img.height;
        } else {
            img.resize(0, majorAxis);
            nonMajorAxis = img.width;
        }

        pal = Utility.ownPaletteApproach(img,1000);
        pal = Utility.distanceToWhite(pal);
        img.loadPixels();;
        int[] list = img.pixels;
        int i = list[20];
        float red = i >> 16 & 0xFF;
        float green = i >> 8 & 0xFF;
        float blue = i & 0xFF;
        System.out.format("original rgb: %f, %f, %f \n",red,green,blue);
        RGBConvert.rgbToXYZ(i);
    }

    public void setup() {
        background(255);

    }

    public void draw() {
        noLoop();

        imageMode(CENTER);
        if (img.width > img.height) {
            image(img, squareAxis / 2, (squareAxis / 2) - 80);
        } else {
            image(img, (squareAxis / 2) - 80, squareAxis / 2);
        }

        noStroke();
        rectMode(CORNER);

        //major axis minus the padding between each rectangle
        //this is the size of the entire block next to the image (minus one rectangle)
        //so divide by # of colors
        int rectangleVariableDim = (majorAxis - (10*(pal.length-1))) /pal.length;

        //center of the image is 80 pixels up from the half
        //add half the image non-major axis to the middle of it
        //give a 10 pixel buffer
        int rectangleVariablPos = ((squareAxis/2)-80) + (nonMajorAxis / 2) + 10;

        int fixedDim = 150;
        for (int i = 0; i < pal.length; i++) {
            fill(pal[i][0], pal[i][1], pal[i][2]);

            //start from the edge of the image and build out (+10 for the spacing)
            int movingPos = ((squareAxis - majorAxis) / 2)+((rectangleVariableDim+10)*i);

            if(img.width > img.height){
                rect(movingPos, rectangleVariablPos, rectangleVariableDim, fixedDim);
            }
            else{
                rect(rectangleVariablPos, movingPos, fixedDim, rectangleVariableDim);
            }
        }

        save("yee-yee.jpg");
    }
}
