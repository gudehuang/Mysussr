package com.example.hzg.mysussr.utils;

import java.util.Arrays;

/**
 * Created by hzg on 2017/5/12.
 *
 */

public class ArrayUtils {
    public static <T> T[] concat(T[] frist, T[]...rest) {
        int totalLenght=frist.length;
        for (T[] t:rest)
        {
            totalLenght+=t.length;
        }
        T[] result= Arrays.copyOf(frist,totalLenght);
        int offset=frist.length;
       for (T[] t:rest)
       {
           System.arraycopy(t,0,result, offset,t.length);
       }
        return result;
    }
}
