package com.neji.kaboo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.common.eventbus.Subscribe;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Created by Neji on 26/02/2015.
 */
public class DriverRating extends Activity {
    private String driverName = "";
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    private String docID = "";
    private static final String DATASTORE_MANGER_DIR = "kabooData";
    private static final String DATASTORE_NAME = "kabba";
    ImageView driverImage;
    TextView comments;
    private String username;
    private String dateComment;
    private String rating;
    private Button saveRating;
    private RatingBar ratingBar;
    private TextView txtDriverName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating);
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        txtDriverName = (TextView)findViewById(R.id.txtDriverName);
        driverImage = (ImageView)findViewById(R.id.profile_image);
        comments = (TextView)findViewById(R.id.txtComments);
//        saveRating = (Button)findViewById(R.id.btnRateDriver);
ratingBar = (RatingBar)findViewById(R.id.ratebar);
        mProgressDialog = new ProgressDialog(this);

username = ParseUser.getCurrentUser().toString();
        Picasso.with(this)
                .load(globalVariable.getImageURL())
                .placeholder(R.drawable.taxi) // optional
                .error(R.drawable.photo)         // optional
                .into(driverImage);

        txtDriverName.setText(globalVariable.getDriverName());
        docID = globalVariable.getDocID();
        driverName = globalVariable.getDriverName();
        docID = globalVariable.getDocID();

    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(DriverRating.this, "Please wait ...",	"Uploading your rating ...", true);
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
                    BasicDocumentRevision retrieved = datastore.getDocument(docID);
                    //do the update on latitude and longitude
                   // MutableDocumentRevision update = retrieved.mutableCopy();
                   // Map<String, Object> json = retrieved.getBody().asMap();
                    MutableDocumentRevision rev = new MutableDocumentRevision();
                    //Build up document content
                    Map<String,Object> json = new HashMap<String, Object>();
                    json.put("DocType", "Rating");
                    json.put("rating", String.valueOf(ratingBar.getRating()));
                    json.put("driverName", driverName);
                    json.put("driverID", docID);
                    json.put("userName", username);
                    json.put("comments",comments.getText().toString());
                    Time today = new Time(Time.getCurrentTimezone());
                    today.setToNow();
                    json.put("RateDate", today.monthDay + "/" + today.month + "/" + today.year);
                    json.put("RateTime", today.format("%k:%M:%S"));


                    rev.body = DocumentBodyFactory.create(json);
                    DocumentRevision client = datastore.createDocumentFromRevision(rev);

                    PushReplication push = new PushReplication();
                   // push.username = "stedualareakindayeaselle";
                   // push.password = "40LB1WEG0XcxT5jo47cssuO2";
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
        URI uri = new URI("https://finditt.cloudant.com/kabba");
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

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(DriverRating.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog(){
        runOnUiThread(new Runnable() {
            public void run()
            {
        AlertDialog alertDialog = new AlertDialog.Builder(DriverRating.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success..");

        // Setting Dialog Message
        alertDialog.setMessage("Rating posted successfully");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                // Toast.makeText(getApplicationContext(),"New Location created", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DriverRating.this,DriverDetails.class);
                startActivity(i);
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
            }
        });
    }

    public void saveRating(View view){
      new launchParseDialog().execute();
    }

    public class launchParseDialog extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // Set progressdialog title
            mProgressDialog.setTitle("Posting Driver Rating");
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
            ParseObject newRating = new ParseObject("Rating");
            newRating.put("DocType", "Rating");
            newRating.put("rating", String.valueOf(ratingBar.getRating()));
            newRating.put("driverName", driverName);
            newRating.put("driverID", docID);
            newRating.put("userName", username);
            newRating.put("comments",comments.getText().toString());
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            newRating.put("RateDate", today.monthDay + "/" + today.month + "/" + today.year);
            newRating.put("RateTime", today.format("%k:%M:%S"));


            try {
                newRating.saveInBackground();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            mProgressDialog.dismiss();
            showSuccessDialog();


        }
    }


}
