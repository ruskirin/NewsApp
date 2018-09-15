package com.project.udacity.my.newsapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Article>>,
        Messenger {

    public static final String GUARDIAN_TECH_URL = "https://content.guardianapis.com/technology?&show-tags=contributor&show-fields=body&page-size=15&api-key=69ed075d-c75c-4a27-a955-59dd78da5af0";

    private boolean responseGood = true;

    private RecyclerView articleView;
    private MyRecyclerAdapter articleAdapter;
    private List<Article> articles = new ArrayList<Article>();
    private TextView noDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int LOADER_ONE = 1;

        noDataView = findViewById(R.id.main_text_nodata);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        articleView = findViewById(R.id.main_recyclerview);
        articleView.setLayoutManager(layoutManager);

        articleAdapter = new MyRecyclerAdapter(articles);
        articleView.setAdapter(articleAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        if(isNetworkAvailable()) {
            loaderManager.initLoader(LOADER_ONE, null, this);

        } else {
            noDataView.setVisibility(View.VISIBLE);
            noDataView.setText(R.string.network_error);
        }

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.main_swrefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

                //Doesn't yet do anything, more for visual effect
                Toast.makeText(MainActivity.this, "UPDATED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new ArticleLoader(this, GUARDIAN_TECH_URL, this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> articles) {

        articleAdapter.clear();

        if(articles.size() != 0) {
            noDataView.setVisibility(View.GONE);
            articleAdapter.addList(articles);

        } else if(articles.size() == 0 && responseGood) {
            noDataView.setText(R.string.noarticles);
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        articleAdapter.clear();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void addressResponse(final int code) {

        //TextView refusing to update outside the main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("CODE", "" + code);

                responseGood = false;

                if (code >= 400 && code < 500) {
                    noDataView.setVisibility(View.VISIBLE);
                    noDataView.setText(R.string.error400);

                } else if (code >= 500) {
                    noDataView.setVisibility(View.VISIBLE);
                    noDataView.setText(R.string.error500);

                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    noDataView.setText(R.string.server_response_error);
                }
            }
        });



    }

    public static void printList(List<Article> list) {

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getWebUrl() + "\n");
            sb.append(list.get(i).getTitle() + "\n");
            sb.append(list.get(i).getDate() + "\n");
            sb.append(list.get(i).getAuthor() + "\n");
            sb.append(list.get(i).getBody());
        }
        System.out.println(sb.toString());
    }


}
