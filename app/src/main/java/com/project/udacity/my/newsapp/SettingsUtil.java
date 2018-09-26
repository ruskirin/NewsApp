package com.project.udacity.my.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SettingsUtil extends DrawerLayout.SimpleDrawerListener
                          implements NumberPicker.OnValueChangeListener {


    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private android.support.v4.app.LoaderManager loaderManager;

    private TextView drawerSectionChoice;
    private TextView drawerYearChoice,
                drawerMonthChoice,
                drawerDayChoice;

    private boolean isSettingChange;

    public SettingsUtil(SharedPreferences sharedPreferences,
                        android.support.v4.app.LoaderManager loaderManager,
                        TextView drawerSectionChoice,
                        Context context) {

        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.loaderManager = loaderManager;

        this.drawerSectionChoice = drawerSectionChoice;
        this.drawerYearChoice = ((Activity)context).findViewById(R.id.main_drawer_year_choice);
        this.drawerMonthChoice = ((Activity)context).findViewById(R.id.main_drawer_month_choice);
        this.drawerDayChoice = ((Activity)context).findViewById(R.id.main_drawer_day_choice);
    }

    //TODO: load previous settings here if there are any
    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);

        isSettingChange = false;
        editor = sharedPreferences.edit();

        drawerSectionChoice.setText(sharedPreferences.getString("section", null));
        drawerYearChoice.setText(
                Integer.toString(sharedPreferences.getInt("date year", MainActivity.TODAY_YEAR)) + '-');
        drawerMonthChoice.setText(
                Integer.toString(sharedPreferences.getInt("date month", MainActivity.TODAY_MONTH)) + '-');
        drawerDayChoice.setText(
                Integer.toString(sharedPreferences.getInt("date day", MainActivity.TODAY_DAY)));
    }

    /*
     *  Commit/Apply all changes to settings in SharedPreference editor.
     *  Update the UI according to the settings specified
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);

        verifyDate();
        editor.apply();
        buildUri();
        editor.commit();
        Log.i("onDrawerClosed", "URL2: " + sharedPreferences.getString("url", null));

        if(isSettingChange)
            loaderManager.restartLoader(MainActivity.LOADER_ONE, null, (MainActivity)context);
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //TODO: check project rubric, just need to display selected options next to option name
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        int id = picker.getId();

        Log.i("onValueChange", "ID: " + id + ", oldVal: " + oldVal + ", newVal: " + newVal);

        if(newVal != oldVal) {
            isSettingChange = true;

            switch(id) {
                case R.id.main_drawer_picker_section:
                    String section = switchSection(newVal);
                    drawerSectionChoice.setText(section);
                    editor.putString("section", section);
                    break;

                case R.id.main_drawer_picker_date_month:
                    drawerMonthChoice.setText(Integer.toString(newVal) + '-');
                    editor.putInt("date month", newVal);
                    break;

                case R.id.main_drawer_picker_date_day:
                    drawerDayChoice.setText(Integer.toString(newVal));
                    editor.putInt("date day", newVal);
                    break;

                case R.id.main_drawer_picker_date_year:
                    drawerYearChoice.setText(Integer.toString(newVal) + '-');
                    editor.putInt("date year", newVal);
                    break;
            }
        }
    }

    //Used the following answer from StackOverflow for guidance: https://stackoverflow.com/a/19168199/8916812
    private void buildUri() {

        Uri baseUri = Uri.parse(MainActivity.GUARDIAN_BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String fromDate = String.format(Locale.US, "%d-%d-%d",
                sharedPreferences.getInt("date year", MainActivity.TODAY_YEAR),
                sharedPreferences.getInt("date month", MainActivity.TODAY_MONTH),
                sharedPreferences.getInt("date day", MainActivity.TODAY_DAY));
        String toDate = String.format(Locale.US, "%d-%d-%d",
                MainActivity.TODAY_YEAR, MainActivity.TODAY_MONTH, MainActivity.TODAY_DAY);

        uriBuilder.appendPath(sharedPreferences.getString("section",
                context.getResources().getString(R.string.drawer_settings_section_alltitles_sci)));
        uriBuilder.appendQueryParameter(
                context.getResources().getString(R.string.guardian_query_item_fromdate), fromDate);
        uriBuilder.appendQueryParameter(
                context.getResources().getString(R.string.guardian_query_item_todate), toDate);
        uriBuilder.appendQueryParameter(
                context.getResources().getString(R.string.guardian_query_item_orderby),
                context.getResources().getString(R.string.guardian_query_item_orderby_old));
        uriBuilder.appendQueryParameter(
                context.getResources().getString(R.string.guardian_query_item_fields),
                context.getResources().getString(R.string.guardian_query_item_fields_body));
        uriBuilder.appendQueryParameter(
                context.getResources().getString(R.string.guardian_query_item_pages),
                context.getResources().getString(R.string.guardian_query_item_pages_15));
        uriBuilder.appendQueryParameter(
                context.getResources().getString(R.string.guardian_query_item_tags),
                context.getResources().getString(R.string.guardian_query_item_tags_author));
        uriBuilder.appendQueryParameter(
                context.getResources().getString(R.string.guardian_query_item_key),
                context.getResources().getString(R.string.guardian_query_item_key_my));

        Log.i("uriBuilder", "URL: " + uriBuilder.build().toString());

        editor.putString("url", uriBuilder.build().toString());
    }

    //Verify that selected date is valid, if not change value
    private void verifyDate() {
        int year = sharedPreferences.getInt("date year", MainActivity.TODAY_YEAR);
        int month = sharedPreferences.getInt("date month", MainActivity.TODAY_MONTH);
        int day = sharedPreferences.getInt("date day", MainActivity.TODAY_DAY);

        if(year == MainActivity.TODAY_YEAR && month > MainActivity.TODAY_MONTH) {
            Toast.makeText(context, "Invalid month", Toast.LENGTH_LONG).show();
            editor.putInt("date month", MainActivity.TODAY_MONTH);
        }
        switch(month) {
            case 2:
                if(day > 28) {
                    Toast.makeText(context,
                            "Invalid day\nOnly registered 28 days for February",
                            Toast.LENGTH_LONG).show();
                    editor.putInt("date day", context.getResources().getInteger(R.integer.settings_day_to_feb));
                }
                break;

            case 4: case 6: case 9: case 11:
                if(day > 30) {
                    Toast.makeText(context,
                            "Invalid day\nOnly 30 days in this month",
                            Toast.LENGTH_LONG).show();
                    editor.putInt("date day", context.getResources().getInteger(R.integer.settings_day_to_less));
                }
                break;
        }
    }

    private String switchSection(int index) {
        String section = "";

        switch(index) {
            case 0:
                section = context.getResources().getString(
                        R.string.drawer_settings_section_alltitles_politics);
                break;
            case 1:
                section = context.getResources().getString(
                        R.string.drawer_settings_section_alltitles_sports);
                break;
            case 2:
                section = context.getResources().getString(
                        R.string.drawer_settings_section_alltitles_tech);
                break;
            case 3:
                section = context.getResources().getString(
                        R.string.drawer_settings_section_alltitles_business);
                break;
            case 4:
                section = context.getResources().getString(
                        R.string.drawer_settings_section_alltitles_sci);
                break;
        }

        return section.toLowerCase();
    }
}
