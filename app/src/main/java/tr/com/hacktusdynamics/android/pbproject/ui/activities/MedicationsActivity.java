package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import tr.com.hacktusdynamics.android.pbproject.R;

public class MedicationsActivity extends AppCompatActivity {

    private WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications);
        browser =(WebView) findViewById(R.id.webview_medications);
        browser.loadUrl("http://www.ilacrehberi.com");
    }
}
