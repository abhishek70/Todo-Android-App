package com.example.abhishek.todoapp.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.abhishek.todoapp.Models.TodoItem;
import java.util.ArrayList;

/**
 * Created by abhishek on 9/12/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB_TODOS";
    private static final String TODO_TABLE_NAME = "TBL_TODOS";
    private final ArrayList<TodoItem> todoItems = new ArrayList<TodoItem>();

    private static final String TODO_ID   = "todoId";
    private static final String TODO_NAME = "todoName";
    private static final String TODO_NOTE = "todoNote";
    private static final String TODO_PRIORITY = "todoPriority";


    private static final String TODO_TABLE_CREATE =
            "CREATE TABLE " + TODO_TABLE_NAME + " (" +
                    TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " +
                    TODO_NAME + " TEXT, " +
                    TODO_NOTE + " TEXT, " +
                    TODO_PRIORITY + " CHAR(10) NOT NULL DEFAULT 'LOW');";

    /**
     * Constructor
     * @param context
     */
    public DatabaseHandler(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method will create the table
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Executing the SQL Command
        db.execSQL(TODO_TABLE_CREATE);
    }

    /**
     * This method is used as migration and will upgrade by deleting existing and installing new
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Executing the SQL Command
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE_CREATE);

        // Re-creating table
        onCreate(db);
    }

    /**
     * This method will add TodoItem to the table
     * @param todoItem
     * @return
     */
    public long addTodoItem(TodoItem todoItem) {

        // Getting access for writing to the SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();

        // Initiate for storing column values
        ContentValues values = new ContentValues();

        // todoName
        values.put(TODO_NAME, todoItem.getTodoName());

        // todoNote
        values.put(TODO_NOTE, todoItem.getTodoNote());

        //todoPriority
        values.put(TODO_PRIORITY, todoItem.getTodoPriority());

        // Inserting TodoItem
        long lastInsertId = db.insert(TODO_TABLE_NAME, null, values);

        // Closing DB Connection
        db.close();

        return lastInsertId;
    }


    /**
     * Fetch TodoItem details by TodoId
     * @param todoId
     * @return
     */
    public TodoItem getTodoItemById(int todoId) {

        // Getting access for reading to the SQLite Database
        SQLiteDatabase db = this.getReadableDatabase();

        // Creating cursor
        Cursor cursor = db.query(TODO_TABLE_NAME, new String[] { TODO_ID,
                        TODO_NAME, TODO_NOTE, TODO_PRIORITY },  TODO_ID+" = ?",
                new String[] { String.valueOf(todoId) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        TodoItem todoItem = null;

        if (cursor != null) {

            // Fetching todoItem Details
            todoItem = new TodoItem(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        // return todoItem
        if (cursor != null) {
            cursor.close();
        }

        // Closing DB Connection
        db.close();

        return todoItem;
    }


    /**
     * Fetch All TodoItem from the Database
     * @return
     */
    public ArrayList<TodoItem> getAllTodos() {

        try {

            todoItems.clear();

            // Select All Query
            String selectQuery = "SELECT  * FROM " + TODO_TABLE_NAME;

            // Getting access for reading to the SQLite Database
            SQLiteDatabase db = this.getWritableDatabase();

            // Cursor initiation
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    TodoItem todoItem = new TodoItem();
                    todoItem.setTodoId(Integer.parseInt(cursor.getString(0)));
                    todoItem.setTodoName(cursor.getString(1));
                    todoItem.setTodoNote(cursor.getString(2));
                    todoItem.setTodoPriority(cursor.getString(3));

                    // Adding todoItem to list
                    todoItems.add(todoItem);
                } while (cursor.moveToNext());
            }

            // return todoList
            cursor.close();

            // Closing DB Connection
            db.close();
            return todoItems;

        } catch (Exception e) {

            Log.d("TodoApp - getAllTodos", e.getMessage());
        }

        return todoItems;
    }


    /**
     * This method will update the todoItem by todoId
     * @param todoItem
     * @return
     */
    public boolean updateTodoItem(TodoItem todoItem) {

        // Getting access for writing to the SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();

        // Initiate for storing column values
        ContentValues values = new ContentValues();

        // todoName
        values.put(TODO_NAME, todoItem.getTodoName());

        // todoNote
        values.put(TODO_NOTE, todoItem.getTodoNote());

        //todoPriority
        values.put(TODO_PRIORITY, todoItem.getTodoPriority());

        // updating todoItem
        int rowCount = db.update(TODO_TABLE_NAME, values,  TODO_ID+"  = ?",
                new String[] { String.valueOf(todoItem.getTodoId()) });

        return rowCount > 0;
    }


    /**
     * This method will delete todoItem by todoId
     * @param todoId
     */
    public void deleteTodoItem(long todoId) {

        // Getting access for writing to the SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();

        // deleting the todoItem
        db.delete(TODO_TABLE_NAME, TODO_ID+"  = ?",
                new String[] { String.valueOf(todoId) });

        // Closing DB Connection
        db.close();
    }


    /**
     * This method will return the total count for the todoItem in the table
     * @return
     */
    public int getTotalTodos() {

        // Select query
        String countQuery = "SELECT  * FROM " + TODO_TABLE_NAME;

        // Getting access for reading to the SQLite Database
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor initiation
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
