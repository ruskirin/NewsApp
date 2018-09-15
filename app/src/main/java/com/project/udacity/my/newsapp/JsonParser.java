package com.project.udacity.my.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonParser {

    public static void guardianParser(String jsonResponse, List<Article> articles) {

        try {
            JSONObject jobject = new JSONObject(jsonResponse);
            JSONObject jresponse = jobject.getJSONObject("response");
            JSONArray jresults = jresponse.getJSONArray("results");
            int numResults = jresponse.getInt("pageSize");

            for(int i = 0; i < numResults; i++) {
                JSONObject jarticle = jresults.getJSONObject(i);
                JSONObject jfields = jarticle.getJSONObject("fields");
                JSONArray jtags = jarticle.getJSONArray("tags");
                JSONObject jauthor;

                Article articleObj = new Article();
                articleObj.setWebUrl(jarticle.getString("webUrl"));
                articleObj.setSection(jarticle.getString("sectionName"));
                articleObj.setTitle(jarticle.getString("webTitle"));
                articleObj.setDate(jarticle.getString("webPublicationDate"));
                if(!jtags.isNull(0)) {
                    jauthor = jtags.getJSONObject(0);
                    articleObj.setAuthor(jauthor.getString("webTitle"));
                }
                articleObj.setBody(jfields.getString("body"));

                articles.add(articleObj);
            }
        } catch(JSONException e) {
            e.printStackTrace();

        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }
}
