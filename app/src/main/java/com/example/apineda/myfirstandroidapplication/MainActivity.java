package com.example.apineda.myfirstandroidapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // UI elements
    TextView mainTextView;
    Button button;
    EditText editText;
    ListView listView;
    ShareActionProvider shareAction;

    // Logic elements
    ArrayAdapter arrayAdapter;
    ArrayList nameList = new ArrayList();

    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";

    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTextView = (TextView) findViewById(R.id.hello_word_textview);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        editText = (EditText) findViewById(R.id.edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    addName();
                }
                return false;
            }
        });

        listView = (ListView) findViewById(R.id.list_view);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);

        // Welcome user
        displayWelcome();
    }

    private void displayWelcome () {

        // Accessing the device storage
        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Reading user's name
        String usersName = sharedPreferences.getString(PREF_NAME, "");

        if (usersName != null && !usersName.isEmpty()) {

            Toast.makeText(this, "Hello " + usersName, Toast.LENGTH_LONG).show();

        } else {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What's your name?");

            // Edit text for name
            final EditText input = new EditText(this);
            alert.setView(input);

            // Ok to save name
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Name written by the user
                    String inputName = input.getText().toString();

                    if (inputName != null && !inputName.isEmpty()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PREF_NAME, inputName);
                        editor.commit();

                        // Welcome new user
                        Toast.makeText(getApplicationContext(), "Hello "+ inputName, Toast.LENGTH_LONG).show();
                    }
                }
            });

            // Cancel button
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alert.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        if (shareItem != null) {
            shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }

        setShareIntent();

        return true;
    }

    private void setShareIntent() {

        if (shareAction != null) {

            // create an Intent with the contents of the TextView
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

            // Make sure the provider knows
            // it should work with that Intent
            shareAction.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onClick(View v) {
//        addName();
        queryBooks(editText.getText().toString());
    }

    private void addName() {

        String text = editText.getText().toString();
        mainTextView.setText(text + " is learning Android!");
        editText.getText().clear();

        nameList.add(text);
        arrayAdapter.notifyDataSetChanged();

        setShareIntent();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("omg android", position + ":" + nameList.get(position));
    }

    // Query books
    public void queryBooks(String searchString) {

        String urlString = "";

        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Client for networking
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(QUERY_URL + urlString, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject jsonObject) {

                // Display a "Toast" message
                // to announce your success
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();

                // 8. For now, just log results
                Log.d("omg android", jsonObject.toString());
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {

                // Display a "Toast" message
                // to announce the failure
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                // Log error message
                // to help solve any problems
                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });

    }
}
