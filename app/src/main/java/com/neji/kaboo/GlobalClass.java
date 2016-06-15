package com.neji.kaboo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.replication.Replicator;
import com.parse.Parse;
import com.parse.PushService;

import java.io.File;


/**
 * Created by Neji on 10/26/2014.
 */
public class GlobalClass extends Application {


    private static Context mContext;

    private String thequery;
    private static final String DATASTORE_MANGER_DIR = "kabooData";
    private static final String DATASTORE_NAME = "kaboo";

    private Replicator mPushReplicator;
    private Replicator mPullReplicator;


    Handler mHandler;
    //News Items
    private Double myLatitude;
    private Double myLongitude;
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
    private String distanceAway;


    public String getDistanceAway() {
        return distanceAway;
    }

    public void setDistanceAway(String distanceAway) {
        this.distanceAway = distanceAway;
    }

    public Double getMyLatitude() {
        return myLatitude;
    }

    public void setMyLatitude(Double myLatitude) {
        this.myLatitude = myLatitude;
    }

    public Double getMyLongitude() {
        return myLongitude;
    }

    public void setMyLongitude(Double myLongitude) {
        this.myLongitude = myLongitude;
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

    public void onCreate() {
        Parse.initialize(this, "rLFOXpm8Kx681Lyki8zg5zS7ZKkcSukSMZxXwyDw", "cTTOKoI9IO5HPOe7sqjDb1IdUSJLi97f4ldQdeC8");

        // Also in this method, specify a default Activity to handle push notifications
        PushService.setDefaultPushCallback(this, HomeActivity.class);
    }


    public Datastore OpenDataStore(Context context) {
        this.mContext = context;
        File path = mContext.getApplicationContext().getDir(DATASTORE_MANGER_DIR, MODE_PRIVATE);
        DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
        Datastore datastore;
        try {
            datastore = manager.openDatastore(DATASTORE_NAME);
            Log.d("SET", "Set up database at " + path.getAbsolutePath());
            return datastore;
        } catch (Exception e) {
            Log.e("Err", "Store not created");
        }
        return null;
    }
}