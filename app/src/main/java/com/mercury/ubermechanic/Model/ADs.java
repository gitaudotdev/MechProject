package com.mercury.ubermechanic.Model;

public class ADs {
 String Url;
 int drawable;

    public ADs(String url) {
        Url = url;
    }

    public ADs(int drawable) {
        this.drawable = drawable;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
