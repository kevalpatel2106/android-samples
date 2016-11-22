package com.androidsample.contentresolversample;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1232;
    public static final String ARG_NAME_ID = "ARG_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getIntent().getLongExtra(ARG_NAME_ID, -1) < 0) {
            Toast.makeText(this, "Missing arguments.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse("content://com.androidsample.contentprovidersample/names/" + getIntent().getLongExtra(ARG_NAME_ID, -1)),
                new String[]{"name", "_id"}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        TextView nameTv = (TextView) findViewById(R.id.name_tv);
        nameTv.setText(data.getString(data.getColumnIndex("name")));

        TextView idTv = (TextView) findViewById(R.id.id_tv);
        idTv.setText(data.getLong(data.getColumnIndex("_id")) + "");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        finish();
    }
}
