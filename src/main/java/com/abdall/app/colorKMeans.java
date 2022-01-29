package com.abdall.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import processing.core.*;

public class colorKMeans {
    
    static class Coordinate implements Comparable<Coordinate> {
        
        private int x;
        private int y; 
        private int z;

        public Coordinate(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public int getX(){ return this.x; }
        public int getY(){ return this.y; }
        public int getZ(){ return this.z; }

        public double distance(Coordinate other){
             return Math.sqrt(
                 Math.pow(this.x - other.getX(), 2) + 
                 Math.pow(this.y - other.getY(), 2) + 
                 Math.pow(this.z - other.getZ(), 2)
            );
        }

        @Override
        public int compareTo(Coordinate other){
            if(this.getX() == other.getX()) {
                if(this.getY() == other.getY()) {
                    if(this.getZ() == other.getZ()) {
                        return 0;
                    }
                    else return this.getZ() > other.getZ() ? 1 : -1;
                }
                else return this.getY() > other.getY() ? 1 : -1;
            }
            else return this.getX() > other.getX() ? 1 : -1;
        }
        
        public boolean equals(Object other){
            if(other==null || !(other instanceof Coordinate)) return false;
            else{
                Coordinate cOther = (Coordinate)other;
                return  this.getX() == cOther.getX() &&
                        this.getY() == cOther.getY() &&
                        this.getZ() == cOther.getZ();
            }
        }
        // public boolean equals(Coordinate other){
        //     return  this.getX() == other.getX() &&
        //             this.getY() == other.getY() &&
        //             this.getZ() == other.getZ();
        // }

        public String toString(){
            return "[" + this.getX() + "," + this.getY() + "," + this.getZ() + "]";
        }
    }

    public Coordinate[] pixelArrayToCoordinateArray(PImage img){
        img.loadPixels();
        int[] holdPixels = img.pixels;
        Coordinate[] ret = new Coordinate[holdPixels.length];
        
        for(int i = 0 ; i < holdPixels.length ; i++){
            int currentColor = holdPixels[i];

            float red = currentColor >> 16 & 0xFF;
            float green = currentColor >> 8 & 0xFF;
            float blue = currentColor & 0xFF;

            Coordinate c = new Coordinate(Math.round(red), Math.round(green), Math.round(blue));
            ret[i] = c;
        }

        return ret;
    }

    public List<Coordinate> initializeCenters(Coordinate[] coordinates, int numColors){
        List<Coordinate> coordArray = new ArrayList<Coordinate>();
        int minX = Integer.MAX_VALUE; 
        int minY = Integer.MAX_VALUE; 
        int minZ = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0; 
        int maxZ = 0;

        for(Coordinate c : coordinates){
            minX = Math.min(c.getX(), minX);
            minY = Math.min(c.getY(), minY);
            minZ = Math.min(c.getZ(), minZ);

            maxX = Math.max(c.getX(), maxX);
            maxY = Math.max(c.getY(), maxY);
            maxZ = Math.max(c.getZ(), maxZ);
        }

        Random random = new Random();
        for(int i = 0; i < numColors; i++){
            int coordX = random.nextInt(maxX - minX) + minX;
            int coordY = random.nextInt(maxY - minY) + minY;
            int coordZ = random.nextInt(maxZ - minZ) + minZ;

            Coordinate c = new Coordinate(coordX, coordY, coordZ);
            coordArray.add(c);
        }
        return coordArray;
    }

    public List<Coordinate> initializeCentersLogical(Coordinate[] coordinates, int numColors){
        List<Coordinate> coordArray = new ArrayList<Coordinate>();
        
        Random random = new Random();
        for(int i = 0; i < numColors; i++){
            int index = random.nextInt(coordinates.length);

            while(coordArray.contains(coordinates[index])){
                index = random.nextInt(coordinates.length);
            }

            coordArray.add(coordinates[index]);
        }
        return coordArray;
    }

    public List<Coordinate> reComputeCenters(HashMap<Coordinate, List<Coordinate>> centerFreq){
        List<Coordinate> newCenters = new ArrayList<Coordinate>();

        for(Coordinate center : centerFreq.keySet()){
            List<Coordinate> curr = centerFreq.get(center);
            
            if(!curr.isEmpty()){
                int totalX = 0;
                int totalY = 0;
                int totalZ = 0;

                for(Coordinate c : curr){
                    totalX += c.getX();
                    totalY += c.getY();
                    totalZ += c.getZ();
                }

                int avgX = totalX / curr.size();
                int avgY = totalY / curr.size();
                int avgZ = totalZ / curr.size();

                newCenters.add(new Coordinate(avgX, avgY, avgZ));
            }
            else newCenters.add(center);
        }
        return newCenters;
    }

    public void assignCenters(HashMap<Coordinate, List<Coordinate>> centerFreq, Coordinate[] pixels){

        for(Coordinate point : pixels){
            double distance = Double.MAX_VALUE;
            Coordinate closestCenter = new Coordinate(0, 0, 0);

            for(Coordinate center : centerFreq.keySet()){
                double currDistance = point.distance(center);
                if(currDistance < distance){
                    distance = currDistance;
                    closestCenter = center;
                }
            }

            centerFreq.get(closestCenter).add(point);
        }
    }

    public void generateMap(HashMap<Coordinate, List<Coordinate>> centerFreq, List<Coordinate> centers){
        centerFreq.clear();
        for(Coordinate c: centers){
            centerFreq.put(c, new ArrayList<Coordinate>());
        }
    }

    public Coordinate mostOccurences(List<Coordinate> coords){

        if(coords.isEmpty())
            System.out.println("no points");
        Coordinate mostFrequentCoordinate = new Coordinate(0, 0, 0);
        int maxOccurences = 0;
        HashMap<Coordinate, Integer> frequencyMap = new HashMap<Coordinate, Integer>();

        for(Coordinate c : coords){
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            if(frequencyMap.get(c) > maxOccurences){
                maxOccurences = frequencyMap.get(c);
                mostFrequentCoordinate = c;
            }
        }
        return mostFrequentCoordinate;
    }

    public static int[][] computeKMeans(PImage img, int numColors){
        
        colorKMeans ref = new colorKMeans();
        int[][] finalColors = new int[numColors][3];
        
        Coordinate[] initColorPalette = ref.pixelArrayToCoordinateArray(img);
        List<Coordinate> centers = ref.initializeCentersLogical(initColorPalette, numColors);

        HashMap<Coordinate, List<Coordinate>> closeToCenterMap = new HashMap<Coordinate, List<Coordinate>>();

        ref.generateMap(closeToCenterMap, centers);

        for(int i = 0; i < numColors * 10; i++){
            ref.assignCenters(closeToCenterMap, initColorPalette);
            List<Coordinate> newCenters = ref.reComputeCenters(closeToCenterMap);

            //if the new centers are not equal then regenerate the map
            Collections.sort(centers);
            Collections.sort(newCenters);
            
            // System.out.println(centers);
            // System.out.println(newCenters);
            // System.out.println("Point comparison: " + centers.get(0).equals(newCenters.get(0)));
            // System.out.println("List comparison: " + centers.equals(newCenters));
            // System.out.println("======================================");
            if(centers.equals(newCenters) || i == (numColors*10) - 1){
                break;
            }
            centers = newCenters;
            
            ref.generateMap(closeToCenterMap, newCenters);
        }

        Coordinate dud = new Coordinate(0,0,0);
        int i = 0;
        for(Coordinate center : closeToCenterMap.keySet()){
            Coordinate mostFreq = ref.mostOccurences(closeToCenterMap.get(center));

            if(mostFreq.equals(dud)){
                finalColors[i][0] = center.getX();
                finalColors[i][1] = center.getY();
                finalColors[i][2] = center.getZ();
            }
            else {
                finalColors[i][0] = mostFreq.getX();
                finalColors[i][1] = mostFreq.getY();
                finalColors[i][2] = mostFreq.getZ();
            }
            i++;
        }
        return finalColors;
    }
}
