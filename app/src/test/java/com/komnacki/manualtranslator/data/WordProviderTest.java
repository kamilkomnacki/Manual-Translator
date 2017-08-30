package com.komnacki.manualtranslator.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.compat.BuildConfig;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.Arrays;

import static com.komnacki.manualtranslator.data.WordDbContract.CONTENT_AUTHORITY;
import static com.komnacki.manualtranslator.data.WordDbContract.WordDbEntry;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 17)
public class WordProviderTest extends ProviderTestCase2 {
    private Uri contentUri;
    private ContentResolver contentResolver;


    public WordProviderTest() {
        super(WordProvider.class, CONTENT_AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }






    @Before
    public void doBeforeEachTestCase(){
        MockitoAnnotations.initMocks(this);
        WordProvider provider = new WordProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider(CONTENT_AUTHORITY, provider);

        contentResolver = RuntimeEnvironment.application.getContentResolver();
        assertNotNull(contentResolver);

        contentUri = WordDbEntry.CONTENT_URI;
        assertNotNull(contentUri);


    }





    @Test
    public void insert_test() {
        final int expectedSizeOfContentValues = 4;
        int realSizeOfContentValues;

        ContentValues values = new ContentValues();
        values.put(WordDbEntry.COLUMN_WORD_NAME, "Examplename");
        values.put(WordDbEntry.COLUMN_WORD_TRANSLATION, "ExampleTranslation");
        values.put(WordDbEntry.COLUMN_WORD_CATEGORY, "Category1");
        values.put(WordDbEntry.COLUMN_WORD_LANGUAGE, "language1");


        //if all values pass to ContentValues buffor:
        realSizeOfContentValues = values.size();
        assertEquals(expectedSizeOfContentValues, realSizeOfContentValues);


        //if the operation was finalized:
        Uri resultingUri = contentResolver.insert(contentUri, values);
        assertNotNull(resultingUri);


        //if the insert method put correctly row:
        long id = ContentUris.parseId(resultingUri);
        assertTrue(id > 0);
    }

    @Test
    public void query_test(){
        final int expectedSizeOfCursor = 1;
        int realSizeOfCursor;
        String[] projection = {
                WordDbEntry._ID,
                WordDbEntry.COLUMN_WORD_NAME,
                WordDbEntry.COLUMN_WORD_TRANSLATION};
        final String WORD_NAME = "Examplename";
        final String WORD_TRANSLATION = "Exampletranslation";


        ContentValues values = new ContentValues();
        values.put(WordDbEntry.COLUMN_WORD_NAME, WORD_NAME);
        values.put(WordDbEntry.COLUMN_WORD_TRANSLATION, WORD_TRANSLATION);
        values.put(WordDbEntry.COLUMN_WORD_CATEGORY, "Category1");
        values.put(WordDbEntry.COLUMN_WORD_LANGUAGE, "language1");
        Uri resultingUri = contentResolver.insert(contentUri, values);


        Cursor resultingCursor = contentResolver.query(contentUri, projection, null, null, null);


        //if the cursor is empty:
        assertTrue(resultingCursor.moveToFirst());


        //if the size of cursor and amount input rows is the same:
        realSizeOfCursor = resultingCursor.getCount();
        assertEquals(expectedSizeOfCursor, realSizeOfCursor);


        //if columns name are the same as in parameters:
        assertEquals(Arrays.toString(projection), Arrays.toString(resultingCursor.getColumnNames()));


        //if the input word name and translation is the same:
        int nameColumnIndex = resultingCursor.getColumnIndex(WordDbEntry.COLUMN_WORD_NAME);
        int translationColumnIndex = resultingCursor.getColumnIndex(WordDbEntry.COLUMN_WORD_TRANSLATION);

        String realWordName = resultingCursor.getString(nameColumnIndex);
        String realTranslationName = resultingCursor.getString(translationColumnIndex);
        assertEquals(WORD_NAME, realWordName);
        assertEquals(WORD_TRANSLATION, realTranslationName);
    }


//    @Test
//    public void getType_test() {
//        final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        final int WORDS = 100;
//        final int WORD_ID = 101;
//
//        sUriMatcher.addURI(WordDbContract.CONTENT_AUTHORITY, WordDbContract.PATH, WORDS);
//        sUriMatcher.addURI(WordDbContract.CONTENT_AUTHORITY, WordDbContract.PATH + "/#", WORD_ID);
//
//        final int match = sUriMatcher.match(uri);
//    }



}
