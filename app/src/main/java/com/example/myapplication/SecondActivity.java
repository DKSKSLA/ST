package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    Button addbutton1;//완료버튼
    EditText title,memo;//제목 내용
    TimePicker time;//시간선택
    Spinner spin;//장소선택
    String t;//바뀐시간 담아둘 스트링

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        final String[] space={"추가안함","추가"};

        addbutton1=(Button)findViewById(R.id.addbutton1);
        title=findViewById(R.id.title);
        time=findViewById(R.id.time);
        memo=findViewById(R.id.memo);
        spin=(Spinner) findViewById(R.id.space);


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


        //시간선택
        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String t = hourOfDay + " : " + minute;
            }
        });

        //내용전송
        addbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("time",t);
                intent.putExtra("memo",memo.getText().toString());
                intent.putExtra("space",spin.getSelectedItem().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
}