package de.verdox.vcore.util;

import java.util.Random;

public class RandomUtil {

    public double randomDouble(double min, double max){
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public int randomInt(int min, int max){
        Random r = new Random();
        return r.nextInt(max+1)+min;
    }

}
