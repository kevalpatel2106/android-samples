package example.calandersync.activity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import example.calandersync.R;
import example.calandersync.pojo.EventPojo;
import example.calandersync.utils.DividerItemDecorator;

/**
 * This activity will display how to load and display events for the given calender.
 */
public class EventListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        setRecyclerView();
    }

    private void setRecyclerView() {
        RecyclerView eventList = (RecyclerView) findViewById(R.id.event_list);
        eventList.setItemAnimator(new DefaultItemAnimator());
        eventList.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.line_divider));
        eventList.addItemDecoration(dividerItemDecoration);

        eventList.setAdapter(new RecyclerViewAdapter(getCalenderEvents()));
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
        private ArrayList<EventPojo> mData;

        public RecyclerViewAdapter(ArrayList<EventPojo> datas) {
            mData = datas;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(EventListActivity.this).inflate(android.R.layout.two_line_list_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            EventPojo eventPojo = mData.get(position);
            holder.titleTv.setText(eventPojo.getTitle());
            holder.descriptionTv.setText(eventPojo.getDescription());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView titleTv;
            private TextView descriptionTv;

            public MyViewHolder(View itemView) {
                super(itemView);
                titleTv = (TextView) itemView.findViewById(android.R.id.text1);
                descriptionTv = (TextView) itemView.findViewById(android.R.id.text2);
            }
        }
    }

    private ArrayList<EventPojo> getCalenderEvents() {
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.SECOND, 0);
        ContentUris.appendId(eventsUriBuilder, calendar.getTimeInMillis());
        ContentUris.appendId(eventsUriBuilder, calendar.getTimeInMillis() + 86400000);
        Uri eventsUri = eventsUriBuilder.build();
        Log.d("events uri",eventsUri + "");
        final String[] columns = new String[]{
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION
        };
        Cursor calCursor = getContentResolver().query(eventsUri, columns, null, null, CalendarContract.Instances.DTSTART + " ASC");

        ArrayList<EventPojo> eventPojos = new ArrayList<>();
        if (calCursor != null) {
            if (calCursor.moveToFirst()) {
                do {
                    EventPojo calenderPojo = new EventPojo();
                    calenderPojo.setTitle(calCursor.getString(0));
                    calenderPojo.setDescription(calCursor.getString(1));
                    eventPojos.add(calenderPojo);

                } while (calCursor.moveToNext());
            }
            calCursor.close();
        }
        return eventPojos;
    }
}
