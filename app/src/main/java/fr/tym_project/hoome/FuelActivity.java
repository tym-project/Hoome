package fr.tym_project.hoome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class FuelActivity extends Activity {

    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    //final String URL = "http://hoome.tym-project.fr/api.php/hoome_fuels";
    final String URL = "http://192.168.1.101/hoome/api.php/fuels";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuel);

        Intent intent = getIntent();

        final ListView fuelHistory= findViewById(R.id.fuelHistory);
        final DatePicker datePicker = findViewById(R.id.datePicker);
        final TextView cost = findViewById(R.id.cost);

        Button button = findViewById(R.id.button);
        FloatingActionButton refresh = findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                refreshListView(URL);
            }

        });

        fuelHistory.setLongClickable(true);


        fuelHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                StringRequest dr = new StringRequest(Request.Method.DELETE, URL+"/"+((Fuel) fuelHistory.getItemAtPosition(pos)).getId(),
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response

                                Toast.makeText(getApplicationContext(),"Deleted !", Toast.LENGTH_SHORT).show();
                                refreshListView(URL);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();

                            }
                        }
                );

                ApplicationController.getInstance().addToRequestQueue(dr);

                return true;
            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Post params to be sent to the server
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("date", getDateFromDatePicker(datePicker,format) );
                params.put("cost", cost.getText().toString());
                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                refreshListView(URL);
                                resetInputs();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshListView(URL);
                        resetInputs();
                    }
                });

                ApplicationController.getInstance().addToRequestQueue(req);

            }
        });

        refreshListView(URL);

    }



    private void setListView(ArrayList<Fuel> list){

        Collections.sort(list);
        ListView listView = (ListView) findViewById(R.id.fuelHistory);
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        setDaysElapsed();
        Toast.makeText(getApplicationContext(),"Refreshed !", Toast.LENGTH_LONG).show();

    }

    private void setDaysElapsed(){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        ListView listView = (ListView) findViewById(R.id.fuelHistory);;

        Fuel f =(Fuel) listView.getAdapter().getItem(0);

        TextView daysElapsed = (TextView) findViewById(R.id.daysElapsed);
        daysElapsed.setText(String.valueOf(Days.daysBetween(DateTime.parse(f.getDate(),format).toLocalDate(),DateTime.now().toLocalDate()).getDays()));
    }

    private void resetInputs(){
        ((DatePicker) findViewById(R.id.datePicker)).updateDate(DateTime.now().toLocalDate().getYear(),DateTime.now().toLocalDate().getMonthOfYear()-1,DateTime.now().toLocalDate().getDayOfMonth());
        ((TextView) findViewById(R.id.cost)).setText("");
    }


    private void refreshListView(String URL){
        final ArrayList<Fuel> fuelHistoryData =new ArrayList<Fuel>();
        // pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray contacts = response.getJSONObject("fuels").getJSONArray("records");

                            for (int i = 0; i < contacts.length(); i++) {
                                JSONArray c = contacts.getJSONArray(i);

                                String id = c.getString(0);
                                String date = c.getString(1);
                                String cost = c.getString(2);
                                fuelHistoryData.add(new Fuel(id,date,cost));
                            }

                            setListView(fuelHistoryData);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        ApplicationController.getInstance().addToRequestQueue(req);
    }




    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public static String getDateFromDatePicker(DatePicker datePicker,SimpleDateFormat format){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return format.format(calendar.getTime());
    }




   // @Override
   // public Map<String, String> getHeaders() throws AuthFailureError {
   //     HashMap<String, String> params = new HashMap<String, String>();
   //     String creds = String.format("%s:%s","USERNAME","PASSWORD");
   //     String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
   //     params.put("Authorization", auth);
   //     return params;
   // }
}
