package com.themarbles.game;

import java.util.Arrays;
import java.util.stream.IntStream;

/** class for testing **/
public class TestClass {
    public static void main(String[] args){
        int[] a = IntStream.rangeClosed(1, 5).toArray();
        System.out.println(Arrays.toString(converter(a)));
    }

    static Integer[] converter(int[] toConvert){
        Integer[] converted = new Integer[toConvert.length];
        for (int item = 0; item < toConvert.length; item++){
            converted[item] = toConvert[item];
        }
        return converted;
    }
}
