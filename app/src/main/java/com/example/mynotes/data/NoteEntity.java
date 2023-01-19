package com.example.mynotes.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

@Entity(tableName = "notes")
public class NoteEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;



    @ColumnInfo(name = "modified_at")
    private Date modifiedAt;

    @Ignore
    public NoteEntity(String description, Date modifiedAt){
        this.description = description;
        this.modifiedAt = modifiedAt;
    }

    public NoteEntity(int id, String description, Date modifiedAt){
        this.id = id;
        this.description = description;
        this.modifiedAt = modifiedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

}
