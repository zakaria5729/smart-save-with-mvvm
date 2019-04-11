package com.zakariahossain.mvvmtodoapp.databases;

import android.content.Context;
import android.os.AsyncTask;

import com.zakariahossain.mvvmtodoapp.models.NoteDao;
import com.zakariahossain.mvvmtodoapp.models.Note;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Note.class, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase noteDatabaseInstance;
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getNoteDatabaseInstance(Context context) {
        if (noteDatabaseInstance == null) {
            noteDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return noteDatabaseInstance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(noteDatabaseInstance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        PopulateDbAsyncTask(NoteDatabase noteDatabase) {
            this.noteDao = noteDatabase.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("title1", "description1", 1));
            noteDao.insert(new Note("title2", "description2", 2));
            noteDao.insert(new Note("title3", "description3", 3));
            return null;
        }
    }
}
