package com.example.mynotes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY modified_at DESC")
    LiveData<List<NoteEntity>> loadAllNotes();

    @Query("SELECT * FROM notes WHERE id = :id")
    LiveData<NoteEntity> loadNoteById(int id);

    @Insert
    void insertNote(NoteEntity noteEntity);

    @Update
    void updateNote(NoteEntity noteEntity);

    @Delete
    void deleteNote(NoteEntity noteEntity);



}
