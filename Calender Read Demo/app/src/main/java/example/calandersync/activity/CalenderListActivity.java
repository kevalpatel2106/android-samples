package example.calandersync.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import example.calandersync.utils.DividerItemDecorator;
import example.calandersync.R;
import example.calandersync.pojo.CalenderPojo;

/**
 * This activity will display how to display all the available calenders.
 */
public class CalenderListActivity extends AppCompatActivity {

    private static final int REQUEST_CALENDER_PERMISSION = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check for the calender permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_CALENDER_PERMISSION);
        } else {
            //recycle view
            setRecyclerView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALENDER_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) setRecyclerView();
        }
    }

    /**
     * Set the recycler view to display calender list
     */
    public void setRecyclerView() {
        RecyclerView calenderList = (RecyclerView) findViewById(R.id.recycler_view);
        calenderList.setLayoutManager(new LinearLayoutManager(this));
        calenderList.setHasFixedSize(true);
        calenderList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.line_divider));
        calenderList.addItemDecoration(dividerItemDecoration);

        calenderList.setAdapter(new CalenderListAdapter(getCalenders()));
    }

    /**
     * Adapter to bind calender list
     */
    private class CalenderListAdapter extends RecyclerView.Adapter<CalenderListAdapter.ViewHolder> {
        private ArrayList<CalenderPojo> mData;

        public CalenderListAdapter(ArrayList<CalenderPojo> data) {
            mData = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(CalenderListActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(mData.get(position).getName());
            holder.textView.setTextColor(mData.get(position).getColor());
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CalenderListActivity.this,EventListActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }
    }

    /**
     * Make a query to {@link CalendarContract} and get all the available calenders.
     *
     * @return list of the calenders.
     */
    private ArrayList<CalenderPojo> getCalenders() {
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_COLOR,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};

        //noinspection MissingPermission
        Cursor calCursor = getContentResolver().
                query(CalendarContract.Calendars.CONTENT_URI,
                        projection,
                        CalendarContract.Calendars.VISIBLE + " = 1",
                        null,
                        CalendarContract.Calendars.NAME + " ASC");

        ArrayList<CalenderPojo> calenderPojos = new ArrayList<>();
        if (calCursor != null) {
            if (calCursor.moveToFirst()) {
                do {
                    CalenderPojo calenderPojo = new CalenderPojo();
                    calenderPojo.setId(calCursor.getLong(0));
                    calenderPojo.setName(calCursor.getString(1));
                    calenderPojo.setDisplayName(calCursor.getString(2));
                    calenderPojo.setColor(calCursor.getInt(3));
                    calenderPojos.add(calenderPojo);

                } while (calCursor.moveToNext());
            }
            calCursor.close();
        }
        return calenderPojos;
    }
}
