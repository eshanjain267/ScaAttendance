package step.first.scaattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private Button next;
    private EditText ed1;
    private EditText ed2;
    private EditText ed3;
    private EditText ed4;
    private Spinner spbtch;
    private CheckBox cblk;
    private HashMap<String,BatchPojo> hm ;
    private ArrayList<String> categories ;
   public static RequestQueue rq;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rq = Volley.newRequestQueue(this);

        dialog = new ProgressDialog(this);
        spbtch = (Spinner) findViewById(R.id.sp);
        ed1 = (EditText) findViewById(R.id.ed1);
        ed2 = (EditText) findViewById(R.id.ed2);
        ed3 = (EditText) findViewById(R.id.ed3);
        ed4 = (EditText) findViewById(R.id.ed4);
        cblk = (CheckBox) findViewById(R.id.cblk);
        next = (Button) findViewById(R.id.btnext);
        ed1.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        ed2.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        ed3.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        ed4.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        next.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        dialog.setMessage("Loading..., Please Wait.");
        dialog.show();

        categories = new ArrayList<>();
        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spbtch.setAdapter(dataAdapter);


        JsonArrayRequest jsobjreq = new JsonArrayRequest(Request.Method.GET, "http://192.168.1.214:2040/SCALIVE/bacthjsp.jsp", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    JSONArray arr = response;
                    hm = new HashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        BatchPojo bp = new BatchPojo();
                        bp.setBatchcode(arr.getJSONObject(i).getString("batchid"));
                        bp.setCourse(arr.getJSONObject(i).getString("course"));
                        bp.setDays(arr.getJSONObject(i).getString("days"));
                        bp.setTime(arr.getJSONObject(i).getString("time"));
                        bp.setFaculty(arr.getJSONObject(i).getString("faculty"));
                        hm.put(bp.getBatchcode(), bp);


                    }
                    categories.addAll(hm.keySet());
                    dataAdapter.notifyDataSetChanged();
next.setVisibility(View.VISIBLE);

                } catch (JSONException ex) {
                    Toast.makeText(MainActivity.this, "Error" + ex, Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "CHECK YOUR CONNECTION AND OPEN APP AGAIN" , Toast.LENGTH_LONG).show();

            }
        });

        rq.add(jsobjreq);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Att_screen.class);
                i.putExtra("batchid", spbtch.getSelectedItem().toString());

                startActivity(i);
            }

        });


      spbtch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

              String id = spbtch.getSelectedItem().toString();
              ed1.setText(hm.get(id).getCourse());
              ed2.setText(hm.get(id).getDays());
              ed3.setText(hm.get(id).getTime());
              ed4.setText(hm.get(id).getFaculty());

          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {


          }
      });
    }
}
