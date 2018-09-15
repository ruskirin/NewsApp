package com.project.udacity.my.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonParser {

    public static void guardianParser(String jsonResponse, List<Article> articles) {

        try {
            JSONObject obj = new JSONObject(jsonResponse);
            JSONObject jresponse = obj.getJSONObject("response");
            JSONArray jresults = jresponse.getJSONArray("results");
            int numResults = jresponse.getInt("pageSize");

            for(int i = 0; i < numResults; i++) {
                JSONObject jarticle = jresults.getJSONObject(i);
                JSONObject jfields = jarticle.getJSONObject("fields");
                JSONArray jtags = jarticle.getJSONArray("tags");
                JSONObject jauthor;

                Article a = new Article();
                a.setWebUrl(jarticle.getString("webUrl"));
                a.setSection(jarticle.getString("sectionName"));
                a.setTitle(jarticle.getString("webTitle"));
                a.setDate(jarticle.getString("webPublicationDate"));
                if(!jtags.isNull(0)) {
                    jauthor = jtags.getJSONObject(0);
                    a.setAuthor(jauthor.getString("webTitle"));
                }
                a.setBody(jfields.getString("body"));

                articles.add(a);
            }
        } catch(JSONException e) {
            e.printStackTrace();

        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }
}
