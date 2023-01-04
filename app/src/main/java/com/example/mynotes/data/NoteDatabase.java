package com.example.mynotes.data;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {NoteEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class NoteDatabase extends RoomDatabase {

    private static final String LOG_TAG = NoteDatabase.class.getSimpleName();
    private static  final Object LOCK = new Object();
    private static final String DATABASE_NAME = "note.db";
    private static NoteDatabase instance;

    public static NoteDatabase getInstance(Context context) {
        if (instance == null){
            synchronized (LOCK){
                Log.v(LOG_TAG, "Creating database ");
                instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, NoteDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.v(LOG_TAG, "Getting database instance");
        return instance;
    }

    public abstract NoteDao noteDao();
}
