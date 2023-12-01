package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends BaseActivity {

    private ImageButton backButton;
    private Spinner languageSpinner;
    private boolean userHasInteracted = false;
    private Switch darkModeToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUiMode();
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).hide();

        languageSpinner = findViewById(R.id.language_spinner);
        darkModeToggle = findViewById(R.id.darkModeToggle);
        backButton = findViewById(R.id.backButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "English");
        int spinnerPosition = adapter.getPosition(language);
        languageSpinner.setSelection(spinnerPosition, false);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (userHasInteracted) {
                    String selectedLanguage = parent.getItemAtPosition(position).toString();
                    changeAppLanguage(selectedLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        boolean isDarkMode = prefs.getBoolean("DarkMode", false);

        darkModeToggle.setOnCheckedChangeListener(null);

        darkModeToggle.setChecked(isDarkMode);
        darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    changeUiMode(isChecked);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });
    }

    private void changeUiMode(boolean isDarkMode) {
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if ((isDarkMode && currentMode != AppCompatDelegate.MODE_NIGHT_YES) ||
                (!isDarkMode && currentMode != AppCompatDelegate.MODE_NIGHT_NO)) {

            SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("DarkMode", isDarkMode);
            editor.apply();

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    private void loadUiMode() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("DarkMode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userHasInteracted = true;
    }


    private void changeAppLanguage(String language) {
        Locale locale;
        switch (language) {
            case "Français":
                locale = new Locale("fr");
                break;
            case "English":
            default:
                locale = Locale.ENGLISH;
                break;
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", language);
        editor.apply();

        refreshUI();
    }

    private void refreshUI() {
        finish();
        startActivity(getIntent());
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}