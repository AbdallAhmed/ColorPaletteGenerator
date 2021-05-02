package com.abdall.app;


public class RGBConvert {

    /*
        Reference values used for sRGB conversions
    */
    private static final double D65_TRISTIMULUS_REF_X = 95.047; 
    private static final double D65_TRISTIMULUS_REF_Y = 100.000; 
    private static final double D65_TRISTIMULUS_REF_Z = 108.883; 

    /**
     * Standard conversion to go from an RGB color to an XYZ color
     * @param rgbColor An integer that holds the RGB representation of a color
     * @return double[] Returns the XYZ representation for the passed in RGB color
     */
    public static double[] rgbToXYZ(int rgbColor){
        //Extract the r, g, b value from the integer
        double red = rgbColor >> 16 & 0xFF;
        double green = rgbColor >> 8 & 0xFF;
        double blue = rgbColor & 0xFF;

        //normalize on a scale of 0 - 1
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

        double holdColor[] = {X, Y, Z};
        return holdColor;
    }

    /**
     * Conversion from XYZ to to CIE-L*ab
     * @param xzyColor an array that holds the XYZ representation of a color
     * @return double[] Returns the CIE-L*ab representation for the passed in XYZ color 
     */
    public static double[] xyzToLAB(double[] xyzColor){
        double X = xyzColor[0];
        double Y = xyzColor[1]; 
        double Z = xyzColor[2];

        X = X / D65_TRISTIMULUS_REF_X;
        Y = Y / D65_TRISTIMULUS_REF_Y;
        Z = Z / D65_TRISTIMULUS_REF_Z;

        X = X > 0.008856 ? Math.pow(X, (1.0/3)) : ((X * 7.787) + (16 / 116));
        Y = Y > 0.008856 ? Math.pow(Y, (1.0/3)) : ((Y * 7.787) + (16 / 116));
        Z = Z > 0.008856 ? Math.pow(Z, (1.0/3)) : ((Z * 7.787) + (16 / 116));

        double L = ( 116 * Y ) - 16; 
        double a = 500 * (X - Y);
        double b = 200 * (Y - Z);

        double holdLab[] = {L, a, b};
        return holdLab;
        
    }
}