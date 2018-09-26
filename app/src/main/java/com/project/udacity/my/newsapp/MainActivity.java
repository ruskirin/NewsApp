package com.project.udacity.my.newsapp;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Article>>,
        Messenger {


    private final static Calendar TODAY = Calendar.getInstance(Locale.US);
    public final static int TODAY_YEAR = TODAY.get(Calendar.YEAR);
    public final static int TODAY_MONTH = TODAY.get(Calendar.MONTH) + 1;
    public final static int TODAY_DAY = TODAY.get(Calendar.DAY_OF_MONTH);

    //Switches depending on HTTP response from server
    private boolean responseGood;

    public static final int LOADER_ONE = 1;
    public static final String GUARDIAN_BASE_URL = "https://content.guardianapis.com";

    private ArticleRecyclerAdapter articleAdapter;
    private SwipeRefreshLayout articleRefreshLayout;
    private SharedPreferences sharedPreferences;

    private DrawerLayout drawerLayout;
    private LinearLayout datePickers;
    private NumberPicker drawerSectionPicker;

    private TextView noDataView;
    private LinearLayout drawerDateChoice;
    private TextView drawerSectionChoice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar mainToolbar;
        RecyclerView articleView;
        final ScrollView drawerPickerScroll;

        TextView drawerTitleSettings;
        final View drawerSettingsSeparator;
        TextView drawerTitleSection;
        TextView drawerTitleDate;
        NumberPicker drawerDateDayPicker;
        NumberPicker drawerDateMonthPicker;
        NumberPicker drawerDateYearPicker;

        List<Article> articleList = new ArrayList<>();

        final String[] SECTION_ARRAY =
                getResources().getStringArray(R.array.drawer_settings_section_alltitles);

        final String SETTINGS_FILE_ONE = "settings1";
        sharedPreferences = getSharedPreferences(SETTINGS_FILE_ONE, MODE_PRIVATE);

        /*
         * TOOLBAR - access navigation drawer for settings
         */
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        //TextView for displaying any http-related error messages
        noDataView = findViewById(R.id.main_text_nodata);

        drawerLayout = findViewById(R.id.main_drawer_layout);
        drawerPickerScroll = findViewById(R.id.main_drawer_settings_picker_scrollview);
        drawerTitleSettings = findViewById(R.id.main_drawer_settings_title_textview);
        drawerSettingsSeparator = findViewById(R.id.main_drawer_settings_separator);
        drawerTitleSection = findViewById(R.id.main_drawer_title_section);
        drawerTitleDate = findViewById(R.id.main_drawer_title_date);
        drawerSectionChoice = findViewById(R.id.main_drawer_section_choice);
        drawerDateChoice = findViewById(R.id.main_drawer_date_choicelayout);
        datePickers = findViewById(R.id.main_drawer_datepickers);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        articleAdapter = new ArticleRecyclerAdapter(articleList);


        articleView = findViewById(R.id.main_recycler_view);
        articleView.setLayoutManager(layoutManager);
        articleView.setAdapter(articleAdapter);


        LoaderManager loaderManager = getSupportLoaderManager();
        if(isNetworkAvailable())
            loaderManager.initLoader(LOADER_ONE, null, this);
        else {
            noDataView.setVisibility(View.VISIBLE);
            noDataView.setText(R.string.network_error);
        }

        /*
         * SettingsUtil instance
         */
        SettingsUtil settingsUtil = new SettingsUtil(sharedPreferences,
                loaderManager,
                drawerSectionChoice,
                this);

        /*
         * RefreshLayout: does nothing, for visual response
         */
        articleRefreshLayout = findViewById(R.id.main_refresh_layout);
        articleRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(articleRefreshLayout.isRefreshing())
                    articleRefreshLayout.setRefreshing(false);

                Toast.makeText(MainActivity.this, "UPDATED", Toast.LENGTH_SHORT).show();
            }
        });

        /*
         * DRAWER - Numberpickers
         */
        drawerLayout.addDrawerListener(settingsUtil);

        drawerSectionPicker = findViewById(R.id.main_drawer_picker_section);
        drawerSectionPicker.setMinValue(0);
        drawerSectionPicker.setMaxValue(SECTION_ARRAY.length - 1);
        drawerSectionPicker.setDisplayedValues(SECTION_ARRAY);
        drawerSectionPicker.setWrapSelectorWheel(false);
        drawerSectionPicker.setOnValueChangedListener(settingsUtil);

        drawerDateMonthPicker = findViewById(R.id.main_drawer_picker_date_month);
        drawerDateMonthPicker.setMinValue(getResources().getInteger(R.integer.settings_month_from));
        drawerDateMonthPicker.setMaxValue(getResources().getInteger(R.integer.settings_month_to));
        drawerDateMonthPicker.setWrapSelectorWheel(false);
        drawerDateMonthPicker.setOnValueChangedListener(settingsUtil);

        drawerDateDayPicker = findViewById(R.id.main_drawer_picker_date_day);
        drawerDateDayPicker.setMinValue(getResources().getInteger(R.integer.settings_day_from));
        drawerDateDayPicker.setMaxValue(getResources().getInteger(R.integer.settings_day_to_more));
        drawerDateDayPicker.setWrapSelectorWheel(false);
        drawerDateDayPicker.setOnValueChangedListener(settingsUtil);

        drawerDateYearPicker = findViewById(R.id.main_drawer_picker_date_year);
        drawerDateYearPicker.setMinValue(getResources().getInteger(R.integer.settings_year_from));
        drawerDateYearPicker.setMaxValue(TODAY_YEAR);
        drawerDateYearPicker.setWrapSelectorWheel(false);
        drawerDateYearPicker.setOnValueChangedListener(settingsUtil);

        drawerTitleSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerPickerScroll.setVisibility(
                        drawerPickerScroll.getVisibility() == View.VISIBLE ?
                                View.GONE : View.VISIBLE);
                drawerSettingsSeparator.setVisibility(
                        drawerPickerScroll.getVisibility() == View.VISIBLE ?
                                View.GONE : View.VISIBLE);
            }
        });

        drawerTitleSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerSectionPicker.setVisibility(
                        drawerSectionPicker.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                drawerSectionChoice.setVisibility(
                        drawerSectionPicker.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        drawerTitleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickers.setVisibility(
                        datePickers.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                drawerDateChoice.setVisibility(
                        datePickers.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }

    /*
     * LOADER - onCreateLoader, onLoadFinished, onLoadReset
     */
    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int i, @Nullable Bundle bundle) {

        getSupportActionBar().setTitle(sharedPreferences.getString("section", null));
        return new ArticleLoader(
                this, sharedPreferences.getString("url", null), this);
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

    /*
     * UTIL -
     *  onOptionsItemSelected: open drawer on menu button tap
     *  addressResponse: display TextView if Http response is bad
     *  isNetworkAvailable: check for network connectivity
     *  printList: print to terminal all articles received from Json query
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
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
