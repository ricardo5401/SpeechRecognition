package pe.edu.upc.speechrecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends Activity implements RecognitionListener {

    private SpeechRecognizer speech;
    private Intent recognizerIntent;
    Switch mSpeech;
    TextView mTextView;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 500;
    private static String TAG = "SPEECH_RECOGNITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeech = (Switch) findViewById(R.id.speechSwitch);
        mSpeech.setOnCheckedChangeListener(onChangeSwitch);
        mTextView = (TextView) findViewById(R.id.textView);
        Log.e(TAG, "onCreate");
    }
    private void checkPermission(){
        if(grantedPermission()){
            Log.e(TAG, "checkPermission Grant");
            mTextView.setText("Speak now");

            if(speech != null && recognizerIntent != null)
                speech.startListening(recognizerIntent);
            else
                buildSpeech();

        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }
    }

    private void buildSpeech(){
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "es");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
    }

    private boolean grantedPermission(){
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_RECORD_AUDIO: {
                if((grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED)){
                    checkPermission();
                }else {
                    showResult("Permission not granted");
                    mSpeech.setChecked(false);
                }
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.e(TAG, "destroy");
        }

    }

    private void showResult(String text){
        mTextView.setText(text);
    }

    private CompoundButton.OnCheckedChangeListener onChangeSwitch = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Log.e(TAG, "checked");
                checkPermission();
            }else{
                Log.e(TAG, "no checked");
                if(speech != null)
                    speech.stopListening();
            }
        }
    };


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.e(TAG, "onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e(TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.e(TAG, "onEndOfSpeech");
        mSpeech.setChecked(false);
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "onError");
        showResult(getErrorText(error));
    }

    @Override
    public void onResults(Bundle results) {
        Log.e(TAG, "onResults");
        ArrayList<String> _array = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.e(TAG, "Total results: " + String.valueOf(_array.size()));
        if(_array.size() > 0)
            showResult(_array.get(0));
        else
            showResult("No result found");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.e(TAG, "onResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e(TAG, "onEvent");
    }

    public static String getErrorText(int error){
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
