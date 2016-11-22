package com.androidsample.contentprovidersample;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.data_list);
        final EditText editText = (EditText) findViewById(R.id.name_et);

        final ContentResolver contentResolver = getContentResolver();
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().length() <= 0) return;

                ContentValues contentValues = new ContentValues();
                contentValues.put("name", editText.getText().toString());
                contentResolver.insert(Uri.parse("content://com.androidsample.contentprovidersample/names/"), contentValues);

                Cursor cursor = contentResolver.query(Uri.parse("content://com.androidsample.contentprovidersample/names/"),
                        new String[]{"name", "_id"},
                        null, null, "name ASC");
                mAdapter.swapCursor(cursor);

                editText.setText("");
            }
        });

        getContentResolver().
                registerContentObserver(
                        Uri.parse("content://com.androidsample.contentprovidersample/names/"),
                        true,
                        new MyObserver());

        mAdapter = new SimpleCursorAdapter(MainActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                contentResolver.query(Uri.parse("content://com.androidsample.contentprovidersample/names/"), new String[]{"name", "_id"}, null, null, "name ASC"),
                new String[]{"name"},
                new int[]{android.R.id.text1});
        listView.setAdapter(mAdapter);
    }

    class MyObserver extends ContentObserver {
        MyObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
        }
    }
}
