package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class SecondActivity extends AppCompatActivity {

    Button addbutton1;//완료버튼
    EditText title,memo;//제목 내용
    TimePicker time;//시간선택
    Spinner spin;//장소선택
    String t;//바뀐시간 담아둘 스트링
    TextView textday;
    String day;
    String id;
    SQLiteDatabase db;
    boolean flag=false;
    @RequiresApi(api = Build.VERSION_CODES.M)
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
        textday=findViewById(R.id.textday);

        DBHelper helper;
        helper = new DBHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        day=getIntent().getStringExtra("day");
        textday.setText(day);
        id=getIntent().getStringExtra("id");
        System.out.println("아이디가원멘"+id);

        //수정용으로 받은거면 내용들을 미리 설정해둠
        if(id!=null) {
            flag=true;
            System.out.println("갔냐"+id);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor c = db.rawQuery("SELECT * FROM " + TableInfo.TABLE_NAME + " WHERE " + TableInfo.COLUMN_NAME_ID + " ='" + id + "'", null);
                            while (c.moveToNext()) {
                                int colid = c.getColumnIndex(TableInfo.COLUMN_NAME_ID);

                                colid = c.getColumnIndex(TableInfo.COLUMN_NAME_TIME);
                                textday.setText(c.getString(colid));

                                colid = c.getColumnIndex(TableInfo.COLUMN_NAME_TITLE);
                                title.setText(c.getString(colid));

                                colid = c.getColumnIndex(TableInfo.COLUMN_NAME_MEMO);
                                memo.setText(c.getString(colid));

                            }
                        }
                    });
                }
            }).start();
        }

        //장소 배열어댑터
        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                space);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(space[position].equals("추가")){
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivityForResult(intent, 0); // 지도액티비티 오픈
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
                t = hourOfDay + " : " + minute;
            }
        });

        //내용전송
        addbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("1버어어어언");

                if(flag){//수정할때
                    System.out.println("2버어어어언");

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                    ContentValues values=new ContentValues();
                    values.put(TableInfo.COLUMN_NAME_TITLE,title.getText().toString());
                    values.put(TableInfo.COLUMN_NAME_TIME,t);
                    values.put(TableInfo.COLUMN_NAME_DATE,day);
                    values.put(TableInfo.COLUMN_NAME_MEMO,memo.getText().toString());
                    values.put(TableInfo.COLUMN_NAME_SPACE,spin.getSelectedItem().toString());

                    db.update(TableInfo.TABLE_NAME,values,"ID = ?",new String[] {id});
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 플래그 설정
                    setResult(RESULT_OK, intent); // 결과값 전달 시점과 OK 메시지를 알려줌
                    finish();

                }else{//새로만들때
                    System.out.println("3버어어어언");

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                    ContentValues values=new ContentValues();
                    values.put(TableInfo.COLUMN_NAME_TITLE,title.getText().toString());
                    values.put(TableInfo.COLUMN_NAME_TIME,t);
                    values.put(TableInfo.COLUMN_NAME_DATE,day);
                    values.put(TableInfo.COLUMN_NAME_MEMO,memo.getText().toString());
                    values.put(TableInfo.COLUMN_NAME_SPACE,spin.getSelectedItem().toString());

                    db.insert(TableInfo.TABLE_NAME,null,values);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 플래그 설정
                    setResult(RESULT_OK, intent); // 결과값 전달 시점과 OK 메시지를 알려줌
                    finish();
                }
            }
        });
    }
}