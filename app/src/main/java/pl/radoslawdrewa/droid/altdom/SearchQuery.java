package pl.radoslawdrewa.droid.altdom;

/**
 * Created by radoslaw.drewa on 2014-10-20.
 */
public class SearchQuery {

    private int page;
    private int limit;
    private int objectName = -1;
    private int offerType = -1;
    private int priceFrom = -1;
    private int priceTo = -1;
    private int areaFrom = -1;
    private int areaTo = -1;
    private double latFrom = -1;
    private double latTo = -1;
    private double lngFrom = -1;
    private double lngTo = -1;
    private int zoom;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getObjectName() {
        return objectName;
    }

    public void setObjectName(int objectName) {
        this.objectName = objectName;
    }

    public int getOfferType() {
        return offerType;
    }

    public void setOfferType(int offerType) {
        this.offerType = offerType;
    }

    public int getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(int priceFrom) {
        this.priceFrom = priceFrom;
    }

    public int getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(int priceTo) {
        this.priceTo = priceTo;
    }

    public int getAreaFrom() {
        return areaFrom;
    }

    public void setAreaFrom(int areaFrom) {
        this.areaFrom = areaFrom;
    }

    public int getAreaTo() {
        return areaTo;
    }

    public void setAreaTo(int areaTo) {
        this.areaTo = areaTo;
    }

    public double getLatFrom() {
        return latFrom;
    }

    public void setLatFrom(double latFrom) {
        this.latFrom = latFrom;
    }

    public double getLatTo() {
        return latTo;
    }

    public void setLatTo(double latTo) {
        this.latTo = latTo;
    }

    public double getLngFrom() {
        return lngFrom;
    }

    public void setLngFrom(double lngFrom) {
        this.lngFrom = lngFrom;
    }

    public double getLngTo() {
        return lngTo;
    }

    public void setLngTo(double lngTo) {
        this.lngTo = lngTo;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public SearchQuery(int limit) {
        this.limit = limit;
    }

    public String prepareParams() {
        StringBuilder sb = new StringBuilder("{");
        if (getPage() > 0) {
            sb.append("\"Page\":" + getPage() + ",");
        }
        if (getLimit() > 0) {
            sb.append("\"Limit\":" + getLimit() + ",");
        }
        sb.append("\"ObjectName\":" + getObjectName());
        sb.append(",\"OfferType\":" + getOfferType());
        sb.append(",\"LatFrom\":\"" + getLatFrom() + "\"");
        sb.append(",\"LatTo\":\"" + getLatTo() + "\"");
        sb.append(",\"LngFrom\":\"" + getLngFrom() + "\"");
        sb.append(",\"LngTo\":\"" + getLngTo() + "\"");
        //"OnlyWithPhoto":true
        if (getPriceFrom() > 0) {
            sb.append(",\"PriceFrom\":" + getPriceFrom());
        }
        if (getPriceTo() > 0) {
            sb.append(",\"PriceTo\":" + getPriceTo());
        }
        if (getAreaFrom() > 0) {
            sb.append(",\"AreaFrom\":" + getAreaFrom());
        }
        if (getAreaTo() > 0) {
            sb.append(",\"AreaTo\":" + getAreaTo());
        }
        sb.append(",\"OnlyWithPhoto\":true");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        SearchQuery sq = (SearchQuery) o;
        if (sq == null) {
            return false;
        }
        if (sq.getObjectName() != this.getObjectName()) {
            return false;
        }
        if (sq.getOfferType() != this.getOfferType()) {
            return false;
        }
        if (sq.getPriceFrom() != this.getPriceFrom()) {
            return false;
        }
        if (sq.getPriceTo() != this.getPriceTo()) {
            return false;
        }
        if (sq.getAreaFrom() != this.getAreaFrom()) {
            return false;
        }
        if (sq.getAreaTo() != this.getAreaTo()) {
            return false;
        }
        return true;
    }

    public void fill(SearchQuery sq) {
        this.setObjectName(sq.getObjectName());
        this.setOfferType(sq.getOfferType());
        this.setPriceFrom(sq.getPriceFrom());
        this.setPriceTo(sq.getPriceTo());
        this.setAreaFrom(sq.getAreaFrom());
        this.setAreaTo(sq.getAreaTo());
        this.setLatFrom(sq.getLatFrom());
        this.setLatTo(sq.getLatTo());
        this.setLngFrom(sq.getLngFrom());
        this.setLngTo(sq.getLngTo());
        this.setPage(sq.getPage());
    }
}
