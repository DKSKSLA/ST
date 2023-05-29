package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// 안드로이드 api 26(Android 8.0 오레오) 미만 버전에서 java.time 패키지 사용하려고 하면 error 발생
// Syntax 에러로 뜨지만 빌드는 되는걸 확인했음
// 하지만 에러때문에 github에 push가 안됨
// 해당 오류 없애려면 'ThreeTen' 백포트를 사용해야함 (java.time. -> org.threeten.bp. 으로 import 해야함)
// 참고 링크 https://scshim.tistory.com/250
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Boolean weekon=false;
    public Button mapButton;
    public Button aaa;
    public FloatingActionButton fab;
    public View L3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ThreeTen 백포트 사용
        AndroidThreeTen.init(this);
        L3=(View) findViewById(R.id.L3);
        ViewGroup.LayoutParams params =L3.getLayoutParams();

        ItemAdapter adapter;
        RecyclerView recyclerView;

        ArrayList<DataModel> dataModels = new ArrayList();

        dataModels.add(new DataModel("대학","2시","모바일캡스톤 발표","내용"));
        dataModels.add(new DataModel("대학","5시","웹프로그래밍시험","내용"));

        recyclerView = findViewById(R.id.listRecyclerView);
        adapter = new ItemAdapter(this,dataModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FirebaseSettingEx", "getInstanceId failed", task.getException());
                        return;
                    }
            String aaa = task.getResult();//자신 기기의 토큰
            System.out.println(aaa+"해당 기기의 토큰");//출력

            FirebaseMessaging.getInstance().subscribeToTopic("1");//푸시 알림을 빨리 보내기 위한 구독기능
            //개인마다 다르게 하기로 설정, 임의의 번호를 부여하는식으로


        });

        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();
        aaa=(Button) findViewById(R.id.aaa);
        aaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!weekon) {

                    params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            150, getResources().getDisplayMetrics());
                    L3.setLayoutParams(params);//L3 크기조절
                    setWeekView();//주로바꾸기
                    weekon= !weekon;//같은버튼 재활용위한 BOOL
                }
                else{
                    params.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            300, getResources().getDisplayMetrics());
                    L3.setLayoutParams(params);//L3 크기조절
                    setMonthView();//달로 바꾸기
                    weekon= !weekon;//같은 버튼 재활용위한 BOOL
                }
            }
        });

        //FAB버튼 설정
        fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(intent);//할일 추가 액티비티로 이동
            }
        });

        // 임시 맵 버튼 동작
        mapButton = (Button) findViewById(R.id.tempMap_btn);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(LocalDate.now()));
        ArrayList<String> daysInMonth = WeekArray(LocalDate.now());

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {

        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);

        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }


    private ArrayList<String> WeekArray(LocalDate date)
    {

        ArrayList<String> daysInMonthArray = new ArrayList<>();//배열

        int today=selectedDate.getDayOfMonth();//현재날짜

        YearMonth yearMonth = YearMonth.from(date);//년도와달을 받기
        int daysInMonth = yearMonth.lengthOfMonth();//년도와달로 해당달최대일수

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);

        int dayOfWeekValue = selectedDate.getDayOfWeek().getValue();

        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int k=0;

        for(int i=1; i<=dayOfWeekValue;i++)
        {
            if(dayOfWeekValue==7){break;}
            int day=today-dayOfWeekValue;

            if(day<0)//저번달로넘어가면 저번달 마지막날부터
            {LocalDate selectedDate2 = selectedDate.minusMonths(1);
                YearMonth yearMonth2 = YearMonth.from(selectedDate2);//년도와달을 받기
                int daysInMonth2 = yearMonth2.lengthOfMonth();//년도와달로 해당달최대일수
                day=daysInMonth2+day;}

            daysInMonthArray.add(String.valueOf(day));
            k++;
        }
        for(int i=0;(i+k)<7;i++){
            int day=today+i;
            if(day>daysInMonth){day=day-daysInMonth+1;}//다음달로넘어가면 1일부터
            daysInMonthArray.add(String.valueOf(day));
        }


        return  daysInMonthArray;
    }



    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view)
    {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    public void onItemClick(int position, String dayText)
    {
        if(!dayText.equals(""))
        {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}