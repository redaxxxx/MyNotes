package com.example.mynotes.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mynotes.data.NoteDatabase;
import com.example.mynotes.data.NoteEntity;

public class AddNoteViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final NoteDatabase noteDatabase;
    private final int mNoteId;

    public AddNoteViewModelFactory(NoteDatabase database, int noteId ){
        noteDatabase = database;
        mNoteId = noteId;
    }

    public <T extends ViewModel> T create(Class<T> modelClasses){
        return (T) new AddNoteViewModel(noteDatabase, mNoteId);
    }
}
