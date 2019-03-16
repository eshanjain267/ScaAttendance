package step.first.scaattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Date;

public class Att_screen extends AppCompatActivity {

    private EditText edcontact;
    private EditText edname ;
    private TextView tvAttSts;
    private TextView tvAttErrSts;
    private TextView  tvFeedue ;
    private TextView tvFeePaid ;
    private TextView tvFeeCmplt ;
    private TextView  tvFeeNtCmplt ;
    private Button btcam ;
    RequestQueue rq ;
    private IntentIntegrator qrScan;
    private String batchid ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_att_screen);
         edcontact = (EditText)findViewById(R.id.ated1);
         edname = (EditText)findViewById(R.id.ated2);
         tvAttSts = (TextView)findViewById(R.id.attv4);
        tvAttErrSts = (TextView)findViewById(R.id.attv5);
        tvFeedue = (TextView)findViewById(R.id.attv6);
        tvFeePaid = (TextView)findViewById(R.id.attv7);
        tvFeeCmplt = (TextView)findViewById(R.id.attv8);
        tvFeeNtCmplt = (TextView)findViewById(R.id.attv9);
        qrScan = new IntentIntegrator(this);
        Bundle bd = getIntent().getExtras();
        batchid = bd.getString("batchid");
       rq = MainActivity.rq ;

       btcam = (Button)findViewById(R.id.btcam);
       btcam.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               qrScan.initiateScan();
           }
       });


        edcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate())
                    return ;

                try {

                    JSONObject jsobj = new JSONObject();
                    jsobj.put("batchcode",""+batchid);
                    jsobj.put("contact",edcontact.getText().toString().trim());
                    Date d = new Date(System.currentTimeMillis()) ;
                    jsobj.put("date",d);
                    JsonObjectRequest jsobjreq = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.214:2040/SCALIVE/statusJsp.jsp", jsobj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                edname.setText("");
                                tvAttErrSts.setText("");
                                tvAttSts.setText("");
                                tvFeePaid.setText("");
                                tvFeedue.setText("");
                                tvFeeNtCmplt.setText("");
                                tvFeeCmplt.setText("");

                                String RegisterStatus = response.getString("RS");
                                if(RegisterStatus.equalsIgnoreCase("p"))
                                {
                                    String name =  response.getString("name");
                                    edname.setText(name.toUpperCase());
                                    String Attsts = response.getString("ATTSTS");
                                    if(Attsts.equalsIgnoreCase("done"))
                                    {
                                        tvAttSts.setText("ATTENDANCE SUCCESSFULL");
                                    }
                                    else
                                    {
                                        tvAttErrSts.setText("ATTENDANCE ALLREADY");
                                    }
                                    String FeeStatus = response.getString("FS");
                                    if(FeeStatus.equalsIgnoreCase("COM"))
                                    {
                                        tvFeeCmplt.setText("FEE COMPLETED");
                                    }
                                    else
                                    {
                                        tvFeeNtCmplt.setText("FEE NOT COMPLETED");
                                        String FeeDue = response.getString("FD");
                                        tvFeedue.setText("FEE DUE :"+FeeDue);
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                        } else {
                                            //deprecated in API 26
                                            v.vibrate(500);
                                        }

                                        MediaPlayer mp = MediaPlayer.create(Att_screen.this,R.raw.feenot);
                                        mp.seekTo(8000);
                                        mp.start();


                                    }

                                    String FeePaid = response.getString("FP");
                                    tvFeePaid.setText("FEE PAID :"+FeePaid);



                                }
                                else if(RegisterStatus.equalsIgnoreCase("f"))
                                {
                                tvAttErrSts.setText("NOT REGISTERED");


                                }
                                else
                                {
                                    tvAttErrSts.setText("SERVER IS FACING ERROR");
                                }

                                edcontact.setText("");



                            }
                            catch(JSONException ex)
                            {
                                Toast.makeText(Att_screen.this,"Error"+ex, Toast.LENGTH_LONG).show();
                                edcontact.setText("");
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(Att_screen.this,"Error"+error.getMessage(), Toast.LENGTH_LONG).show();
                            edcontact.setText("");
                            Intent i = new Intent(Att_screen.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });

                    rq.add(jsobjreq);
        Toast.makeText(Att_screen.this,"Send", Toast.LENGTH_LONG).show();
                }
                catch (Exception ex)
                {
                    Toast.makeText(Att_screen.this,"error"+ex, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Att_screen.this,MainActivity.class);
                    startActivity(i);
                    finish();

                }

            }
        });






    }
    public boolean validate()
    {
        String no =edcontact.getText().toString();
        if(no.length()>=10)
            return true ;
return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Found", Toast.LENGTH_LONG).show();
                edcontact.setText(result.getContents());
                edcontact.callOnClick();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
