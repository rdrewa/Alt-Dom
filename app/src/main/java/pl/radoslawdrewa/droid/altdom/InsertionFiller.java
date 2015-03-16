package pl.radoslawdrewa.droid.altdom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by radoslaw.drewa on 2014-10-23.
 */
public class InsertionFiller {

    private Dictionary dictionary;
    private StringResource stringResource;

    private String[] ommitArray = {"ObjectName", "OfferType"};
    private List<String> ommitList;
    private String[] boolArray = {"Furnished", "PriceIncludeRent", "RentToStudents",
            "SellWithOtodom", "Individual", "Fence", "Parking", "OfficeSpace", "SocialFacilities"};
    private List<String> boolList;

    public InsertionFiller(Dictionary dictionary, StringResource stringResource) {
        this.dictionary = dictionary;
        this.stringResource = stringResource;
        ommitList = Arrays.asList(ommitArray);
        boolList = Arrays.asList(boolArray);
    }

    public Insertion fill(JSONObject jsonObject) {
        Insertion insertion = new Insertion();
        try {
            insertion.setId(jsonObject.getInt("ID"));
            insertion.setPrice(jsonObject.getDouble("Price"));
            insertion.setCurrency(dictionary.get("PriceCurrency", jsonObject.getString("PriceCurrency")));
            if (jsonObject.has("Description")) {
                insertion.setDescription(jsonObject.getString("Description"));
            }
            insertion.setArea(jsonObject.getInt("Area"));
            if (jsonObject.has("Latitude") && !jsonObject.isNull("Latitude")) {
                insertion.setLat(jsonObject.getDouble("Latitude"));
            }
            if (jsonObject.has("Longitude") && !jsonObject.isNull("Longitude")) {
                insertion.setLng(jsonObject.getDouble("Longitude"));
            }
            if (jsonObject.has("Photo")) {
                insertion.setPhoto(jsonObject.getString("Photo"));
            }
            String title = null;
            if (jsonObject.has("Title") && !jsonObject.isNull("Title")) {
                title = jsonObject.getString("Title");
            } else {
                if (jsonObject.has("City")) {
                    title = jsonObject.getString("City") + ": ";
                }
                title += "Super oferta!";
            }
            insertion.setTitle(title);
            if (jsonObject.has("Photo")) {
                insertion.setPhoto(jsonObject.getString("Photo"));
            }
            parseObjectType(jsonObject, insertion);
            parseOwner(jsonObject, insertion);
            parseLocation(jsonObject, insertion);
            parseDetails(jsonObject, insertion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return insertion;
    }

    private void parseObjectType(JSONObject jsonObject, Insertion insertion) {
        StringBuilder value = new StringBuilder();
        try {
            value.append(dictionary.get("ObjectName", jsonObject.getString("ObjectName")));
            value.append(" na ");
            value.append(dictionary.get("OfferType", jsonObject.getString("OfferType")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        insertion.setObjectType(value.toString());
    }

    private void parseOwner(JSONObject jsonObject, Insertion insertion) {
        String value = null;
        try {
            boolean individual = jsonObject.getBoolean("Individual");
            if (individual) {
                value = "bezpośrednio";
            } else {
                value = jsonObject.getJSONObject("SellerInfo").getString("Name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        insertion.setOwner(value);
    }

    private void parseLocation(JSONObject jsonObject, Insertion insertion) {
        StringBuilder sb = new StringBuilder();
        try {
            int countryId = jsonObject.getInt("Country");
            if (countryId != 1) {
                sb.append(dictionary.get("Country", String.valueOf(countryId)) + ", ");
            }
            String province = dictionary.get("Province", String.valueOf(jsonObject.getInt("Province")));
            sb.append("woj. " + province + ", ");
            String district = dictionary.get("District", String.valueOf(jsonObject.getInt("District")));
            String city = jsonObject.getString("City");
            if (!district.equals(city)) {
                sb.append("pow. " + district + ", ");
            }
            sb.append(city);
            if (!jsonObject.isNull("Quarter")) {
                String quarter = jsonObject.getString("Quarter");
                sb.append(", " + quarter);
            }
            if (!jsonObject.isNull("Street")) {
                sb.append(", " + jsonObject.getString("Street"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        insertion.setLocation(sb.toString());
    }

    private void parseDetails(JSONObject jsonObject, Insertion insertion) throws JSONException {
        parseDetails(jsonObject, insertion, null);
    }

    private void parseDetails(JSONObject jsonObject, Insertion insertion, String prefix) throws JSONException {
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String field = iterator.next();
            if (jsonObject.isNull(field)) {
                continue;
            }
            String label = stringResource.get("lab_" + field);
            String value = jsonObject.get(field).toString();
            if (!shouldParse(field, label)) {
                continue;
            }
            if (field.contains("Mask")) {
                value = parseMask(field, jsonObject);
            }
            if (boolList.contains(field)) {
                insertion.addDetail(label, Boolean.parseBoolean(value));
                continue;
            }
            if (field.contains("Details")) {
                if (value != null && !value.equals("null")) {
                    parseDetails(jsonObject.getJSONObject(field), insertion);
                } else {
                    continue;
                }
            }
            else {
                if (dictionary.has(field, value)) {
                    value = dictionary.get(field, value);
                }
                if (prefix != null) {
                    field = prefix + "." + field;
                }
                insertion.addDetail(label, value);
            }
        }
    }

    private String parseMask(String field, JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray items = jsonObject.getJSONArray(field);
            for (int i=0; i<items.length(); i++) {
                String val = String.valueOf(items.getInt(i));
                if (dictionary.has(field, val)) {
                    sb.append(dictionary.get(field, val) + ", ");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int lastIndexOf = sb.lastIndexOf(",");
        if (lastIndexOf > 0) {
            sb.delete(lastIndexOf, lastIndexOf + 2);
        }
        return sb.toString();
    }

    private boolean shouldParse(String field, String label) {
        return label != null && !ommitList.contains(field);
    }
}
