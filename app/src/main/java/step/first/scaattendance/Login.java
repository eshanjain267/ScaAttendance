package step.first.scaattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText mobile ;
    private EditText password;
    private Button login ;
    public  RequestQueue rq;
    SharedPreferences.Editor editor ;
    SharedPreferences sharedPreferences ;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobile = (EditText)findViewById(R.id.mobile);
        password=(EditText)findViewById(R.id.pwd);
        login =(Button)findViewById(R.id.login);
        rq = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);

        mobile.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        password.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        login.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        int chck = sharedPreferences.getInt("check",0);

        if(chck==1)
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        editor  = sharedPreferences.edit();




          login.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if(mobile.getText().toString().length()<9 || password.getText().toString().isEmpty())
                      return ;
                  try {
                      JSONObject obj = new JSONObject();
                      obj.put("id", mobile.getText().toString());
                      obj.put("pwd",password.getText().toString());
                      dialog.setMessage("Verifying, Please Wait.");
                      dialog.show();

                      JsonObjectRequest jsobjreq = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.214:2040/SCALIVE/login.jsp", obj, new Response.Listener<JSONObject>() {
                          @Override
                          public void onResponse(JSONObject response) {

                              try {
                                  if (dialog.isShowing()) {
                                      dialog.dismiss();
                                  }
                                    String ans = response.getString("rs");
                                    if(ans.equalsIgnoreCase("true"))
                                    {

                                        editor.putInt("check", 1);
                                        editor.commit();
                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(Login.this, "Invalid Number/Password status = " + ans, Toast.LENGTH_LONG).show();
                                    }

                              } catch (Exception ex) {
                                  Toast.makeText(Login.this, "Error", Toast.LENGTH_LONG).show();

                              }

                          }
                      }, new Response.ErrorListener() {
                          @Override
                          public void onErrorResponse(VolleyError error) {

                              Toast.makeText(Login.this, "CHECK YOUR CONNECTION AND OPEN APP AGAIN", Toast.LENGTH_LONG).show();

                          }
                      });

                      rq.add(jsobjreq);

                  }
                  catch (JSONException ed)
                  {
                      Toast.makeText(Login.this, "Please right valid number and paswword", Toast.LENGTH_LONG).show();

                  }



              }
          });







    }
}
