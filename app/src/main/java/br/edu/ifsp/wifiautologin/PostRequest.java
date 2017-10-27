package br.edu.ifsp.wifiautologin;

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

public final class PostRequest {

    private Resources resources = null;

    private String mUserName = null;

    private String mUserPass = null;

    private String mResponse = null;

    private String mUrl = null;

    private Document mDocument = null;

    private RequestQueue queue = null;

    private OnResponseReceivedListener responseReceivedListener = null;

    private Response.Listener<String> rListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            setResponse(resources.getString(R.string.Progress_Status_connecting) + "\n");
            mDocument = Jsoup.parse(response);
            Elements errors = mDocument.select(".error_message");
            if (errors.isEmpty()) {
                Elements table_trs = mDocument.select("table.ss_table tr");
                StringBuilder sb = new StringBuilder(255);
                Element td;
                for (Element tr : table_trs) {
                    td = tr.children().first();
                    sb.append(td.text());
                    sb.append(td.nextElementSibling().text());
                    sb.append("\n");
                }
                setResponse(sb.toString());
            } else {
                setResponse(errors.first().text());
            }
        }
    };

    private Response.ErrorListener rErrorListener = new Response.ErrorListener() {
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

    PostRequest(String userName, String userPass, OnResponseReceivedListener listener) {
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

    private String getLastResponse() {
        return mResponse;
    }

    private void setResponse(String response) {
        mResponse = response;
        // Dispara o evento onResponseReceived com a resposta para alertar a View
        responseReceivedListener.onResponseReceived(mResponse);
    }

    public interface OnResponseReceivedListener {
        void onResponseReceived(String response);
    }

    public PostRequest setOnResponseReceivedListener(OnResponseReceivedListener listener) {
        responseReceivedListener = listener;
        return this;
    }

    private class CustomStringRequest extends StringRequest {

        CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();

            params.put("login", mUserName);
            params.put("password", mUserPass);

            return params;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();

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

    void sendPost() throws IllegalArgumentException{
        if (mUserName == null || mUserPass == null) {
            setResponse(resources.getString(R.string.PostRequest_Not_Enough_Parameters));
            throw new IllegalArgumentException(getLastResponse());
        }

        CustomStringRequest customStringRequest =
                new CustomStringRequest(CustomStringRequest.Method.POST, mUrl, rListener, rErrorListener);

        getRequestQueue().add(customStringRequest);
    }

    private RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(WifiAutoLogin.getContext());
        }

        return queue;
    }
}
