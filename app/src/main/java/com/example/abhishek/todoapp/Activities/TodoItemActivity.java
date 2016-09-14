package com.example.abhishek.todoapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.todoapp.Helpers.DatabaseHandler;
import com.example.abhishek.todoapp.Models.TodoItem;
import com.example.abhishek.todoapp.Models.TodoItem;
import com.example.abhishek.todoapp.R;

/**
 * TodoItem Activity will perform add and update todoItem
 */
public class TodoItemActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private String action;
    private long todoId;
    private EditText todoName, todoNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item);

        db = new DatabaseHandler(this);
        todoName = (EditText) findViewById(R.id.todo_name_et);
        todoNote = (EditText) findViewById(R.id.todo_note_et);
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
        }

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
    public void addTodoItem() {

        String name = todoName.getText().toString();
        String note = todoNote.getText().toString();

        if(name.length() > 0) {

            long insertId = db.addTodoItem(new TodoItem(name,note));

            if(insertId!=-1) {

                // Toast Message
                Toast.makeText(this, R.string.add_todo_success_msg, Toast.LENGTH_SHORT).show();

                // Prepare data intent
                Intent data = new Intent();
                data.putExtra("todoId", insertId);
                data.putExtra("todoName", name);
                data.putExtra("todoNote", note);

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
    public void editTodoItem() {


        String name = todoName.getText().toString();
        String note = todoNote.getText().toString();

        if(name.length() > 0) {

            boolean success = db.updateTodoItem(new TodoItem(todoId,name,note));
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
