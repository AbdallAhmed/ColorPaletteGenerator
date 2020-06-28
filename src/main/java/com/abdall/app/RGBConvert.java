package com.abdall.app;


public class RGBConvert {
    public static void rgbToXYZ(int rgbColor){
        double red = rgbColor >> 16 & 0xFF;
        double green = rgbColor >> 8 & 0xFF;
        double blue = rgbColor & 0xFF;

        double normalizedRed = red / 255;
        double normalizedGreen = green / 255;
        double normalizedBlue = blue / 255;

        
        normalizedRed = normalizedRed > 0.04045 ? Math.pow(((normalizedRed+0.055)/1.055), 2.4) : (normalizedRed/12.92);
        normalizedGreen = normalizedGreen > 0.04045 ? Math.pow(((normalizedGreen+0.055)/1.055), 2.4) : (normalizedGreen/12.92);
        normalizedBlue = normalizedBlue > 0.04045 ? Math.pow(((normalizedBlue+0.055)/1.055), 2.4) : (normalizedBlue/12.92);

        normalizedRed*=100;
        normalizedGreen*=100;
        normalizedBlue*=100;

        double X = normalizedRed * 0.4124 + normalizedGreen * 0.3576 + normalizedBlue * 0.1805;
        double Y = normalizedRed * 0.2126 + normalizedGreen * 0.7152 + normalizedBlue * 0.0722;
        double Z = normalizedRed * 0.0193 + normalizedGreen * 0.1192 + normalizedBlue * 0.9505;
        System.out.format("new red: %f, new green: %f, new blue: %f\n", X, Y, Z);
    }
}