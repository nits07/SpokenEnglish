package com.spokenenglish;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class Home extends Activity {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public Intent intent;
    //Text to Speech
    TextToSpeech textToSpeech;
    EditText readText;
    Button readButton;
    //Speech to Text
    private TextView writeText;
    private Button speakButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Text to Speech
        readText=(EditText) findViewById(R.id.readText);
        readButton=(Button) findViewById(R.id.readButton);

        /*
        textToSpeech=new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if(status!=TextToSpeech.ERROR)
                    textToSpeech.setLanguage(new Locale("en","US"));
            }
        });*/

        readButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                /*
                String toSpeak=readText.getText().toString();
                Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                */
                textToSpeechService();
            }
        });


        //Speech to Text ///////check if this block is required in onResume() too
        writeText = (TextView) findViewById(R.id.writeText);                     //
        speakButton = (Button) findViewById(R.id.speakButton);                   //
        //
        speakButton.setOnClickListener(new View.OnClickListener() {             //
            @Override                                                          //
            public void onClick(View view) {                                    //
                promptSpeechInput();                                           //
            }                                                                  //
        });                                                                    //
        /////////////////////////////////////////////////////////////////////////
    }

    //Text to Speech
    @Override
    public void onPause(){
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        //Text to Speech
        readText=(EditText) findViewById(R.id.readText);
        readButton=(Button) findViewById(R.id.readButton);

        /*
        textToSpeech=new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if(status!=TextToSpeech.ERROR)
                    textToSpeech.setLanguage(new Locale("en","US"));
            }
        });*/

        readButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                /*
                String toSpeak=readText.getText().toString();
                Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                */
                textToSpeechService();
            }
        });

    }

    //////////////Text to Speech method/////////////////
    private void textToSpeechService() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    textToSpeech.setLanguage(new Locale("en", "US"));
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = readText.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    //////////////Speech to Text/////////////////
    private void promptSpeechInput(){
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try{
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
        finally{

        }
    }

    //Receiving Speech Input
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:{
                if(resultCode==RESULT_OK && null != data){
                    ArrayList<String> result =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    writeText.setText(result.get(0));
                }
                break;
            }
        }
    }

    //Menu and other
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if(intent!=null)
        {
            this.finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
