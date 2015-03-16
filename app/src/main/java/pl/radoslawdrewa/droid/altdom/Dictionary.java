package pl.radoslawdrewa.droid.altdom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by radoslaw.drewa on 2014-10-23.
 */
public class Dictionary {

    private HashMap<String, HashMap<String, String>> data;

    public Dictionary(InputStream is) {
        data = new HashMap<String, HashMap<String, String>>();
        String json = load(is);
        parse(json);
        orderObjectName();
    }

    private void orderObjectName() {
        data.remove("ObjectName");
        HashMap<String, String> dictObjectName = new HashMap<String, String>();
        dictObjectName.put("0", "Mieszkanie");
        dictObjectName.put("1", "Dom");
        dictObjectName.put("2", "Działka");
        dictObjectName.put("3", "Pokój");
        dictObjectName.put("4", "Lokal użytkowy");
        dictObjectName.put("5", "Hala");
        dictObjectName.put("6", "Garaż");
        data.put("ObjectName", dictObjectName);
    }

    private String load(InputStream is) {
        String json = null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private void parse(String json) {

        try {
            JSONObject rootJSON = new JSONObject(json);
            JSONObject jsonDictionaries = rootJSON.getJSONObject("dictionaries");
            Iterator<String> mainIterator = jsonDictionaries.keys();
            while (mainIterator.hasNext()) {
                String mainDictName = mainIterator.next();
                if (isNeeded(mainDictName)) {
                    JSONObject subDictionary = jsonDictionaries.getJSONObject(mainDictName);
                    Iterator<String> subIterator = subDictionary.keys();
                    while (subIterator.hasNext()) {
                        String subDictName = subIterator.next();
                        HashMap<String, String> currentDict = new HashMap<String, String>();
                        JSONObject jsonDict = subDictionary.getJSONObject(subDictName);
                        Iterator<String> keys = jsonDict.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            currentDict.put(key, jsonDict.getString(key));
                        }
                        data.put(subDictName, currentDict);
                    }
                }
            }
        } catch (JSONException e) {
        }
    }

    private boolean isNeeded(String dict) {
        return dict.equals("Common") || dict.contains("Details");
    }

    public boolean has(String dict, String key) {
        if (!data.containsKey(dict)) {
            return false;
        }
        HashMap<String, String> dictionary = data.get(dict);
        return dictionary.containsKey(key);
    }

    public String get(String dict, String key) {
        if (!data.containsKey(dict)) {
            return null;
        }
        HashMap<String, String> dictionary = data.get(dict);
        return dictionary.get(key);
    }
}
