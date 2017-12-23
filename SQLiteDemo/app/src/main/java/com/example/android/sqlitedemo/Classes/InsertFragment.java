package com.example.android.sqlitedemo.Classes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.sqlitedemo.R;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Shiv Kumar Aggarwal on 25-11-2017.
 */

public class InsertFragment extends Fragment {

    Button button_submit;
    EditText edittext_name,percentage;
    CalendarView cal_doj;
    DataBase database;
    Date date_sql;

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
                d.setDoj(String.valueOf(cal_doj.getDate()));
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
        final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        cal_doj=(CalendarView)getActivity().findViewById(R.id.calview_doj);
        cal_doj.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {

                java.util.Date dateutil = new java.util.Date();
                if(month==12)
                    month=1;
                else
                    month=month+1;
                String startDate = date + "-" + month + "-" + year;
                try {
                    dateutil = sdf1.parse(startDate);
                } catch (Exception e) {
                    Log.e("date error==",e.toString());
                }
                Date datesql = new Date(dateutil.getTime());
//                d = datesql;
//                datedatam=d.toString();
//                Log.e("datedatam by cal=",datedatam);
                Toast.makeText(getContext(), date + "/" + month + "/" + year + "\n" + datesql.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
