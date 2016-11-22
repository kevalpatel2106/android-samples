package com.androidsample.contentprovidersample.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Keval on 21-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

class NameContract {
    static final String CONTENT_AUTHORITY = "com.androidsample.contentprovidersample";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String NAMES_TABLE = "names";

    static class NamesEntry implements BaseColumns {
        static final String ID = "_id";
        static final String NAME = "name";

        static final Uri CONTENT_URI  = BASE_CONTENT_URI.buildUpon().appendPath(NAMES_TABLE).build();

        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + NAMES_TABLE;

        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + NAMES_TABLE;

        // for building URIs on insertion
        public static Uri buildNameUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
