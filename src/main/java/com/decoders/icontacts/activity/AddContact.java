package com.decoders.icontacts.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.decoders.icontacts.R;
import com.decoders.icontacts.model.ContactModel;
import com.decoders.icontacts.utils.RealmFunctions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddContact extends AppCompatActivity {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.notes)
    EditText notes;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.cancel)
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);
    }

    /*
    click listeners
     */
    @OnClick({R.id.save, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                // save contact
                ContactModel model = new ContactModel();
                model.setName(name.getText().toString());
                model.setPhone(phone.getText().toString());
                model.setAddress(address.getText().toString());
                model.setNotes(notes.getText().toString());
                RealmFunctions.storeContact(model);
                Toast.makeText(AddContact.this, "Contact Saved!", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.cancel:
                // cancel and close the screen
                finish();
                break;
        }
    }
}
