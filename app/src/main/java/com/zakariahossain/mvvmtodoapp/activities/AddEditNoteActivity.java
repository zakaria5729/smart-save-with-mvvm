package com.zakariahossain.mvvmtodoapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;

import com.google.android.material.textfield.TextInputLayout;
import com.zakariahossain.mvvmtodoapp.R;

import java.util.Objects;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";
    public static final String KEY_PRIORITY = "KEY_PRIORITY";

    private TextInputLayout titleInput, descriptionInput;
    private NumberPicker priorityPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleInput = findViewById(R.id.tilTitle);
        descriptionInput = findViewById(R.id.tilDescription);
        priorityPicker= findViewById(R.id.npPriority);

        priorityPicker.setMinValue(1);
        priorityPicker.setMaxValue(10);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        if (getIntent().hasExtra(KEY_ID)) {
            setTitle("Edit Note");
            Objects.requireNonNull(titleInput.getEditText()).setText(getIntent().getStringExtra(KEY_TITLE));
            Objects.requireNonNull(descriptionInput.getEditText()).setText(getIntent().getStringExtra(KEY_DESCRIPTION));
            priorityPicker.setValue(getIntent().getIntExtra(KEY_PRIORITY, 1));
        } else {
            setTitle("Add Note");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menu_save).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String title = Objects.requireNonNull(titleInput.getEditText()).getText().toString();
        String description = Objects.requireNonNull(descriptionInput.getEditText()).getText().toString();
        int priority = priorityPicker.getValue();

        if (!TextUtils.isEmpty(title.trim()) && !TextUtils.isEmpty(description.trim())) {
            Intent data = new Intent();

            data.putExtra(KEY_TITLE, title);
            data.putExtra(KEY_DESCRIPTION, description);
            data.putExtra(KEY_PRIORITY, priority);
            data.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            if (getIntent().getIntExtra(KEY_ID, -1) != -1) {
                data.putExtra(KEY_ID, getIntent().getIntExtra(KEY_ID, 1));
            }

            setResult(RESULT_OK, data);
            finish();
        } else {
            if (TextUtils.isEmpty(title.trim())) {
                titleInput.setError("Title can not be empty");
            }
            if (TextUtils.isEmpty(description.trim())) {
                descriptionInput.setError("Description can not be empty");
            }
        }
    }
}
