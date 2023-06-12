package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final TextView dayOfMonth;
    private final  CalendarAdapter.OnItemListener onItemListener;
    int loc[]=new int[2];
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText() );
        view.getLocationOnScreen(loc);
        MainActivity.circle.setX(loc[0]+40);
        MainActivity.circle.setY(loc[1]-50);
        MainActivity.circle.setVisibility(View.VISIBLE);
    }
}