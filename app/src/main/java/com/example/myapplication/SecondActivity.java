package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SecondActivity extends AppCompatActivity {

    Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        final String[] space={"회사","학교","추가"};

        spin=(Spinner) findViewById(R.id.spin);

        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                space);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(space[position].equals("추가")){
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);//지도액티비티 오픈
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }
}