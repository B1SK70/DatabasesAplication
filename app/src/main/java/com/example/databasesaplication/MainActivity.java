package com.example.databasesaplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    Cursor cursor;

    ArrayAdapter<CharSequence> spinnerAdapter;

    EditText insertTitle;
    EditText insertBody;
    TextView selectedTitle;
    TextView selectedBody;
    Button insert;
    Spinner selector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDB();

        bindElements();
        setButtonListener();

        fillSpinner();

    }

    private void bindElements() {
        insertTitle = (EditText) findViewById(R.id.insert_title);
        insertBody = (EditText) findViewById(R.id.insert_body);
        insert = (Button) findViewById(R.id.createComment);
        selector = (Spinner) findViewById(R.id.selectComment);
        selectedTitle = (TextView) findViewById(R.id.selectedTitle);
        selectedBody = (TextView) findViewById(R.id.selectdBody);
    }


    private void setButtonListener() {
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = insertTitle.getText().toString();
                String body = insertBody.getText().toString();

                if (!title.equals("") && !body.equals("")) {
                    insertIntoDB(title, body);
                    spinnerAdapter.notifyDataSetChanged();
                } else {
                    System.out.println("error");
                }
            }
        });

    }

    private ArrayList<String> getAllComentsTitles() {
        cursor = selectFromDB();
        ArrayList<String> titles = new ArrayList<>();

        while (cursor.moveToNext()) {
            titles.add(cursor.getString(1));
        }
        cursor.close();

        return titles;
    }


    private void fillSpinner() {
        spinnerAdapter = new ArrayAdapter(
                this, android.R.layout.simple_list_item_1, getAllComentsTitles());
        selector.setAdapter(spinnerAdapter);

        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedComentTile = (String) parentView.getItemAtPosition(position);

                cursor = selectByTitle(selectedComentTile);
                cursor.moveToNext();

                fillDisplayData(cursor.getString(1), cursor.getString(2));

                cursor.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private void fillDisplayData(String title, String body) {

        selectedTitle.setText(title);

        selectedBody.setText(body);

    }

    private void setDB() {
        db = openOrCreateDatabase("MyDatabase", MODE_PRIVATE, null);
        try {
            db.execSQL(
                    "CREATE TABLE coments (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT,text TEXT)"
            );
        } catch (Exception e) {
            System.out.println("table existent, skipping create table");
        }
    }

    private void fillDB() {
        insertIntoDB("Hola", "comment 1");
        insertIntoDB("Adeu", "comment 2");
        insertIntoDB("Bon dia", "comment 3");
        insertIntoDB("Bona tarda", "comment 4");
        insertIntoDB("Bona nit", "comment 5");
    }

    private void insertIntoDB(String title, String text) {
        ContentValues values = new ContentValues(2);
        values.put("title", title);
        values.put("text", text);
        db.insert(
                "coments",
                "_id",
                values
        );
    }

    private void deleteFromDB(String title) {
        db.delete("coments",
                "title=?",
                new String[]{title}
        );
    }

    private Cursor selectFromDB() {
        return db.rawQuery("SELECT _id, title, text FROM coments", null);
    }

    private Cursor selectByTitle(String desiredTitle) {
        return db.rawQuery("SELECT * FROM coments WHERE title =?", new String[]{desiredTitle});
    }

    private void clearTable() {
        db.execSQL("delete from coments");
    }

}