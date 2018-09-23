package com.app.nevada.nevadaapp;

/**
 * Created by Mothana on 15/03/2017.
 */

public class StringWithTag {
    public String string;
    public Object tag;

    public StringWithTag(String stringPart, Object tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }
}