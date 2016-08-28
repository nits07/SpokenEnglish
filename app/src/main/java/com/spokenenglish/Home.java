package com.spokenenglish;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class Home extends AppCompatActivity {
    private static final int TTS_CHECK_CODE = 101;
    private static final int RESULT_SETTINGS = 1;
    private static final String TTS_MISSING_MESSAGE = "Please install TextToSpeech Service";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    //Text to Speech
    EditText readText;
    Button readButton;
    private Intent recognizerIntent;
    //Speech to Text
    private TextView writeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Text to Speech
        readText=(EditText) findViewById(R.id.readText);
        readButton=(Button) findViewById(R.id.readButton);
        readButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                speak();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "2016 © Purva ♥ nits", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ///Speech to Text///
        writeText = (TextView) findViewById(R.id.writeText);
        Button speakButton = (Button) findViewById(R.id.speakButton);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        ////Intent to check if TTS Data is available else direct to download it////
        Intent checkTTSDataIntent = new Intent();
        checkTTSDataIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSDataIntent, TTS_CHECK_CODE);
    }

    /* ////Nothing to override
    @Override
    public void onPause(){
        super.onPause();
    }*/

    @Override
    public void onResume(){
        super.onResume();
        //Text to Speech
        readText=(EditText) findViewById(R.id.readText);
        readButton=(Button) findViewById(R.id.readButton);
        readButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                speak();
            }
        });
    }

    //////////////Speech to Text/////////////////
    private void promptSpeechInput(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale(pref.getString("speechLocale", "en_US")));
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try{
            startActivityForResult(recognizerIntent, REQ_CODE_SPEECH_INPUT);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
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
            case TTS_CHECK_CODE: {///////Checking TTS Data/////////
                if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //missing data, install it
                    Intent installIntent = new Intent();
                    Toast.makeText(getApplicationContext(), TTS_MISSING_MESSAGE, Toast.LENGTH_SHORT).show();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
                break;
            }
        }
    }

    //Calling SpeechService
    public void speak() {
        Context context = getApplicationContext();
        Intent speechServiceIntent = new Intent(context, SpeechService.class);
        speechServiceIntent.putExtra(SpeechService.EXTRA_TO_SPEAK, readText.getText().toString());
        context.startService(speechServiceIntent);
    }

    //Menu and other
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (recognizerIntent != null)
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
        //switch (item.getItemId()){
        //    case R.id.action_settings:{
        Intent i = new Intent(this, SettingsActivity.class);
//                Intent i = new Intent(this, UserSettingsActivity.class);
        startActivityForResult(i, RESULT_SETTINGS);
        return true;
        //    }
        //}

        //return super.onOptionsItemSelected(item);
    }
}
