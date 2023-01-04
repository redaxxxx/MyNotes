package com.example.mynotes.models;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mynotes.data.NoteDatabase;
import com.example.mynotes.data.NoteEntity;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<NoteEntity>> notes;

    public MainViewModel(@NonNull Application application) {
        super(application);
        NoteDatabase mDB = NoteDatabase.getInstance(this.getApplication());
        notes = mDB.noteDao().loadAllNotes();
    }

    public LiveData<List<NoteEntity>> getNotes(){
        return notes;
    }
}
