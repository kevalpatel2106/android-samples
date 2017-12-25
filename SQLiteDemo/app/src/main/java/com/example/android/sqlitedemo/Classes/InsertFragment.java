package com.example.android.sqlitedemo.Classes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.sqlitedemo.R;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Nishita Aggarwal on 25-11-2017.
 */

public class InsertFragment extends Fragment {
    EditText edittext_name,percentage;
    CalendarView cal_doj;
    DataBase database;
    String data_date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.insert_fragment,container,false);
        FloatingActionButton fab = v.findViewById(R.id.fab);
        edittext_name=v.findViewById(R.id.edittext_name);
        percentage=v.findViewById(R.id.percentage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data d=new Data();
                d.setName(edittext_name.getText().toString());
                d.setPercentage(Float.parseFloat(percentage.getText().toString()));
                d.setDoj(data_date);
                database.addData(d);
                Snackbar.make(view, "Data added succesfully in database", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database=new DataBase(this.getContext());
        cal_doj=getActivity().findViewById(R.id.calview_doj);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        data_date = sdf.format(new Date(cal_doj.getDate()));
        cal_doj.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
                if(month==12)
                    month=1;
                else
                    month=month+1;
                data_date=date + "/" + month + "/" + year;
                Toast.makeText(getContext(), date + "/" + month + "/" + year , Toast.LENGTH_SHORT).show();
            }

        });
    }
}
