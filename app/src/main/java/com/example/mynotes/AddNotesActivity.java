package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.mynotes.data.NoteDatabase;
import com.example.mynotes.data.NoteEntity;
import com.example.mynotes.executor.AppExecutors;
import com.example.mynotes.models.AddNoteViewModel;
import com.example.mynotes.models.AddNoteViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNotesActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddNotesActivity.class.getSimpleName();
    public static final String EXTRA_NOTE_ID = "task_id";
    private EditText noteEditText;
    private EditText titleEditText;
    private MaterialToolbar topBar;
    private static final int DEFAULT_NOTE_ID = -1;
    private int noteId = DEFAULT_NOTE_ID;
    private static final String INSTANCE_NOTE_ID = "instanceNoteId";
    private NoteDatabase mDB;

    private boolean noteHasChange = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            noteHasChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        initView();
        noteEditText.setOnTouchListener(touchListener);

        mDB = NoteDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NOTE_ID)) {
            noteId = savedInstanceState.getInt(INSTANCE_NOTE_ID, DEFAULT_NOTE_ID);
        }

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {
            topBar.setTitle("Update note");
            if (noteId == DEFAULT_NOTE_ID) {
                noteId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_NOTE_ID);
                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(mDB, noteId);
                AddNoteViewModel viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) factory)
                        .get(AddNoteViewModel.class);
                viewModel.getNote().observe(this, new Observer<NoteEntity>() {
                    @Override
                    public void onChanged(NoteEntity noteEntity) {
                        populateUI(noteEntity);
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_NOTE_ID, noteId);
        super.onSaveInstanceState(outState);
    }

    private void onSaveButton() {
        String description = noteEditText.getText().toString();
        Date modify_at = new Date();
        final NoteEntity note = new NoteEntity(description, modify_at);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (noteId == DEFAULT_NOTE_ID) {
                    mDB.noteDao().insertNote(note);
                } else {
                    note.setId(noteId);
                    mDB.noteDao().updateNote(note);
                }
                finish();
            }
        });
    }

    private void initView() {
        titleEditText = (EditText) findViewById(R.id.title_of_note);
        noteEditText = (EditText) findViewById(R.id.edit_text_notes_description);

        topBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(topBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        topBar.setTitle("Add Note");
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButton();
            }
        });

    }

    private void populateUI(NoteEntity noteEntity) {
        if (noteEntity == null) {
            return;
        }
        noteEditText.setText(noteEntity.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (noteId == DEFAULT_NOTE_ID){
            menu.findItem(R.id.delete_note).setVisible(false);
        }else {
            menu.findItem(R.id.delete_note).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_note:
                showDeleteConfirmation();
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Note");
        builder.setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDB.noteDao().deleteById(noteId);
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}