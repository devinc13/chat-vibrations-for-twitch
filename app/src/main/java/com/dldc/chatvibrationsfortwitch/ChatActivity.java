package com.dldc.chatvibrationsfortwitch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    public PircBotX bot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            // TODO: Error message
        }

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constants.PREFERENCES, 0);
        String accessToken = preferences.getString(Constants.CURRENT_ACCESS_TOKEN, null);
        if (accessToken == null) {
            // TODO: Error message
        }

        String username = extras.getString(Constants.USERNAME);
        if (username == null) {
            // TODO: Error message
        }

        int num_vibrations = extras.getInt(Constants.NUM_VIBRATIONS, 1);
        int min_time_between = extras.getInt(Constants.MIN_TIME, 1);

        final Button button = (Button) findViewById(R.id.disconnect_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DisconnectTask(ChatActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });

        new StartBotTask(this, accessToken, username, num_vibrations, min_time_between).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    class StartBotTask extends AsyncTask<String, Void, Void> {
        private Context mContext;
        private String accessToken;
        private String username;
        private int num_vibrations;
        private int min_time_between;

        public StartBotTask(Context context, String accessToken, String username, int num_vibrations, int min_time_between){
            mContext = context;
            this.accessToken = accessToken;
            this.username = username;
            this.num_vibrations = num_vibrations;
            this.min_time_between = min_time_between;
        }

        protected Void doInBackground(String... strings) {
            String twitch_username = username;
            String oauth_password = "oauth:" + accessToken;
            String twitch_channel = "#" + username;

            Configuration configuration = new Configuration.Builder()
                    .setAutoNickChange(false)
                    .setOnJoinWhoEnabled(false)
                    .setCapEnabled(true)
                    .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                    .addServer("irc.twitch.tv")
                    .setName(twitch_username)
                    .setServerPassword(oauth_password)
                    .addAutoJoinChannel(twitch_channel)
                    .addListener(new MyListenerAdapter(mContext, num_vibrations, min_time_between)).buildConfiguration();

            bot = new PircBotX(configuration);

            try {
                bot.startBot();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void param) {
            // Do nothing
        }
    }

    class DisconnectTask extends AsyncTask<String, Void, Void> {
        private Context mContext;

        public DisconnectTask(Context context){
            mContext = context;
        }

        protected Void doInBackground(String... strings) {
            bot.sendIRC().quitServer();
            return null;
        }

        protected void onPostExecute(Void param) {
            // Go back to the main activity
            Intent intent = new Intent(mContext, SettingsActivity.class);
            startActivity(intent);
        }
    }
}
