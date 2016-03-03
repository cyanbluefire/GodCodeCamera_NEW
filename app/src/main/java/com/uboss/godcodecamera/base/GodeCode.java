package com.uboss.godcodecamera.base;

import java.util.Date;

/**
 * Created by cuiyan on 16/3/3.
 */
public class GodeCode {
    private String content ; //二维码内容文字
    private int count;  //二维码内容图片数量
    private String url;    //二维码内容地址
    private String filename;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public GodeCode(String filename, String url, String content, int count) {
        this.content = content;
        this.count = count;
        this.url = url;
        this.filename = filename;
    }

    public GodeCode() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;

    }

    public int getCount() {
        return count;
    }

    public String getUrl() {
        return url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
