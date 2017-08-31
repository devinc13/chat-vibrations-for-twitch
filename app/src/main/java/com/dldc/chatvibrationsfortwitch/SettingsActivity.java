package com.dldc.chatvibrationsfortwitch;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {
    private String accessToken;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            // TODO: Error message
        }

        accessToken = extras.getString(Constants.ACCESSTOKEN);
        if (accessToken == null) {
            // TODO: Error message
        }

        RadioButton single_vibration_radio = (RadioButton) findViewById(R.id.single_vibration);
        RadioButton double_vibration_radio = (RadioButton) findViewById(R.id.double_vibration);
        RadioButton triple_vibration_radio = (RadioButton) findViewById(R.id.triple_vibration);

        single_vibration_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(Constants.one_length);
            }
        });

        double_vibration_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(Constants.two_pattern, -1);
            }
        });

        triple_vibration_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(Constants.three_pattern, -1);
            }
        });

        Button button = (Button) findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText min_time_edit_text = (EditText) findViewById(R.id.min_time_between_vibrations);
                int min_time = Integer.parseInt(min_time_edit_text.getText().toString());

                RadioGroup num_vibrations_group = (RadioGroup) findViewById(R.id.num_vibrations_radio_group);
                int selectedId = num_vibrations_group.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = (RadioButton) findViewById(selectedId);
                String num_vibrations_string = selectedRadioButton.getText().toString();
                int num_vibrations = 1;
                if (getString(R.string.single_vibration).equals(num_vibrations_string)) {
                    num_vibrations = 1;
                } else if (getString(R.string.double_vibration).equals(num_vibrations_string)) {
                    num_vibrations = 2;
                } else if (getString(R.string.triple_vibration).equals(num_vibrations_string)) {
                    num_vibrations = 3;
                }

                Intent intent = new Intent(SettingsActivity.this, ChatActivity.class);
                intent.putExtra(Constants.ACCESSTOKEN, accessToken);
                intent.putExtra(Constants.MIN_TIME, min_time);
                intent.putExtra(Constants.NUM_VIBRATIONS, num_vibrations);

                // TODO: Replace username with result from api call
                intent.putExtra(Constants.USERNAME, "devinc13");
                startActivity(intent);
            }
        });
    }
}
