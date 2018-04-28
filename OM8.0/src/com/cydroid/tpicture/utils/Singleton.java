package com.cydroid.tpicture.utils;

import com.cydroid.tpicture.animal.Pig;
import com.cydroid.tpicture.bean.PictureListenner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiang on 4/9/18.
 */
public class Singleton {
    private static Singleton instance = null;
    private PictureListenner mPictureListenner;
    private List pigList = new ArrayList<Pig>();


    public Singleton() {}

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    public PictureListenner getPictureListenner() {
        return mPictureListenner;
    }

    public void setmPictureListenner(PictureListenner mPictureListenner) {
        this.mPictureListenner = mPictureListenner;
    }

    public void unPictureListenner() {
        this.mPictureListenner = null;
    }

    public List getPigList() {
        return pigList;
    }

    public void setPigList(List pigList) {
        this.pigList = pigList;
    }

    public void clearPigList() {
        pigList.clear();
    }
}
