package com.neji.kaboo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.text.format.DateUtils;
import android.util.Log;

import com.cloudant.sync.datastore.Attachment;
import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.parse.ParseObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by Neji on 23/02/2015.
 */
public class Taxi implements Comparable<Taxi> {
    private String vehicleMake;
    private String phone;
    private String vehicleYear;
    private String contactAddress;
    private String status;
    private String hasAC;
    private String stateOfOrigin;
    private String regDate;
    private String driverName;
    private String regTime;
    private String vehicleRegNo;
    private String driversLicenseNo;
    private String residentCity;
    private String category;
    private String TimeOfCapture;
    private String imageURL;
    private String latitude;
    private String longitude;
    private String vechicleModel;
    private String docID;
    private float distanceFrom;
    private static float mydistance=0;
    private String timeDifference;
    private String distanceAway;
    private String liveStatus;

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getDistanceAway() {
        return distanceAway;
    }

    public void setDistanceAway(String distanceAway) {
        this.distanceAway = distanceAway;
    }

    public String getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(String timeDifference) {
        this.timeDifference = timeDifference;
    }

    public float getDistanceFrom() {
        return distanceFrom;
    }

    public void setDistanceFrom(float distanceFrom) {
        this.distanceFrom = distanceFrom;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(String vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHasAC() {
        return hasAC;
    }

    public void setHasAC(String hasAC) {
        this.hasAC = hasAC;
    }

    public String getStateOfOrigin() {
        return stateOfOrigin;
    }

    public void setStateOfOrigin(String stateOfOrigin) {
        this.stateOfOrigin = stateOfOrigin;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getVehicleRegNo() {
        return vehicleRegNo;
    }

    public void setVehicleRegNo(String vehicleRegNo) {
        this.vehicleRegNo = vehicleRegNo;
    }

    public String getDriversLicenseNo() {
        return driversLicenseNo;
    }

    public void setDriversLicenseNo(String driversLicenseNo) {
        this.driversLicenseNo = driversLicenseNo;
    }

    public String getResidentCity() {
        return residentCity;
    }

    public void setResidentCity(String residentCity) {
        this.residentCity = residentCity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimeOfCapture() {
        return TimeOfCapture;
    }

    public void setTimeOfCapture(String timeOfCapture) {
        TimeOfCapture = timeOfCapture;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getVechicleModel() {
        return vechicleModel;
    }

    public void setVechicleModel(String vechicleModel) {
        this.vechicleModel = vechicleModel;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public Location getLocation(Double latitude,Double longitude){
        Location itemLocation = new Location("ItemLocation");
        itemLocation.setLatitude(latitude);
        itemLocation.setLongitude(longitude);

        return itemLocation;
    }

    public static Taxi giveDetails(BasicDocumentRevision revision, Context context) {
        Taxi t = new Taxi();
        BasicDocumentRevision docs = revision;
        Map<String, Object> map = docs.asMap();
        //now get the distancce away
        final GlobalClass globalVariable = (GlobalClass) context;
        Location locationA = new Location("point A");
        locationA.setLatitude((globalVariable.getMyLatitude()));  //(GPS CAPTURED LATITUDE
        locationA.setLongitude((globalVariable.getMyLongitude())); //GPS CAPTURED LONGITUDE;
        //Location firstLocation = myLocation.getLocation();
        Location LocationB = new Location("point B");
        LocationB.setLatitude(Double.parseDouble((String) map.get("Latitude")));
        LocationB.setLongitude(Double.parseDouble((String) map.get("Longitude")));

        mydistance = locationA.distanceTo(LocationB);
        DecimalFormat df = new DecimalFormat("###.#");
        Log.d("Dist", "Got distance - " + df.format(mydistance / 1000));
//Now get time difference
        long tsLong = System.currentTimeMillis() / 1000;
        java.util.Date date = new java.util.Date();
        java.sql.Timestamp theTime = new Timestamp(date.getTime());

        // CharSequence timeresult = DateUtils.getRelativeTimeSpanString((Long.valueOf((String)map.get("Time"))), tsLong, DateUtils.SECOND_IN_MILLIS);
        if (mydistance / 1000 > 150) {
            return t = null;
        } else {


            try {
                t.setVehicleMake((String) map.get("VehicleMake"));
                t.setPhone((String) map.get("Phone"));
                t.setVehicleYear((String) map.get("VehicleYear"));
                // t.setImage(bMap);
                t.setContactAddress((String) map.get("ContactAddress"));
                t.setStatus((String) map.get("status"));
                t.setHasAC((String) map.get("HasAC"));
                t.setStateOfOrigin((String) map.get("StateOfOrigin"));
                t.setRegDate((String) map.get("RegDate"));
                t.setDriverName((String) map.get("DriverName"));
                ;
                t.setRegTime((String) map.get("RegTime"));
                t.setVehicleRegNo((String) map.get("VehicleRegNo"));
                t.setDriversLicenseNo((String) map.get("DriverLicenseNo"));
                t.setResidentCity((String) map.get("ResidentCity"));
                t.setCategory((String) map.get("Category"));
                t.setLiveStatus((String) map.get("LiveType"));
                t.setTimeOfCapture((String) map.get("Time"));
                t.setImageURL((String) map.get("Image"));
                t.setLatitude((String) map.get("Latitude"));
                t.setLongitude((String) map.get("Longitude"));
                t.setDistanceFrom(mydistance);
                t.setDistanceAway((String.valueOf(roundTwoDecimals(mydistance / 1000) + " km away")));
                //  t.setTimeDifference(getTimeString((Date.parse((String)map.get("Time")))));
                t.setVechicleModel((String) map.get("VehicleModel"));
                t.setDocID(docs.getId());

            } catch (Exception e) {
                Log.e("Error", "Error somewhere in content");
            }


            // if(map.containsKey("type") && map.get("type").equals(Task.DOC_TYPE)) {


            return t;

        }
    }

    public static Taxi giveFullDetails(ParseObject revision, Context context) {
        Taxi t = new Taxi();
        ParseObject docs = revision;

     //   Map<String, Object> map = docs.asMap();
        //now get the distancce away
        final GlobalClass globalVariable = (GlobalClass) context;
        Location locationA = new Location("point A");
        locationA.setLatitude((globalVariable.getMyLatitude()));  //(GPS CAPTURED LATITUDE
        locationA.setLongitude((globalVariable.getMyLongitude())); //GPS CAPTURED LONGITUDE;
        //Location firstLocation = myLocation.getLocation();
        Location LocationB = new Location("point B");
       LocationB.setLatitude(Double.parseDouble((String) docs.get("Latitude")));
     LocationB.setLongitude(Double.parseDouble((String) docs.get("Longitude")));

       mydistance = locationA.distanceTo(LocationB);
        DecimalFormat df = new DecimalFormat("###.#");
        Log.d("Dist", "Got distance - " + df.format(mydistance / 1000));
//Now get time difference
        long tsLong = System.currentTimeMillis() / 1000;
        java.util.Date date = new java.util.Date();
        java.sql.Timestamp theTime = new Timestamp(date.getTime());

        // CharSequence timeresult = DateUtils.getRelativeTimeSpanString((Long.valueOf((String)map.get("Time"))), tsLong, DateUtils.SECOND_IN_MILLIS);
        if (mydistance / 1000 > 150) {
            return t = null;
        } else {


            try {
                t.setVehicleMake((String) docs.get("VehicleMake"));
                t.setPhone((String) docs.get("Phone"));
                t.setVehicleYear((String) docs.get("VehicleYear"));
                // t.setImage(bMap);
                t.setContactAddress((String) docs.get("ContactAddress"));
                t.setStatus((String) docs.get("status"));
                t.setHasAC((String) docs.get("HasAC"));
                t.setStateOfOrigin((String) docs.get("StateOfOrigin"));
                t.setRegDate((String) docs.get("RegDate"));
                t.setDriverName((String) docs.get("DriverName"));
                ;
                t.setRegTime((String) docs.get("RegTime"));
                t.setVehicleRegNo((String) docs.get("VehicleRegNo"));
                t.setDriversLicenseNo((String) docs.get("DriverLicenseNo"));
                t.setResidentCity((String) docs.get("ResidentCity"));
                t.setCategory((String) docs.get("Category"));
                t.setLiveStatus((String) docs.get("LiveType"));
                t.setTimeOfCapture((String) docs.get("Time"));
                t.setImageURL((String) docs.get("Image"));
                t.setLatitude((String) docs.get("Latitude"));
                t.setLongitude((String) docs.get("Longitude"));
                t.setDistanceFrom(mydistance);
                t.setDistanceAway((String.valueOf(roundTwoDecimals(mydistance / 1000) + " km away")));
                //  t.setTimeDifference(getTimeString((Date.parse((String)map.get("Time")))));
                t.setVechicleModel((String) docs.get("VehicleModel"));
                t.setDocID(docs.getObjectId());

            } catch (Exception e) {
                Log.e("Error", "Error somewhere in content");
            }


            // if(map.containsKey("type") && map.get("type").equals(Task.DOC_TYPE)) {


            return t;

        }
    }


    private static float roundTwoDecimals(float d)
    {
        DecimalFormat twoDForm = new DecimalFormat("####.#");
        return Float.valueOf(twoDForm.format(d));

    }

    public static String getTimeString(Date fromdate) {

        long then;
        then = fromdate.getTime();
        Date date = new Date(then);

        StringBuffer dateStr = new StringBuffer();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar now = Calendar.getInstance();

        int days = daysBetween(calendar.getTime(), now.getTime());
        int minutes = hoursBetween(calendar.getTime(), now.getTime());
        int hours = minutes / 60;
        if (days == 0) {

            int second = minuteBetween(calendar.getTime(), now.getTime());
            if (minutes > 60) {

                if (hours >= 1 && hours <= 24) {
                    dateStr.append(hours).append("h");
                }

            } else {

                if (second <= 10) {
                    dateStr.append("Now");
                } else if (second > 10 && second <= 30) {
                    dateStr.append("few seconds ago");
                } else if (second > 30 && second <= 60) {
                    dateStr.append(second).append("s");
                } else if (second >= 60 && minutes <= 60) {
                    dateStr.append(minutes).append("m");
                }
            }
        } else

        if (hours > 24 && days <= 7) {
            dateStr.append(days).append("d");
        } else {
            dateStr.append((date));
        }

        return dateStr.toString();
    }

    public static int minuteBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
    }

    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
    }

    public int compareTo(Taxi compareItems) {

        float compareDistance = ((Taxi) compareItems).getDistanceFrom();

        //ascending order
        return (int)(this.distanceFrom - compareDistance);

        //descending order
        //return compareQuantity - this.quantity;

    }

}
