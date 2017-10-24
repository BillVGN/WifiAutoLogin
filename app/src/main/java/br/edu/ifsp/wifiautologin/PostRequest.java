package br.edu.ifsp.wifiautologin;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by willian on 20/10/17.
 */

public final class PostRequest {

    private Resources resources = null;

    private String mUserName = null;

    private String mUserPass = null;

    private String mResponse = null;

    private String mUrl = null;

    private Document mDocument = null;

    private OnResponseReceivedListener responseReceivedListener = null;

    protected Response.Listener<String> rListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            setResponse(resources.getString(R.string.Progress_Status_connecting) + "\n");
            mDocument = Jsoup.parse(response);
            Elements errors = mDocument.select(".error_message");
            if (errors.isEmpty()) {
                Elements table_trs = mDocument.select("table.ss_table tr");
                StringBuilder sb = new StringBuilder(255);
                Element td = null;
                for (Element tr : table_trs) {
                    td = tr.children().first();
                    sb.append(td.text() + td.nextElementSibling().text());
                    sb.append("\n");
                }
                setResponse(sb.toString());
            } else {
                setResponse(errors.first().text());
            }
        }
    };

    protected Response.ErrorListener rErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            setResponse(error.getLocalizedMessage());
        }
    };

    public PostRequest() {
        resources = WifiAutoLogin.getContext().getResources();

        mUrl = resources.getString(R.string.Login_url);
    }

    public PostRequest(String userName, String userPass) {
        resources = WifiAutoLogin.getContext().getResources();

        mUserName = userName;
        mUserPass = userPass;
        mUrl = resources.getString(R.string.Login_url);
    }

    public PostRequest(String userName, String userPass, OnResponseReceivedListener listener) {
        resources = WifiAutoLogin.getContext().getResources();

        mUserName = userName;
        mUserPass = userPass;
        mUrl = resources.getString(R.string.Login_url);
        responseReceivedListener = listener;
    }

    public String getUserName() {
        return mUserName;
    }

    public PostRequest setUserName(String userName) {
        mUserName = userName;
        return this;
    }

    public String getUserPass() {
        return mUserPass;
    }

    public PostRequest setUserPass(String userPass) {
        mUserPass = userPass;
        return this;
    }

    public String getLastResponse() {
        return mResponse;
    }

    private PostRequest setResponse(String response) {
        mResponse = response;
        // Dispara o evento onResponseReceived com a resposta para alertar a View
        responseReceivedListener.onResponseReceived(mResponse);
        return this;
    }

    public interface OnResponseReceivedListener {
        void onResponseReceived(String response);
    }

    public PostRequest setOnResponseReceivedListener(OnResponseReceivedListener listener) {
        responseReceivedListener = listener;
        return this;
    }

    private class CustomStringRequest extends StringRequest {

        public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();

            params.put("login", mUserName);
            params.put("password", mUserPass);

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
            parsed = new String(response.data, Charset.defaultCharset());
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }

    }

    public void sendPost(Context context) {
        if (mUserName == null || mUserPass == null) {
            setResponse(resources.getString(R.string.PostRequest_Not_Enough_Parameters));
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        CustomStringRequest customStringRequest =
                new CustomStringRequest(CustomStringRequest.Method.POST, mUrl, rListener, rErrorListener);

        queue.add(customStringRequest);
    }
}
