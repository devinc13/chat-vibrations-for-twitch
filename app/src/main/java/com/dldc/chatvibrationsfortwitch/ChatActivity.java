package com.dldc.chatvibrationsfortwitch;

import android.content.Context;
import android.content.Intent;
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

        final Button button = (Button) findViewById(R.id.disconnect_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DisconnectTask(ChatActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });

        new StartBotTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    class StartBotTask extends AsyncTask<String, Void, Void> {
        private Context mContext;

        public StartBotTask(Context context){
            mContext = context;
        }

        protected Void doInBackground(String... strings) {
            // TODO: pass these through intent
            String twitch_username = "";
            String oauth_password = "";
            String twitch_channel = ""; // Note: This needs a # in front of it

            Configuration configuration = new Configuration.Builder()
                    .setAutoNickChange(false) //Twitch doesn't support multiple users
                    .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
                    .setCapEnabled(true)
                    .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
                    .addServer("irc.twitch.tv")
                    .setName(twitch_username) //Your twitch.tv username
                    .setServerPassword(oauth_password) //Your oauth password from http://twitchapps.com/tmi
                    .addAutoJoinChannel(twitch_channel) //Some twitch channel
                    .addListener(new MyListenerAdapter(mContext)).buildConfiguration();

            //Create our bot with the configuration
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
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            bot.sendIRC().quitServer();
            return null;
        }

        protected void onPostExecute(Void param) {
            // Go back to the main activity
            Log.d(TAG, "GOING BACK");
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
    }
}
