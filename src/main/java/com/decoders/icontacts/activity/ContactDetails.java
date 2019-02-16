package com.decoders.icontacts.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.decoders.icontacts.R;
import com.decoders.icontacts.application.MyApp;
import com.decoders.icontacts.model.ContactModel;
import com.decoders.icontacts.utils.MyLogger;
import com.decoders.icontacts.utils.RealmFunctions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class ContactDetails extends AppCompatActivity {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.callPhone)
    Button callPhone;
    @BindView(R.id.notes)
    EditText notes;
    int id_;
    String name_, phone_, address_, notes_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        ButterKnife.bind(this);

        // check call phone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 100);
        }

        // get data from intent
        id_ = getIntent().getIntExtra("id", 0);
        name_ = getIntent().getStringExtra("name");
        phone_ = getIntent().getStringExtra("phone");
        address_ = getIntent().getStringExtra("address");
        notes_ = getIntent().getStringExtra("notes");

        showData();
    }

    private void showData() {
        name.setText(name_);
        phone.setText(phone_);
        address.setText(address_);
        notes.setText(notes_);
        getLocationFromAddress(address_);
    }

    /*
    click listeners
     */
    @OnClick({R.id.callPhone, R.id.save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.callPhone:
                // call on phone number
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone_));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
                break;
            case R.id.save:
                // empty checks
                if(name.getText().toString().isEmpty()){
                    name.setError("Required");
                    return;
                }
                if(phone.getText().toString().isEmpty()){
                    phone.setError("Required");
                    return;
                }
                if(address.getText().toString().isEmpty()){
                    address.setError("Required");
                    return;
                }
                if(notes.getText().toString().isEmpty()){
                    notes.setError("Required");
                    return;
                }
                // update contact
                Realm realm = MyApp.getRealmDatabaseInstance();
                if (realm.isInTransaction())
                    realm.cancelTransaction();
                realm.beginTransaction();
                ContactModel model = new ContactModel();
                model.setId(id_);
                model.setName(name.getText().toString());
                model.setPhone(phone.getText().toString());
                model.setAddress(address.getText().toString());
                model.setNotes(notes.getText().toString());
                realm.copyToRealmOrUpdate(model);
                realm.commitTransaction();
                Toast.makeText(ContactDetails.this, "Contact Updated!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /*
    function to get latitude/longitude from address and show on map
     */
    public void getLocationFromAddress(String strAddress){
        MyLogger.loge("search address: "+strAddress);
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address == null) {
                MyLogger.loge("null");
                return;
            }
            if(address.size() == 0) {
                MyLogger.loge("empty");
                return;
            }
            final Address location = address.get(0);
            MyLogger.loge("latitude: "+location.getLatitude());
            MyLogger.loge("longitude: "+location.getLongitude());
            // show location on map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // set marker on map
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Address"));
                        // move camera to marker position
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                    }
                });
            }
            else{
                MyLogger.loge("fragment null");
            }

        } catch (IOException e) {
            // error happened
            e.printStackTrace();
            Toast.makeText(ContactDetails.this, "Address service not available!", Toast.LENGTH_SHORT).show();
        }
    }
}
