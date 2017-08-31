package com.dldc.chatvibrationsfortwitch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.AuthorizationUIController;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.authorize_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AuthorizeTask(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });
    }

    class AuthorizeTask extends AsyncTask<String, Void, String> {
        private Context mContext;

        public AuthorizeTask(Context context){
            mContext = context;
        }

        protected String doInBackground(String... strings) {
            SharedPreferencesCredentialStore credentialStore = new SharedPreferencesCredentialStore(mContext, "chatVibrationsForTwitchPreferences", new JacksonFactory());

            AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                    BearerToken.authorizationHeaderAccessMethod(),
                    AndroidHttp.newCompatibleTransport(),
                    new JacksonFactory(),
                    new GenericUrl("https://api.twitch.tv/kraken/oauth2/authorize"),
                    new ClientParametersAuthentication("m4niy6t4288f42ori1b439nhmrfps0", null),
                    "m4niy6t4288f42ori1b439nhmrfps0",
                    "https://api.twitch.tv/kraken/oauth2/authorize");
            // TODO: Do we want to store the credentials? Or just authorize each time?
            //builder.setCredentialStore(credentialStore);
            Set scopes = new HashSet();
            scopes.add("chat_login");
            scopes.add("user_read");
            builder.setScopes(scopes);
            AuthorizationFlow flow = builder.build();

            // TODO: Add force_verify?

            AuthorizationUIController controller = new DialogFragmentController(getFragmentManager()) {
                @Override
                public String getRedirectUri() throws IOException {
                    return "http://localhost/Callback";
                }

                @Override
                public boolean isJavascriptEnabledForWebView() {
                    return true;
                }

                @Override
                public boolean disableWebViewCache() {
                    return false;
                }

                @Override
                public boolean removePreviousCookie() {
                    return true;
                }
            };

            OAuthManager oauth = new OAuthManager(flow, controller);

            String accessToken = null;
            try {
                // TODO: Is userId my user id or client id?
                accessToken = oauth.authorizeImplicitly("devinc13", null, null).getResult().getAccessToken();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return accessToken;
        }

        protected void onPostExecute(String accessToken) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra(Constants.ACCESSTOKEN, accessToken);
            startActivity(intent);
        }
    }
}
