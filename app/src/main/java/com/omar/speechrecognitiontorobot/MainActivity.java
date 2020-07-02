package com.omar.speechrecognitiontorobot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Button firstNumTextView;
    TextView textView;


    TextToSpeech textToSpeech;


    private static String direction= "N";
    private int length = -1;
    private static String movement="";
    private static String d="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeech = new TextToSpeech(this, this);

        firstNumTextView = findViewById(R.id.firstNumTextView);
        textView=findViewById(R.id.textview);



        firstNumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                startActivityForResult(intent, 10);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 10:
                    String intFound = getNumberFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
                    if (intFound != "-1") {

                        movement=intFound;
                        textView.setText("The order was executed successfully");
                        InsertToDataBase();
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, I didn't catch that! Please try again", Toast.LENGTH_LONG).show();
                        textView.setText("Sorry, I didn't catch that! Please try again");
                    }
                    break;


            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
        }
    }

    // method to loop through results trying to find a number
    private String getNumberFromResult(ArrayList<String> results) {
        for (String str : results) {
            if (getIntNumberFromText(str) != "-1") {
                return getIntNumberFromText(str);
            }
        }
        return "-1";
    }


    // method to convert string number to integer
    private String getIntNumberFromText(String strNum) {

        String text = strNum;

        int count = 0;
        int i = 0;
         boolean exp=true;


        while (i < text.length() && exp==true ) {

            String s = "";
            char c = text.charAt(i);
            s += c;

            if( isInteger(s) == false) {

                          count++;
                    }
            else {
                 exp=false;
            }


            i++;
        }


              if(count >=5){
         String num=cutNumber(text,count);
        String txt=cutText(text,count);

        boolean LastWord=isInteger(num);
        boolean FirstWord=isCommand(txt);

        if(FirstWord==true && LastWord ==true){
          length= Integer.parseInt(num);
            return num;
           }

          }




        return "-1";
    }





    private void InsertToDataBase() {

       if ( direction =="F"){
           d="F";
       }
       else if ( direction =="R") {
           d = "R";
       }
       else if ( direction =="L"){
           d="L";
       }



        StringRequest request = new StringRequest(Request.Method.POST, "https://android-b.com/Task1/insert-information-DBS.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), " Done !" , Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("Movement", movement);
                params.put("Direction", d);

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);




    }

    @Override
    public void onInit(int i) {

    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public static String cutNumber(String txt,int num){
        String newTxt="";
        for(int i=num;i<=txt.length()-1;i++){
            newTxt+=txt.charAt(i);
        }

        return newTxt;
    }

    public static String cutText(String txt,int num){
        String newTxt="";
        String t=txt;
        int n=num;
        for(int i=0;i<n;i++){
            newTxt+=t.charAt(i);
        }

        return newTxt;
    }

    public static boolean isCommand (String txt){

        switch (txt) {


            case "forward ":
                case "go forward ":
                     case "ku forward ":
                         direction="F";
                        return true;

                      case "right ":
                         case "write ":
                          case "turn right ":
                              direction="R";
                        return true;

                         case "left ":
                             case "turn left ":
                                 direction="L";
                                 return true;

        }

        return false;

    }


}
