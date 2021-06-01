package com.kamennova.doggies;

import java.util.List;

public class Helpers {
    public static  <T> List<T> concatLists(List<T> a, List<T> b){
        a.addAll(b);
        return a;
    }
}
