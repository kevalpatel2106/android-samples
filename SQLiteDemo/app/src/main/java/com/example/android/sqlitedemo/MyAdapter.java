package com.example.android.sqlitedemo;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sqlitedemo.Classes.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shiv Kumar Aggarwal on 25-11-2017.
 */


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CardHolder> {
    Context context;
    List<Data> cardArrayList;

    public MyAdapter(Context context, List<Data> cardArrayList) {
        this.context = context;
        this.cardArrayList = cardArrayList;

    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardHolder(LayoutInflater.from(context).inflate(R.layout.single_row,parent,false));
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        final Data card=cardArrayList.get(position);
        holder.namedisp.setText(card.getName());
        holder.datedisp.setText(card.getDoj());
        holder.perdisp.setText((String.valueOf(card.getPercentage())));
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cardArrayList.size();
    }


    class CardHolder extends RecyclerView.ViewHolder{
        CardView cardView;

        TextView namedisp,datedisp,perdisp;
        public CardHolder(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardView);
            namedisp=itemView.findViewById(R.id.namedisp);
            datedisp=itemView.findViewById(R.id.datedisp);
            perdisp=itemView.findViewById(R.id.perdisp);
        }
    }
}