package com.example.hzg.mysussr.bean;

import java.io.Serializable;

/**
 * Created by hzg on 2017/5/12.
 */

public class ConfigSelectBean  implements Serializable{
    String name;
    int postion;
    boolean isSelected;

    public ConfigSelectBean(String name, int postion) {
        this.name = name;
        this.postion = postion;
    }

    public String getName() {
        return name;
    }

    public int getPostion() {
        return postion;
    }
}
