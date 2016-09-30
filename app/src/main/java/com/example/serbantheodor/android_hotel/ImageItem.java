package com.example.serbantheodor.android_hotel;

/**
 * Created by Serban Theodor on 25-Apr-16.
 */
public class ImageItem {
    private String url, filename, caption;
    private int seq_no;

    public ImageItem(String url, String filename, String caption, int seq_no) {
        this.url = url;
        this.filename = filename;
        this.caption = caption;
        this.seq_no = seq_no;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(int seq_no) {
        this.seq_no = seq_no;
    }
}
