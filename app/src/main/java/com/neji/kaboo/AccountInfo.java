package com.neji.kaboo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.eventbus.Subscribe;
import com.parse.ParseUser;

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
public class AccountInfo extends Fragment {
    private static final String TAG = AccountInfo.class.getSimpleName();
    private TextView txtAccName;
    private TextView txtEmail;
    private TextView txtPhone;
    private RelativeLayout changePass;
private Button btnLogout;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account, container, false);
txtAccName = (TextView)rootView.findViewById(R.id.txtusername);
        txtEmail = (TextView)rootView.findViewById(R.id.txtemail);
changePass = (RelativeLayout)rootView.findViewById(R.id.sign_up_email_button);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        btnLogout = (Button)rootView.findViewById(R.id.login_button);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.logOut();
                Toast.makeText(getActivity(),"You have successfully logged out",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        ParseUser currentUser = ParseUser.getCurrentUser();
        txtAccName.setText(currentUser.getUsername());
        txtEmail.setText(currentUser.getEmail());





        return rootView;

    }









}
