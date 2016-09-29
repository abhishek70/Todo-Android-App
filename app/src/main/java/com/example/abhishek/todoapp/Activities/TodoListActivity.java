package com.example.abhishek.todoapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.abhishek.todoapp.Fragments.TodoItemFormDialogFragment;
import com.example.abhishek.todoapp.R;
import com.example.abhishek.todoapp.Adapters.TodoListAdapter;
import com.example.abhishek.todoapp.Helpers.DatabaseHandler;
import com.example.abhishek.todoapp.Models.TodoItem;
import java.util.ArrayList;

/**
 * TodoList Activity will list all the todoItems from the database
 */
public class TodoListActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        TodoItemFormDialogFragment.TodoItemFormDialogListener {

    private ArrayList<TodoItem> todoItems;
    private ListView todoListView;
    private TodoListAdapter todoListAdapter;
    private DatabaseHandler db;

    private final int ADD_REQUEST_CODE      = 200;
    private final int UPDATE_REQUEST_CODE   = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load layout file
        setContentView(R.layout.activity_todo_list);

        // Populate the todoList from the database
        populateTodoListFromDatabase();

        todoListView    = (ListView) findViewById(R.id.todo_list_view);

        // Load the todoList in the ListView via todoList Adapter
        if (todoListView != null) {
            todoListView.setAdapter(todoListAdapter);
            todoListView.setOnItemClickListener(this);
            todoListView.setOnItemLongClickListener(this);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v("TodoApp", "onStart Called");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v("TodoApp", "onResume Called");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v("TodoApp", "onPause Called");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.v("TodoApp", "onStop Called");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("TodoApp", "onDestroy Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this,TodoItemActivity.class);
            intent.putExtra("ADDEDITKEY","Add");
            startActivityForResult(intent, ADD_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the child activity operation is successfully completed
     * Same method is called for Add and Update TodoItem
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Response after adding the todoItem
        if (resultCode == RESULT_OK && requestCode == ADD_REQUEST_CODE) {

            long todoId     = data.getExtras().getLong("todoId", 0);
            String todoName = data.getExtras().getString("todoName");
            String todoNote = data.getExtras().getString("todoNote");
            String todoPriority = data.getExtras().getString("todoPriority");
            String todoDueDate  = data.getExtras().getString("todoDueDate");

            // Adding new todoItem to the list
            todoItems.add(new TodoItem(todoId, todoName, todoNote, todoPriority, todoDueDate));

            // Notifying the adapter to load the new todoItem
            todoListAdapter.notifyDataSetChanged();
        }

        // Response after editing the todoITem
        if (resultCode == RESULT_OK && requestCode == UPDATE_REQUEST_CODE) {

            // Clear the todoItem List
            todoItems.clear();

            // Fetch and Store the todoList in the todoItem List
            todoItems.addAll(db.getAllTodos());

            // Notifying the adapter to load the updated todoItem
            todoListAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Populating the todoItem List from the database
     */
    public void populateTodoListFromDatabase(){

        // Database Initialization
        db = new DatabaseHandler(this);

        todoItems = new ArrayList<TodoItem>();

        // Fetching all Todos
        ArrayList<TodoItem> data = db.getAllTodos();

        if(data.size()!=0) {

            for (int i = 0; i < data.size(); i++) {

                long todoId = data.get(i).getTodoId();
                String todoName = data.get(i).getTodoName();
                String todoNote = data.get(i).getTodoNote();
                String todoPriority = data.get(i).getTodoPriority();
                String todoDueDate  = data.get(i).getTodoDueDate();

                TodoItem todoItem = new TodoItem();
                todoItem.setTodoId(todoId);
                todoItem.setTodoName(todoName);
                todoItem.setTodoNote(todoNote);
                todoItem.setTodoPriority(todoPriority);
                todoItem.setTodoDueDate(todoDueDate);

                todoItems.add(todoItem);
            }
        }

        // Closing database connection
        db.close();

        // Custom Adapter Initialization
        todoListAdapter = new TodoListAdapter(getApplicationContext(), todoItems);

    }

    /**
     * This method will be called when the user click on any listView row
     * and will take the user to the Edit TodoItem Activity
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
        // Creating explicit intent
        Intent intent = new Intent(this,TodoItemActivity.class);

        // Passing required data to the child activity
        intent.putExtra("ADDEDITKEY", "Edit");
        intent.putExtra("TODOID",todoItems.get(position).getTodoId());

        // open the child activity
        startActivityForResult(intent, UPDATE_REQUEST_CODE);

       */
        showEditDialog(todoItems.get(position).getTodoId());
    }


    /**
     * This method will be called when the user perform long click on any listView Row
     * Also this will show dialog to get confirmation from user to delete the todoItem
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        // Creating dialog box with confirmation for the user to select operation
        AlertDialog.Builder builder = new AlertDialog.Builder(TodoListActivity.this);

        // Setting Dialog Title
        builder.setTitle(getString(R.string.action_delete)+ " "+ todoItems.get(position).getTodoName());

        // Setting Dialog Message
        builder.setMessage(R.string.dialog_message);

        // Forcing user to make a choice
        builder.setCancelable(false);

        // Set the Positive button and perform the delete operation
        builder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // Deleting the todoItem from the database
                db.deleteTodoItem(todoItems.get(position).getTodoId());

                // Removing that todoItem from the todoItem List
                todoItems.remove(position);

                // Notifying the adapter to load the updated todoItem
                todoListAdapter.notifyDataSetChanged();
            }
        });

        // Set the Negative button and perform the cancel operation
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Dismiss the dialog
                dialog.cancel();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Showing the dialog
        dialog.show();

        return true;
    }

    private void showEditDialog(long todoId) {

        TodoItem todoItem = db.getTodoItemById((int) todoId);

        FragmentManager fm = getSupportFragmentManager();
        TodoItemFormDialogFragment todoItemFormDialogFragment = TodoItemFormDialogFragment.newInstance(todoItem);
        todoItemFormDialogFragment.show(fm, "fragment_edit_name");

    }

    @Override
    public void onFinishEditDialog(String inputText) {

        // Clear the todoItem List
        todoItems.clear();

        // Fetch and Store the todoList in the todoItem List
        todoItems.addAll(db.getAllTodos());

        // Notifying the adapter to load the updated todoItem
        todoListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();

    }

}
