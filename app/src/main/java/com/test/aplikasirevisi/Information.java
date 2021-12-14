package com.test.aplikasirevisi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Information extends Activity {
    SharedPreferences sharedpreferences;
    TextView name;
    TextView phonenum;
    TextView highest;
    TextView lowest;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String PhoneNum = "phonenumKey";
    public static final String Highest = "highestKey";
    public static final String Lowest = "lowestKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        name = (TextView) findViewById(R.id.etName);
        phonenum = (TextView) findViewById(R.id.etPhoneNum);
        highest = (TextView) findViewById(R.id.etHighest);
        lowest = (TextView) findViewById(R.id.etLowest);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name)) {
            name.setText(sharedpreferences.getString(Name, ""));
        }
        if (sharedpreferences.contains(PhoneNum)) {
            phonenum.setText(sharedpreferences.getString(PhoneNum, ""));
        }
        if (sharedpreferences.contains(Highest)) {
            highest.setText(sharedpreferences.getString(Highest, ""));
        }
        if (sharedpreferences.contains(Lowest)) {
            lowest.setText(sharedpreferences.getString(Lowest, ""));
        }
    }
    public void Save(View view) {
        String n = name.getText().toString();
        String p = phonenum.getText().toString();
        String h = highest.getText().toString();
        String l = lowest.getText().toString();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Name, n);
        editor.putString(PhoneNum, p);
        editor.putString(Highest, h);
        editor.putString(Lowest, l);
        editor.apply();
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }
    public void clear(View view) {
        name.setText("");
        phonenum.setText("");
        highest.setText("");
        lowest.setText("");
    }
    public void Get(View view) {
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name)) {
            name.setText(sharedpreferences.getString(Name, ""));
        }
        if (sharedpreferences.contains(PhoneNum)) {
            phonenum.setText(sharedpreferences.getString(PhoneNum, ""));
        }
        if (sharedpreferences.contains(Highest)) {
            highest.setText(sharedpreferences.getString(Highest, ""));
        }
        if (sharedpreferences.contains(Lowest)) {
            lowest.setText(sharedpreferences.getString(Lowest, ""));
        }
    }

}
