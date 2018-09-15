package com.project.udacity.my.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private Messenger messenger;
    private String url;

    public ArticleLoader(@NonNull Context context, String url, Messenger messenger) {
        super(context);
        this.messenger = messenger;
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Article> loadInBackground() {
        List<Article> articles = new ArrayList<Article>();

        HttpHandler httpHandler = new HttpHandler(messenger);

        String jsonResponse = httpHandler.getJsonResponse(url);

        if(jsonResponse != null) {
            JsonParser.guardianParser(jsonResponse, articles);
        }

        return articles;
    }
}
