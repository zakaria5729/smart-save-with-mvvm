package com.zakariahossain.mvvmtodoapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zakariahossain.mvvmtodoapp.R;
import com.zakariahossain.mvvmtodoapp.adapters.NoteAdapter;
import com.zakariahossain.mvvmtodoapp.models.Note;
import com.zakariahossain.mvvmtodoapp.viewmodels.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_NOTE_REQUEST_CODE = 123;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView noteRecyclerView = findViewById(R.id.rcvNote);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteRecyclerView.setHasFixedSize(true);

        final NoteAdapter noteAdapter = new NoteAdapter();
        noteRecyclerView.setAdapter(noteAdapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.setNoteList(notes);
            }
        });

        findViewById(R.id.fabAddNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                noteViewModel.insert(new Note(data.getStringExtra(AddNoteActivity.KEY_TITLE), data.getStringExtra(AddNoteActivity.KEY_DESCRIPTION), data.getIntExtra(AddNoteActivity.KEY_PRIORITY, 1)));
            }
        } else {
            Toast.makeText(this, "Note not added", Toast.LENGTH_SHORT).show();
        }
    }
}
