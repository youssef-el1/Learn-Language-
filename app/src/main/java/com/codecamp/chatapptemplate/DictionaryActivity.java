package com.codecamp.chatapptemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DictionaryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextView mTextViewResult;
    private RequestQueue mQueue;
    String langage="en";
    String[] languages = { "English", "French", "Spanish"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);


        Spinner spino = findViewById(R.id.langueSpinner);
        spino.setOnItemSelectedListener(this);
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, languages);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spino.setAdapter(ad);

        mTextViewResult = findViewById(R.id.textView);
        Button findButton = findViewById(R.id.button);
        mQueue = Volley.newRequestQueue(this);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonParse();
            }
        });
    }private  void jsonParse() {
        mTextViewResult.setText("");
        EditText editText=findViewById(R.id.editTextTextPersonName);
        String value=editText.getText().toString();

        String url = "https://api.dictionaryapi.dev/api/v2/entries/"+langage+"/"+value;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    JSONArray meanings = jsonObject.getJSONArray("meanings");
//                    Log.d("res", "Response is " + meanings);
                    for (int i = 0; i<meanings.length(); i++) {
                        JSONObject obj = meanings.getJSONObject(i);
                        JSONArray definitions = obj.getJSONArray("definitions");
                        for (int j = 0; j<definitions.length(); j++) {
                            JSONObject def = definitions.getJSONObject(j);
//                            Log.d("res", "Response is " + def.getString("definition"));
                            mTextViewResult.append("Definition: "+ def.getString("definition") +"\n"+
                                    "Example: "+def.getString("example")+ "\n\n"
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (languages[position].toString().equals("English")){
            langage = "en";
        }
        if (languages[position].toString().equals("French")){
            langage = "fr";
        }
        if (languages[position].toString().equals("Spanish")){
            langage = "es";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}