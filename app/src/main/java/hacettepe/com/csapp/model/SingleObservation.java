package hacettepe.com.csapp.model;

import java.util.Date;

public class SingleObservation {

    private String code;
    private String request_code;
    private String loc_desc;
    private double loc_lat;
    private double loc_lon;
    private double measurement;
    private String measurement_text;
    private String usercode;
    private Date recorded_at;
    private String property;

    public SingleObservation(String request_code, String loc_desc, double loc_lat, double lon_lat, String usercode, Date recorded_at) {
        this.request_code = request_code;
        this.loc_desc = loc_desc;
        this.loc_lat = loc_lat;
        this.loc_lon = lon_lat;
        this.usercode = usercode;
        this.recorded_at = recorded_at;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLoc_desc() {
        return loc_desc;
    }

    public void setLoc_desc(String loc_desc) {
        this.loc_desc = loc_desc;
    }

    public Double getLoc_lat() {
        return loc_lat;
    }

    public void setLoc_lat(Double loc_lat) {
        this.loc_lat = loc_lat;
    }

    public Double getLoc_lon() {
        return loc_lon;
    }

    public void setLoc_lon(Double lon_lat) {
        this.loc_lon = lon_lat;
    }

    public Double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Double measurement) {
        this.measurement = measurement;
    }

    public String getMeasurement_text() {
        return measurement_text;
    }

    public void setMeasurement_text(String measurement_text) {
        this.measurement_text = measurement_text;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public Date getRecorded_at() {
        return recorded_at;
    }

    public void setRecorded_at(Date recorded_at) {
        this.recorded_at = recorded_at;
    }
}
