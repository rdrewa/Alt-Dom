package pl.radoslawdrewa.droid.altdom;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by radoslaw.drewa on 2014-10-19.
 */
public class Insertion {

    private int id;
    private String title;
    private double price;
    private String currency;
    private int area;
    private String description;
    private double lat;
    private double lng;
    private String photo;
    private HashMap<String, String> details;
    private Set<String> specialFields;
    private String owner;
    private String objectType;
    private String location;

    public Insertion() {
        details = new HashMap<String, String>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public HashMap<String, String> getDetails() {
        return details;
    }

    public void addDetail(String key, String value) {
        details.put(key, value);
    }

    public void addDetail(String key, int value) {
        details.put(key, Integer.toString(value));
    }

    public void addDetail(String key, boolean value) {
        details.put(key, value ? "tak" : "nie");
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean hasDetail(String key) {
        return details.containsKey(key);
    }

    public Set<String> getDetailKeys() {
        return details.keySet();
    }
}
