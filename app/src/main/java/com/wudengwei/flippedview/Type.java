package com.wudengwei.flippedview;

/**
 * Created by wudengwei
 * on 2019/4/18
 */
public class Type {
    private String sign;
    private String title;
    private String sign1;
    private String title1;

    public Type(String sign,String title,String sign1,String title1) {
        this.sign = sign;
        this.title = title;
        this.sign1 = sign1;
        this.title1 = title1;
    }

    public String getSign1() {
        return sign1;
    }

    public void setSign1(String sign1) {
        this.sign1 = sign1;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
