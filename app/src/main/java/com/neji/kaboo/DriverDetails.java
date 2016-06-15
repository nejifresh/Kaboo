package com.neji.kaboo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.replication.ErrorInfo;
import com.cloudant.sync.replication.PushReplication;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorFactory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.common.eventbus.Subscribe;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import mehdi.sakout.fancybuttons.FancyButton;
/**
 * Created by Neji on 25/02/2015.
 */
public class DriverDetails extends BaseActivity implements SinchService.StartFailedListener  {
    private TextView txtCarDetails;
    private TextView txtdriverName;
    private TextView txtVehRegistration;
    private TextView txtACState;
    private TextView txtCategory;
    private TextView txtdistanceAway;
    private ImageView imageLoad;
    private String phone;
    private static final String DATASTORE_MANGER_DIR = "kabooData";
    private static final String DATASTORE_NAME = "kaboo";
    private String lati;
    private String longi;
    private ProgressDialog mSpinner;

    private String docId;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driverdetails);
        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // .addTestDevice("abc") //Random Text
                .build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff9800")));
bar.setTitle("");
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
txtCarDetails = (TextView)findViewById(R.id.txtCarInfo);
        txtdriverName = (TextView)findViewById(R.id.txtDriverName);
        txtVehRegistration = (TextView)findViewById(R.id.txtVehReg);
        txtACState = (TextView)findViewById(R.id.txtACState);
        txtCategory = (TextView)findViewById(R.id.txtCategory);
        txtdistanceAway = (TextView)findViewById(R.id.txtDist);
imageLoad = (ImageView)findViewById(R.id.profile_image);
        txtCarDetails.setText(toCamelCase(globalVariable.getVehicleMake() +" "+ globalVariable.getVechicleModel()
        +", "+globalVariable.getVehicleYear()));
        txtdriverName.setText( toCamelCase(globalVariable.getDriverName().toString()));
        txtVehRegistration.setText((globalVariable.getVehicleRegNo().toUpperCase()));
        txtACState.setText(toCamelCase(globalVariable.getHasAC()));
        txtCategory.setText(toCamelCase(globalVariable.getCategory()));
        txtdistanceAway.setText(globalVariable.getDistanceAway());
        phone = globalVariable.getPhone();
lati = globalVariable.getLatitude();
        docId = globalVariable.getDocID();
        longi = globalVariable.getLongitude();
        Picasso.with(this)
                .load(globalVariable.getImageURL())
                .placeholder(R.drawable.taxi) // optional
                .resize(500, 500)
                .centerCrop()
                .error(R.drawable.photo)         // optional
                .into(imageLoad);
    }

    public static String toCamelCase(String inputString) {
        String result = "";
        if (inputString.length() == 0) {
            return result;
        }
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);
            if (previousChar == ' ') {
                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }
        return result;
    }


    public void phoneClick(View view){
        try {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            ParseObject phonecall = new ParseObject("DriverPhoneCalls");
            phonecall.put("DriverName", globalVariable.getDriverName().toString());
            phonecall.put("UserName", ParseUser.getCurrentUser().getUsername().toString());
            phonecall.put("DriverPhone", phone);
            phonecall.put("UserPhone", ParseUser.getCurrentUser().get("phone").toString());
            phonecall.put("Category","Regular");
            phonecall.saveInBackground();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
        }catch (Exception e){
            Toast.makeText(DriverDetails.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }



    public void freeClick(View view){
        ParseUser currentUser = ParseUser.getCurrentUser();
//save the phone call in our log


        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(currentUser.getUsername());
            showSpinner();
        } else {
           // Toast.makeText(this,"Sinch Started",Toast.LENGTH_LONG).show();//open calling activity

            Call call = getSinchServiceInterface().callUserVideo(docId);
            String callId = call.getCallId();

            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            startActivity(callScreen);
        }
    }

    public void mapClick(View view){
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        globalVariable.setLatitude(lati);
        globalVariable.setLongitude(longi);
Intent intent = new Intent(DriverDetails.this,MapActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onServiceConnected() {
        //mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {

        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }

    }

    @Override
    public void onStarted() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
     //  Toast.makeText(this,"Sinch Started",Toast.LENGTH_LONG).show(); //openPlaceCallActivity();
        Call call = getSinchServiceInterface().callUser(docId);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Activating Kaboo Caller");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    public void reportClick(View view){
        Intent intent = new Intent(DriverDetails.this,ReportDriver.class);
        startActivity(intent);
    }

    public void addFavourites(View view){
SharedPreference myFavs = new SharedPreference();
        BeanSampleList myList = new BeanSampleList(phone);
        myFavs.addFavorite(this,myList);
List<BeanSampleList> nejilist = myFavs.loadFavorites(this);
        List<String>Titles = new ArrayList<String>();
        for (int i=0; i<nejilist.size(); i++) {

            Titles.add( nejilist.get(i).getTitle());
        }


        Toast.makeText(this,"Driver added to your list of favourites",Toast.LENGTH_LONG).show();
//launchRingDialog();
    }

    public void rateDriver(View view){
Intent intent = new Intent(DriverDetails.this,DriverRating.class);
        startActivity(intent);
    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(DriverDetails.this, "Please wait ...","Adding driver to your list of favourites.", true);
        ringProgressDialog.setCancelable(false);
        //ringProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Create a replicator that replicates changes from the local
// datastore to the remote database.
                    File path = getApplicationContext().getDir(DATASTORE_MANGER_DIR,MODE_PRIVATE);
                    DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
                    Datastore datastore = manager.openDatastore(DATASTORE_NAME);
                    BasicDocumentRevision retrieved = datastore.getDocument(docId);
                    //do the update on latitude and longitude
                    MutableDocumentRevision rev = new MutableDocumentRevision();
                    //Build up document content
                    Map<String,Object> json = new HashMap<String, Object>();
                    json.put("DocType", "Favourites");
                    json.put("DriverID", docId);
                    json.put("UserName", ParseUser.getCurrentUser().getUsername());
                    Time today = new Time(Time.getCurrentTimezone());
                    today.setToNow();
                    json.put("EntryDate", today.monthDay + "/" + today.month + "/" + today.year);
                    json.put("EntryTime", today.format("%k:%M:%S"));


                    rev.body = DocumentBodyFactory.create(json);
                    DocumentRevision client = datastore.createDocumentFromRevision(rev);
                    PushReplication push = new PushReplication();
                  //  push.username = "ughtfurestockertlyedgele";
                   // push.password = "GfdIBberssIe3rEgQTwNlPDg";
                    push.source = datastore;
                    try{
                        push.target = createServerURI();
                    } catch (URISyntaxException e){
                        e.printStackTrace();
                    }

                    final Replicator replicator = ReplicatorFactory.oneway(push);
                    CountDownLatch latch = new CountDownLatch(1);
                    Listener listener = new Listener(latch);
                    replicator.getEventBus().register(listener);

                    try{
                        replicator.start();

                        latch.await();
                        replicator.getEventBus().unregister(listener);
                    } catch (Exception e) {

                    }
                    ringProgressDialog.dismiss();
                    if (replicator.getState() != Replicator.State.COMPLETE) {
                        System.out.println("Error replicating TO remote");
                        System.out.println(listener.error);
                        showToast("Unable to synchronize, please try again later");


                    }
                    else{
//Log.d("TAG","Replication completed successfully");
                        showSuccessDialog();


                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private URI createServerURI()
            throws URISyntaxException {
        URI uri = new URI("https://finditt.cloudant.com/kaboo");
        return uri;
    }

    private class Listener {

        private final CountDownLatch latch;
        public ErrorInfo error = null;

        Listener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Subscribe
        public void complete(ReplicationCompleted event) {
            latch.countDown();


        }

        @Subscribe
        public void error(ReplicationErrored event) {
            this.error = event.errorInfo;
            latch.countDown();


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(DriverDetails.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog(){
        runOnUiThread(new Runnable() {
            public void run()
            {
                AlertDialog alertDialog = new AlertDialog.Builder(DriverDetails.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Success..");

                // Setting Dialog Message
                alertDialog.setMessage("Driver added to your list of favourites");

                // Setting Icon to Dialog
                alertDialog.setIcon(android.R.drawable.ic_dialog_info);
                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        // Toast.makeText(getApplicationContext(),"New Location created", Toast.LENGTH_SHORT).show();
                       // Intent i = new Intent(DriverRating.this,DriverDetails.class);
                       // startActivity(i);

                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        });
    }
}
