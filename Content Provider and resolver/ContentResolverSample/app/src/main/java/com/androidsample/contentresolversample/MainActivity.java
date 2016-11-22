package com.androidsample.contentresolversample;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * This activity displays how to perform search using content resolver on our com.androidsample.contentprovidersample content
 * provider and also bind the cursor with the list view to the list view to display results using SimpleCursorAdapter.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1239;

    private EditText mEditText;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the list view
        ListView listView = (ListView) findViewById(R.id.data_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Cursor cursor = (Cursor) mAdapter.getItem(pos);

                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra(DetailActivity.ARG_NAME_ID,cursor.getLong(cursor.getColumnIndex("_id")));
                startActivity(intent);
            }
        });
        //Bind the cursor using SimpleCursorAdapter to the list view.
        mAdapter = new SimpleCursorAdapter(MainActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                null,
                new String[]{"name"},
                new int[]{android.R.id.text1});
        listView.setAdapter(mAdapter);
        mEditText = (EditText) findViewById(R.id.name_et);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditText.getText().length() <= 0) return;
                search(mEditText);
            }
        });
    }

    private void search(EditText editText) {
        mEditText = editText;
        // Initialize the Loader with id '1239' and callbacks.
        // If the loader doesn't already exist, one is created. Otherwise,
        // the already created Loader is reused. In either case, the
        // LoaderManager will manage the Loader across the Activity/Fragment
        // lifecycle, will receive any new loads once they have completed,
        // and will report this new data back via callbacks.
        LoaderManager lm = getSupportLoaderManager();

        //close any loader that is already running
        lm.destroyLoader(LOADER_ID);

        //init new loader
        lm.initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //create the content resolver query. The syntax for the content uri should be content://authority/names/query.
        //This syntax represents the search operation in com.androidsample.contentprovidersample application.
        return new CursorLoader(this, Uri.parse("content://com.androidsample.contentprovidersample/names/" + mEditText.getText().toString().trim()),
                new String[]{"name", "_id"}, null, null, "name ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID) {
            // The asynchronous load is complete and the data
            // is now available for use. Only now can we associate
            // the queried Cursor with the SimpleCursorAdapter.
            if (mAdapter != null) mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // For whatever reason, the Loader's data is now unavailable.
        // Remove any references to the old data by replacing it with
        // a null Cursor.
        mAdapter.swapCursor(null);
    }
}
