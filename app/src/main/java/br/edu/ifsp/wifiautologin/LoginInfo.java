package br.edu.ifsp.wifiautologin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginInfo extends AppCompatActivity {

    private TextView inputUser = null;
    private TextView inputPassword = null;
    private TextView textResponse = null;
    private Document doc = null;
    private static String PREFS_NAME = "walprefs";

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendPostRequest(inputUser.getText().toString(), inputPassword.getText().toString());
        }
    };

    private void sendPostRequest(final String login, final String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                getString(R.string.Login_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        textResponse.setText(R.string.Progress_Status_connecting + "\n");
                        doc = Jsoup.parse(response);
                        Elements errors = doc.select(".error_message");
                        if (errors.isEmpty()) {
                            Elements table_trs = doc.select("table.ss_table tr");
                            StringBuilder sb = new StringBuilder(255);
                            Element td = null;
                            for (Element tr : table_trs) {
                                td = tr.children().first();
                                sb.append(td.text() + td.nextElementSibling().text());
                                sb.append("\n");
                            }
                            textResponse.setText(sb.toString());
                        } else {
                            textResponse.setText(errors.first().text());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textResponse.setText(error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("login", login);
                params.put("password", password);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("Accept", "text/html");
                headers.put("Accept-Charset", "utf-8");
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        // Sério que havia esquecido de adicionar a request à fila?
        queue.add(stringRequest);
    }

    private void updateSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.prefs_user), inputUser.getText().toString());
        editor.putString(getString(R.string.prefs_pass), inputPassword.getText().toString());
        editor.commit();
    }

    private void readSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        inputUser.setText(sharedPreferences.getString(getString(R.string.prefs_user), ""));
        inputPassword.setText(sharedPreferences.getString(getString(R.string.prefs_pass), ""));
    }

    private View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            updateSettings();
        }
    };

    private void assignViewListeners() {
        // Referenciar os campos da View, caso ainda não estejam
        if (inputUser == null || inputPassword == null) {
            inputUser = (TextView) findViewById(R.id.inputUser);
            inputPassword = (TextView) findViewById(R.id.inputPassword);
        }
        textResponse = (TextView) findViewById(R.id.textResponse);

        // Referenciar os eventos aos listeners
        inputUser.setOnFocusChangeListener(mFocusListener);
        inputPassword.setOnFocusChangeListener(mFocusListener);
        findViewById(R.id.btnPostRequest).setOnClickListener(mClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);


        assignViewListeners();
        readSettings();
    }
}
