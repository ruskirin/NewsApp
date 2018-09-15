package com.project.udacity.my.newsapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHandler {

    private Messenger messenger;

    public HttpHandler(Messenger messenger) {
        this.messenger = messenger;
    }

    public String getJsonResponse(String urlString) {
        String response = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int responseCode = urlConnection.getResponseCode();
            if(responseCode >= 200 && responseCode < 300) {
                urlConnection.setRequestMethod("GET");

                InputStream iStream = new BufferedInputStream(urlConnection.getInputStream());
                response = streamToString(iStream);

            } else
                messenger.addressResponse(responseCode);

        } catch(MalformedURLException e) {
            e.printStackTrace();

        } catch(IOException e) {
            e.printStackTrace();

        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        return response;
    }

    private String streamToString(InputStream iStream) {
        BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
        StringBuilder sBuilder = new StringBuilder();
        String line;

        try {
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line);
            }

        } catch(IOException e) {
            e.printStackTrace();

        } finally {
            try {
                iStream.close();

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return sBuilder.toString();
    }
}
