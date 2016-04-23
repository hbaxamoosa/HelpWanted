package com.baxamoosa.helpwanted.ui;

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
    private EditText phone;
    private EditText address;
    private EditText website;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Job Post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.edit_business_name);
        phone = (EditText) findViewById(R.id.edit_business_phone);
        address = (EditText) findViewById(R.id.edit_business_address);
        website = (EditText) findViewById(R.id.edit_business_website);
        date = new Date();
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
