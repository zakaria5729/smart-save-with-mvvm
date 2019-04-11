package com.zakariahossain.mvvmtodoapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zakariahossain.mvvmtodoapp.R;
import com.zakariahossain.mvvmtodoapp.adapters.NoteAdapter;
import com.zakariahossain.mvvmtodoapp.models.Note;
import com.zakariahossain.mvvmtodoapp.viewmodels.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_NOTE_REQUEST_CODE = 123;
    private static final int EDIT_NOTE_REQUEST_CODE = 456;
    private NoteViewModel noteViewModel;
    private NoteAdapter noteAdapter;
    private RecyclerView noteRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteRecyclerView = findViewById(R.id.rcvNote);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteRecyclerView.setHasFixedSize(true);

        noteAdapter = new NoteAdapter();
        noteRecyclerView.setAdapter(noteAdapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.submitList(notes);
            }
        });

        findViewById(R.id.fabAddNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(noteRecyclerView);

        noteAdapter.setMyOnItemClickListener(new NoteAdapter.MyOnItemClickListener() {
            @Override
            public void myOnItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.KEY_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.KEY_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.KEY_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.KEY_PRIORITY, note.getPriority());
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                noteViewModel.insert(new Note(data.getStringExtra(AddEditNoteActivity.KEY_TITLE), data.getStringExtra(AddEditNoteActivity.KEY_DESCRIPTION), data.getIntExtra(AddEditNoteActivity.KEY_PRIORITY, 1)));
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note not added", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getIntExtra(AddEditNoteActivity.KEY_ID, -1) != -1) {
                Note updatedNote = new Note(data.getStringExtra(AddEditNoteActivity.KEY_TITLE), data.getStringExtra(AddEditNoteActivity.KEY_DESCRIPTION), data.getIntExtra(AddEditNoteActivity.KEY_PRIORITY, 1));
                updatedNote.setId(data.getIntExtra(AddEditNoteActivity.KEY_ID, 1));
                noteViewModel.update(updatedNote);
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note can not be updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menu_delete_all).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_all:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "all notes deleted", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
