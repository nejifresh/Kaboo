package com.neji.kaboo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.indexing.IndexManager;
import com.cloudant.sync.indexing.QueryBuilder;
import com.cloudant.sync.indexing.QueryResult;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.replication.ErrorInfo;
import com.cloudant.sync.replication.PullReplication;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorFactory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.eventbus.Subscribe;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by neokree on 24/11/14.
 */
public class CarParks extends Fragment implements ConnectionCallbacks,OnConnectionFailedListener,
        LocationListener {
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    private static final String TAG = CarParks.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private String currLatitude = "";
    private String currLongitude = "";
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private ListView listView1;
    private static final String DATASTORE_MANGER_DIR = "kabooData";
    private static final String DATASTORE_NAME = "kabba";
    File path ;
    private AdView mAdView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.featured, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listView1 = (ListView)rootView.findViewById(R.id.listview1);  mAdView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // .addTestDevice("abc") //Random Text
                .build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
           startLocationUpdates();
            //displayLocation();
        }


        try {

            new launchParseDialog().execute();



        }catch (Exception e){
Log.d("OMG","Error somewhere");
        }



        return rootView;

    }

    public class launchParseDialog extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Getting Motor Park Drivers");
            // Set progressdialog message
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Locate the class table named "Country" in Parse.com
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "Driver");
            query.whereEqualTo("Category", "TRANSPORT");
            query.whereEqualTo("Active","Yes");

            try {
                ob = query.find();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            List<Taxi> myTaxi = new ArrayList<Taxi>();
            for (ParseObject driver : ob){
                Taxi t = Taxi.giveFullDetails(driver, getActivity().getApplicationContext());
                if (t != null) {
                    myTaxi.add(t);
                }
                myListView(myTaxi);


            }
            mProgressDialog.dismiss();


        }
    }

    public void myListView(final List theTaxis)  {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                List<Taxi> Taxis = theTaxis;
                Collections.sort(Taxis);
                TaxiAdapter adapter = new TaxiAdapter(getActivity().getApplication(),Taxis);
                adapter.notifyDataSetChanged();
                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Taxi thisTaxi = (Taxi)(adapterView.getItemAtPosition(position));
                        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                        globalVariable.setDriverName(thisTaxi.getDriverName());
                        globalVariable.setPhone(thisTaxi.getPhone());
                        globalVariable.setVechicleModel(thisTaxi.getVechicleModel());
                        globalVariable.setVehicleMake(thisTaxi.getVehicleMake());
                        globalVariable.setVehicleYear(thisTaxi.getVehicleYear());
                        globalVariable.setVehicleRegNo(thisTaxi.getVehicleRegNo());
                        globalVariable.setHasAC(thisTaxi.getHasAC());
                        globalVariable.setCategory(thisTaxi.getCategory());
                        globalVariable.setDocID(thisTaxi.getDocID());
                        globalVariable.setDriversLicenseNo(thisTaxi.getDriversLicenseNo());
                        globalVariable.setImageURL(thisTaxi.getImageURL());
                        globalVariable.setLatitude(thisTaxi.getLatitude());
                        globalVariable.setLongitude(thisTaxi.getLongitude());
                        globalVariable.setTimeOfCapture(thisTaxi.getTimeOfCapture());
globalVariable.setDistanceAway(("Approx. "+ (String.valueOf(roundTwoDecimals(thisTaxi.getDistanceFrom()/1000)  +" km away"))));
                        Intent i = new Intent(getActivity(),DriverDetails.class);
                       startActivity(i);
                    }
                });
                listView1.setAdapter(adapter);
            }
        });


    }

    float roundTwoDecimals(float d)
    {
        DecimalFormat twoDForm = new DecimalFormat("####.#");
        return Float.valueOf(twoDForm.format(d));

    }

    private URI createServerURI()
            throws URISyntaxException {
        URI uri = new URI("https://finditt.cloudant.com/kabba");
        return uri;
    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog =
                ProgressDialog.show(getActivity(), "Please wait ...",	"Searching for Car Hire Services around your location..", true);
        ringProgressDialog.setCancelable(false);
        //ringProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Create a replicator that replicates changes from the local
// datastore to t//he remote database.
                    final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

                  //  File path = getApplicationContext().getDir(DATASTORE_MANGER_DIR,MODE_PRIVATE);
                    // DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
                    // Datastore datastore = manager.openDatastore(DATASTORE_NAME);
                    PullReplication pull = new PullReplication();
                  //  pull.username = "stedualareakindayeaselle";
                  //  pull.password = "40LB1WEG0XcxT5jo47cssuO2";
                    pull.target = globalVariable.OpenDataStore(getActivity().getApplicationContext());
                    try{
                        pull.source = createServerURI();
                    } catch (URISyntaxException e){
                        e.printStackTrace();
                    }

                    final Replicator replicator = ReplicatorFactory.oneway(pull);
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
                        int nDocs = globalVariable.OpenDataStore(getActivity().getApplicationContext()).getDocumentCount();
                        showToast("Unable to connect, please check your data connection or try again");


                    }
                    else{
//Log.d("TAG","Replication completed successfully");
                       stopLocationUpdates();
                       // mGoogleApiClient.disconnect();
                        File path = getActivity().getDir(DATASTORE_MANGER_DIR, 1);
                        DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
                        Datastore datastore =  globalVariable.OpenDataStore(getActivity().getApplicationContext());

                        Log.d("store", "Store created");
                        IndexManager indexManager;
                        indexManager = new IndexManager(datastore);
                        indexManager.ensureIndexed("CabType", "Category");//create index called results on field category
                        indexManager.ensureIndexed("DocType", "DocType");//create index called results on field category

                        QueryBuilder query = new QueryBuilder();
                        query.index("CabType").oneOf("CAR HIRE SERVICES");
                        query.index("DocType").equalTo("Driver");

                        QueryResult result = indexManager.query(query.build());

                        int nDocs = datastore.getDocumentCount();
                       // showToast("number of docs" + nDocs);
                        List<BasicDocumentRevision> alldocs = datastore.getAllDocuments(0, nDocs, true);
                        List<Taxi> myTaxi = new ArrayList<Taxi>();
                        for (BasicDocumentRevision rev : result) {
                            Taxi t = Taxi.giveDetails(rev, getActivity().getApplicationContext());
                            if (t != null) {
                                myTaxi.add(t);
                            }
                            myListView(myTaxi);


                        }
                    }


                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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

    public void showToast(final String toast)
    {
       getActivity().runOnUiThread(new Runnable() {
           public void run() {
               Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
           }
       });
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the locationd
        // displayLocation();

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
    private void displayLocation() {
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            globalVariable.setMyLatitude(latitude);
            globalVariable.setMyLongitude(longitude);
            currLatitude = String.valueOf(latitude);
            currLongitude = String.valueOf(longitude);

//Toast.makeText(getActivity(),globalVariable.getMyLatitude().toString(),Toast.LENGTH_LONG).show();
        } else {
            globalVariable.setMyLatitude(0.00);
            globalVariable.setMyLongitude(0.00);
            Toast.makeText(getActivity().getApplicationContext(),"LOCATION IS TURNED OFF: Please turn on Location", Toast.LENGTH_LONG).show();

            Log.d(TAG,"(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        //Toast.makeText(getActivity(), "Location changed!",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Location has changed");

        // Displaying the new location on UI
        try{
            displayLocation();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
