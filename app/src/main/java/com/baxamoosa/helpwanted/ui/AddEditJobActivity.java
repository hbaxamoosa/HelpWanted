package com.baxamoosa.helpwanted.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.helpwanted.R;

import java.util.Date;

import timber.log.Timber;

public class AddEditJobActivity extends AppCompatActivity {

    private TextView name;
    private TextView phone;
    private TextView address;
    private TextView website;
    private EditText wageRate;
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

        name = (TextView) findViewById(R.id.business_name);
        phone = (TextView) findViewById(R.id.business_phone);
        address = (TextView) findViewById(R.id.business_address);
        website = (TextView) findViewById(R.id.business_website);
        wageRate = (EditText) findViewById(R.id.edit_business_wage_rate);
        date = new Date();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        /*Timber.v("get value from SharePref: " + sharedPref.getString(String.valueOf(R.string.person_name), "no name available"));
        name.setText(sharedPref.getString(String.valueOf(R.string.person_name), "no name available"));*/

        if (getIntent().hasExtra(String.valueOf(R.string.business_name))) {
            name.setText(getIntent().getStringExtra(String.valueOf(R.string.business_name)));
        }
        if (getIntent().hasExtra(String.valueOf(R.string.business_phone))) {
            phone.setText(getIntent().getStringExtra(String.valueOf(R.string.business_phone)));
        }
        if (getIntent().hasExtra(String.valueOf(R.string.business_address))) {
            address.setText(getIntent().getStringExtra(String.valueOf(R.string.business_address)));
        }
        if (getIntent().hasExtra(String.valueOf(R.string.business_website))) {
            website.setText(getIntent().getStringExtra(String.valueOf(R.string.business_website)));
        }
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
