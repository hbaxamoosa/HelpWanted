package com.baxamoosa.helpwanted.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;

import java.util.Date;

import timber.log.Timber;

public class AddEditJobActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText address;
    private EditText website;
    private EditText wageRate;
    private EditText postDate;
    private Date date;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Job Post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.edit_business_name);
        email = (EditText) findViewById(R.id.edit_business_email);
        phone = (EditText) findViewById(R.id.edit_business_phone);
        address = (EditText) findViewById(R.id.edit_business_address);
        website = (EditText) findViewById(R.id.edit_business_website);
        wageRate = (EditText) findViewById(R.id.edit_business_wage_rate);
        postDate = (EditText) findViewById(R.id.edit_business_post_date);
        date = new Date();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Timber.v("get value from SharePref: " + sharedPref.getString(String.valueOf(R.string.person_name), "no name available"));
        name.setText(sharedPref.getString(String.valueOf(R.string.person_name), "no name available"));
        email.setText(sharedPref.getString(String.valueOf(R.string.person_email), "no name available"));
        postDate.setText(date.toString());

    }

    public void submitToDB(View view) {
        Timber.v("submitToDB(View view)");
        Timber.v("date: " + date.getTime());
        Toast.makeText(this, "Name: " + name.getText() + "\n" +
                "Phone: " + phone.getText() + "\n" +
                "Address: " + address.getText() + "\n" +
                "Website: " + website.getText(), Toast.LENGTH_LONG).show();
    }
}
