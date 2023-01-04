package com.example.mynotes.models;

import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mynotes.data.NoteDatabase;
import com.example.mynotes.data.NoteEntity;

public class AddNoteViewModel extends ViewModel {

    private LiveData<NoteEntity> note;

    public AddNoteViewModel(NoteDatabase noteDatabase, int taskId){
        note = noteDatabase.noteDao().loadNoteById(taskId);
    }

    public LiveData<NoteEntity> getNote(){
        return note;
    }
}
