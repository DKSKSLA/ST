package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter{

    String TAG = "RecyclerViewAdapter";

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<DataModel> dataModels;
    Context context;

    //
    public ItemAdapter(Context context, ArrayList<DataModel> dataModels){
        this.dataModels = dataModels;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기
        return dataModels.size();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");

        //itemview를 inflate 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemview,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);


        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");
        position=holder.getAdapterPosition();
        MyViewHolder myViewHolder = (MyViewHolder)holder;

        myViewHolder.textView1.setText(dataModels.get(position).getText1());
        myViewHolder.textView2.setText(dataModels.get(position).getText2());
        myViewHolder.textView3.setText(dataModels.get(position).getText3());
        myViewHolder.textView4.setText(dataModels.get(position).getText4());

        myViewHolder.textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, holder.getAdapterPosition()+"번째 텍스트 뷰 클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView1,textView2,textView3,textView4;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 =  itemView.findViewById(R.id.textview1);
            textView2 =  itemView.findViewById(R.id.textview2);
            textView3 =  itemView.findViewById(R.id.textview3);
            textView4 =  itemView.findViewById(R.id.textview4);

        }
    }
}


