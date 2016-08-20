package com.spokenenglish;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
    private static final int TTS_CHECK_CODE = 101;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public Intent intent;
    //Text to Speech
    TextToSpeech textToSpeech;
    EditText readText;
    Button readButton;
    //Speech to Text
    private TextView writeText;
    private Button speakButton;

    private SpeechService speechService;
    private Intent speechServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Text to Speech
        readText=(EditText) findViewById(R.id.readText);
        readButton=(Button) findViewById(R.id.readButton);
        readButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                speak();
            }
        });

        ///Speech to Text///
        writeText = (TextView) findViewById(R.id.writeText);
        speakButton = (Button) findViewById(R.id.speakButton);
        speakButton.setOnClickListener(new View.OnClickListener() {             //
            @Override                                                          //
            public void onClick(View view) {
                promptSpeechInput();
            }                                                                  //
        });

        ///////Intent to check if TTS Data is available else direct to download it//////////
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TTS_CHECK_CODE);
    }

    @Override
    public void onPause(){
        //Text to Speech
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
        readButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                speak();
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
            case TTS_CHECK_CODE: {///////Checking TTS Data/////////
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //success, create the TTS instance
                } else {
                    //missing data, install it
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
            }
        }

    }

    //Calling SpeechService
    public void speak() {
        Context context = getApplicationContext();
        speechServiceIntent = new Intent(context, SpeechService.class);
        speechServiceIntent.putExtra(SpeechService.EXTRA_TO_SPEAK, readText.getText().toString());
        context.startService(speechServiceIntent);
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
