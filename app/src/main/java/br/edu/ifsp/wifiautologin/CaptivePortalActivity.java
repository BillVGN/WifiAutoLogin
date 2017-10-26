package br.edu.ifsp.wifiautologin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CaptivePortalActivity extends AppCompatActivity {

    private static final String TAG = "CaptivePortalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captive_portal);
    }
}
