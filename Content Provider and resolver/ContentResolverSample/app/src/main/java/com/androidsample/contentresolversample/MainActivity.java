package com.androidsample.contentresolversample;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ContentResolver contentResolver = getContentResolver();

        final ListView listView = (ListView) findViewById(R.id.data_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        final EditText editText = (EditText) findViewById(R.id.name_et);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().length() <= 0) return;

                contentResolver.query(Uri.parse("content://com.androidsample.contentprovidersample/names/" + editText.getText().toString().trim()),
                        new String[]{"name", "_id"},
                        null,
                        null,
                        "name ASC");

                mAdapter = new SimpleCursorAdapter(MainActivity.this,
                        android.R.layout.simple_expandable_list_item_1,
                        contentResolver.query(Uri.parse("content://com.androidsample.contentprovidersample/names/"), new String[]{"name", "_id"}, null, null, "name ASC"),
                        new String[]{"name"},
                        new int[]{android.R.id.text1});
                listView.setAdapter(mAdapter);
            }
        });

        mAdapter = new SimpleCursorAdapter(MainActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                contentResolver.query(Uri.parse("content://com.androidsample.contentprovidersample/names/"), new String[]{"name", "_id"}, null, null, "name ASC"),
                new String[]{"name"},
                new int[]{android.R.id.text1});
        listView.setAdapter(mAdapter);
    }
}
