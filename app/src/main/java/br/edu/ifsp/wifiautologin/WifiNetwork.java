package br.edu.ifsp.wifiautologin;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by willian on 22/11/17.
 */

public class WifiNetwork {

    public int id;
    public String content;

    public WifiNetwork(String SSID) {
        this.content = SSID;
    }

    @Override
    public String toString() {
        return this.content;
    }

    public JSONArray toJSONArray() throws JSONException{
        JSONArray json = new JSONArray();

        json.put(this.id, this.content);

        return json;
    }

    public void fromJSONArray(JSONArray jsonArray) {

    }
}
