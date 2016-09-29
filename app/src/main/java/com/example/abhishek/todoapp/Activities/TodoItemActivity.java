package com.example.abhishek.todoapp.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.abhishek.todoapp.Helpers.DatabaseHandler;
import com.example.abhishek.todoapp.Models.TodoItem;
import com.example.abhishek.todoapp.Models.TodoItem;
import com.example.abhishek.todoapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TodoItem Activity will perform add and update todoItem
 */
public class TodoItemActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private String action, prioritySel;
    private long todoId;
    private EditText todoName, todoNote, todoDate;
    private Spinner todoPrioritySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item);

        db = new DatabaseHandler(this);
        todoName = (EditText) findViewById(R.id.todo_name_et);
        todoNote = (EditText) findViewById(R.id.todo_note_et);
        todoDate = (EditText) findViewById(R.id.todo_date_et);
        todoPrioritySpinner = (Spinner) findViewById(R.id.todo_priority_spinner);

        //load date picker dialog
        loadTodoDatePicker();

        // load priority spinner
        loadTodoPrioritySpinner();

        // perform add/edit operation depending on the action
        executeAddEditAction();

    }


    /**
     * Date Picker Fragment
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            try {

                int mMonth = month + 1;

                String todoDueDateStr = day+"/"+mMonth+"/"+year;

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date todoDueDate = sdf.parse(todoDueDateStr);

                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String todoDueDateStrFormatted = outputFormat.format(todoDueDate);

                ((EditText) getActivity().findViewById(R.id.todo_date_et)).setText(todoDueDateStrFormatted);

            }  catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load Date Picker Dialog Fragment
     */
    private void loadTodoDatePicker() {

        todoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }


    /**
     * Store the action value from the parent activity
     * Perform the add or edit operation on the todoItem
     */
    private void executeAddEditAction() {

        // store the action item add/edit
        action = getIntent().getStringExtra("ADDEDITKEY");

        if(action.equals("Add")) {

            // Add Mode
            // Perform operation when the user in add action

        } else {

            // Edit Mode
            todoId = getIntent().getLongExtra("TODOID", 0);
            TodoItem todoItem = db.getTodoItemById((int) todoId);
            todoName.setText(todoItem.getTodoName());
            todoNote.setText(todoItem.getTodoNote());
            todoPrioritySpinner.setSelection(getSpinnerIndexbyValue(todoItem.getTodoPriority()));
            todoDate.setText(todoItem.getTodoDueDate());
        }

    }


    /**
     * Loading the TodoPriority Spinner with the choices
     */
    private void loadTodoPrioritySpinner() {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.todo_priority_choices, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        todoPrioritySpinner.setAdapter(spinnerAdapter);


        todoPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prioritySel = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * Get Spinner Index from the value
     * @param priorityValue
     * @return
     */
    private int getSpinnerIndexbyValue(String priorityValue){

        int index = 0;

        for (int i=0;i<todoPrioritySpinner.getCount();i++){
            if (todoPrioritySpinner.getItemAtPosition(i).equals(priorityValue)){
                index = i;
            }
        }
        return index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {

            case R.id.action_save:

                if(action.equals("Add")) {

                    // Adding todoItem
                    addTodoItem();

                } else {

                    // Editing todoItem
                    editTodoItem();
                }

                break;

            case R.id.action_cancel:

                // This will take user back to the previous activity
                super.onBackPressed();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    // Todo : Needs to refactor the add and edit todoItem method into one

    /**
     * Add todoItem
     */
    private void addTodoItem() {

        String name     = todoName.getText().toString();
        String note     = todoNote.getText().toString();
        String priority = prioritySel;
        String dueDate  = todoDate.getText().toString();

        if(name.length() > 0) {

            // Add todoItem in the DB table
            long insertId = db.addTodoItem(new TodoItem(name,note,priority,dueDate));

            if(insertId!=-1) {

                // Toast Message
                Toast.makeText(this, R.string.add_todo_success_msg, Toast.LENGTH_SHORT).show();

                // Prepare data intent
                Intent data = new Intent();
                data.putExtra("todoId", insertId);
                data.putExtra("todoName", name);
                data.putExtra("todoNote", note);
                data.putExtra("todoPriority", priority);
                data.putExtra("todoDueDate", dueDate);

                // Passing back to the previous activity
                setResult(RESULT_OK, data);
                finish();

            } else {

                // Error
                Toast.makeText(this, R.string.add_todo_error_msg, Toast.LENGTH_SHORT).show();
            }

        } else {

            // Validation Error
            Toast.makeText(this, R.string.todo_name_required_msg, Toast.LENGTH_SHORT).show();

        }
    }


    /**
     * Edit todoItem
     */
    private void editTodoItem() {


        String name     = todoName.getText().toString();
        String note     = todoNote.getText().toString();
        String priority = prioritySel;
        String dueDate  = todoDate.getText().toString();

        if(name.length() > 0) {

            // update todoItem in the DB table
            boolean success = db.updateTodoItem(new TodoItem(todoId,name,note,priority,dueDate));

            if(success) {

                Toast.makeText(this, R.string.edit_todo_success_msg, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();

            } else {

                // Error
                Toast.makeText(this, R.string.edit_todo_error_msg, Toast.LENGTH_SHORT).show();
            }

        } else {

            // Validation Error
            Toast.makeText(this, R.string.todo_name_required_msg, Toast.LENGTH_SHORT).show();
        }
    }

}
