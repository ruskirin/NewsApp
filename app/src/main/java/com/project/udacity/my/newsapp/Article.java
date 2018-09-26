package com.project.udacity.my.newsapp;

import android.text.Html;
import android.util.JsonReader;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Article {

    private String webUrl = "";
    private String section = "";
    private String title = "";
    private String author = "";
    private String body = "";
    private String date = "";
    private boolean expanded = false;

    public Article() { }

    public void setWebUrl(String url) {
        this.webUrl = url;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSection() {
        return section;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author + " in\n" + section;
    }

    public String getAuthor() {
        return author;
    }

    public void setBody(String body) {
        //Display a portion of the article body
        if(body.length() == 0)
            this.body = "";
        else if(body.length() < 500)
            this.body = body.substring(0, body.length() - 1);
        else
            this.body = body.substring(0, 499);

    }

    public String getBody() {
        return body;
    }

    public void setDate(String date) {
        //Remove the time portion of the JSON response
        date = date.substring(0, date.indexOf('T'));
        this.date = date.replace('-', '\n');
    }

    public String getDate() {
        return date;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }
}
