package com.lirancaduri.secendfire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lirancaduri.secendfire.R;
import com.lirancaduri.secendfire.data.Shift;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
//

public class ShiftAdapter extends ArrayAdapter<Shift> {

    private List<Shift> shifts;
    private LayoutInflater layoutInflater;


    public ShiftAdapter(Context context, List<Shift> shifts) {
        super(context, R.layout.shift_cell, shifts);
        this.layoutInflater = LayoutInflater.from(context);
        this.shifts = shifts;
    }



    private class ViewHolder {
        TextView tvTime;
        TextView tvDate;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        //אם השורה היא NULL זאת אומרת בחיים לא נעשתה קודם לכן אנחנו עושים INFLATE מנפחים קובץ XML לקלאסים ומכניסים את TEXT-VIEWS
        //לתוך VIEW HOLDER שמחזיק בתוכו את TEXT VIEWS
        //אם השורה לא NULL זאת אומרת שיש בתוכה VIEW HOLDER שהכנסו בפעם הקודמת  אז נקבל אותו

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.shift_cell, parent, false);
            viewHolder.tvTime = convertView.findViewById(R.id.tvTime);
            viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        // בכל מקרה לאחר מכן משנים את הפרטים
        Shift temp = shifts.get(position);
        viewHolder.tvDate.setText(temp.getDate());
        viewHolder.tvTime.setText(new Time(temp.getStart()).toString());
        return convertView;
    }

    @Override
    public int getCount() {
        return shifts.size();
    }
}
