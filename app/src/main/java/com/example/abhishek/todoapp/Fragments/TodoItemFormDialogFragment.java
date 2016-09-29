package com.example.abhishek.todoapp.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abhishek.todoapp.Activities.TodoItemActivity;
import com.example.abhishek.todoapp.Helpers.DatabaseHandler;
import com.example.abhishek.todoapp.Models.TodoItem;
import com.example.abhishek.todoapp.R;

import android.support.v4.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by abhishek on 9/28/16.
 */

public class TodoItemFormDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {


    private EditText todoName, todoNote, todoDate;
    private Spinner todoPrioritySpinner;
    private String prioritySel;
    private long todoId;
    private DatabaseHandler db;

    public TodoItemFormDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface TodoItemFormDialogListener {
        void onFinishEditDialog(String inputText);
    }


    public static TodoItemFormDialogFragment newInstance(TodoItem todoItem) {
        TodoItemFormDialogFragment frag = new TodoItemFormDialogFragment();
        Bundle args = new Bundle();
        args.putString("todoName", todoItem.getTodoName());
        args.putString("todoNote", todoItem.getTodoNote());
        args.putString("todoPriority", todoItem.getTodoPriority());
        args.putString("todoDate", todoItem.getTodoDueDate());
        args.putLong("todoId", todoItem.getTodoId());
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_todo_item, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHandler(getContext());

        // Get field from view
        todoName = (EditText) view.findViewById(R.id.todo_name_et);
        todoNote = (EditText) view.findViewById(R.id.todo_note_et);
        todoDate = (EditText) view.findViewById(R.id.todo_date_et);
        todoPrioritySpinner = (Spinner) view.findViewById(R.id.todo_priority_spinner);

        //load date picker dialog
        loadTodoDatePicker();

        // load priority spinner
        loadTodoPrioritySpinner();

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("todoName");
        getDialog().setTitle(title);

        todoName.setText(getArguments().getString("todoName"));
        todoNote.setText(getArguments().getString("todoNote"));
        todoPrioritySpinner.setSelection(getSpinnerIndexbyValue(getArguments().getString("todoPriority")));
        todoDate.setText(getArguments().getString("todoDate"));
        todoId = getArguments().getLong("todoId");

        // Show soft keyboard automatically and request focus to field
        todoName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        todoNote.setOnEditorActionListener(this);

    }


    /**
     * Load Date Picker Dialog Fragment
     */
    private void loadTodoDatePicker() {

        todoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        try {

                            int mMonth = month + 1;

                            String todoDueDateStr = day+"/"+mMonth+"/"+year;

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date todoDueDate = sdf.parse(todoDueDateStr);

                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String todoDueDateStrFormatted = outputFormat.format(todoDueDate);

                            todoDate.setText(todoDueDateStrFormatted);

                        }  catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
                datePicker.show();

            }
        });

    }



    /**
     * Loading the TodoPriority Spinner with the choices
     */
    private void loadTodoPrioritySpinner() {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            TodoItemFormDialogListener listener = (TodoItemFormDialogListener) getActivity();

            String name     = todoName.getText().toString();
            String note     = todoNote.getText().toString();
            String priority = prioritySel;
            String dueDate  = todoDate.getText().toString();

            boolean success = db.updateTodoItem(new TodoItem(todoId,name,note,priority,dueDate));

            listener.onFinishEditDialog(name);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }

}
