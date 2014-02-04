package com.dharrya.android.notifier.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends ActionBarActivity {
    TextView mStatus;
    Context context;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.context = getApplicationContext();
        Preferences preferences = Preferences.getInstance(this.context);

        this.mStatus = (TextView) findViewById(R.id.status);
        if (checkPlayServices() && checkRestRegistered()) {
            this.mStatus.setText(R.string.status_ok);
        } else {
            this.mStatus.setText(R.string.status_failed);
        }

        EditText loginEdit = (EditText) findViewById(R.id.LoginEdit);
        loginEdit.setText(preferences.getDeviceName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
            return rootView;
        }
    }

    // Send an upstream message.
    public void onClickReconnect(final View view) {
        EditText loginEdit = (EditText) findViewById(R.id.LoginEdit);
        final String login = loginEdit.getText().toString();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                mStatus.setText(R.string.status_refresh);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result;
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    String s = Preferences.SENDER_ID;
                    String regid = gcm.register(Preferences.SENDER_ID);
                    sendRegistrationId(regid, login);
                    storeRegistrationInfo(regid, login);
                    result = "ok";
                } catch (IOException ex) {
                    result = "fail";
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.equals("ok")) {
                    mStatus.setText(R.string.status_ok);
                } else {
                    mStatus.setText(R.string.status_failed);
                }
            }
        }.execute(null, null, null);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS && GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
            return false;
        }
        return true;
    }

    private boolean checkRestRegistered() {
        return !Preferences.getInstance().getRegistrationId().isEmpty();

//        return (
//                !Preferences.getInstance().getDeviceName().isEmpty()
//                && !Preferences.getInstance().getPassword().isEmpty()
//                && !Preferences.getInstance().getRegistrationId().isEmpty()
//        );
    }

    private void sendRegistrationId(String regId, String login) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("reg_id", regId);
        params.put("login", login);

        HttpRequest.post("http://ws.mmmkay.info/user/new").form(params).code();
    }

    private void storeRegistrationInfo(String regId, String login) {
        Preferences
                .getInstance()
                .setRegistrationId(regId)
                .setDeviceName(login)
                .save();

    }
}
