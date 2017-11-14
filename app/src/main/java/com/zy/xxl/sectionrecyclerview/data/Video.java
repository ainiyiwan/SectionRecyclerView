package com.zy.xxl.sectionrecyclerview.data;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class Video {
    private String img;
    private String name;
    private String year;

    public Video(String img, String name, String year) {
        this.img = img;
        this.name = name;
        this.year = year;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
