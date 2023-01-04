package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mynotes.data.NoteDatabase;
import com.example.mynotes.data.NoteEntity;
import com.example.mynotes.executor.AppExecutors;
import com.example.mynotes.models.AddNoteViewModel;
import com.example.mynotes.models.AddNoteViewModelFactory;

import java.util.Date;

public class AddNotesActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddNotesActivity.class.getSimpleName();
    public static final String EXTRA_NOTE_ID = "task_id";
    private EditText noteEditText;
    private Button addButton;
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
        setTitle("ADD NOTE");
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
            setTitle("UPDATE NOTE");
            addButton.setText(R.string.update_note);
            if (noteId == DEFAULT_NOTE_ID) {
                noteId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_NOTE_ID);
                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(mDB, noteId);
                AddNoteViewModel viewModel = ViewModelProviders.of(this, factory).get(AddNoteViewModel.class);
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
        noteEditText = (EditText) findViewById(R.id.edit_text_notes_description);
        addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (!noteHasChange) {
                    // if note not has change, navigate up to parent activity
                    NavUtils.navigateUpFromSameTask(AddNotesActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(AddNotesActivity.this);
                    }
                };

                showUnSavedChangesDialog(dialogInterface);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!noteHasChange) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnSavedChangesDialog(discardButtonClickListener);
    }

    private void showUnSavedChangesDialog(DialogInterface.OnClickListener dialogInterface) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_your_changes_and_quit_editing);
        builder.setPositiveButton(R.string.discard, dialogInterface);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                if (dialogInterface == null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}