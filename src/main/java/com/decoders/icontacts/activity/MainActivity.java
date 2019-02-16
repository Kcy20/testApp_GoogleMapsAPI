package com.decoders.icontacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.decoders.icontacts.R;
import com.decoders.icontacts.adapter.ContactsAdapter;
import com.decoders.icontacts.application.MyApp;
import com.decoders.icontacts.model.ContactModel;
import com.decoders.icontacts.utils.RealmFunctions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.noresult)
    TextView noresult;
    ArrayList<ContactModel> models = new ArrayList<>();
    ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    /*
    function to load contacts from realm and show
     */
    private void loadContacts() {
        models = RealmFunctions.getAllContacts();
        if(contactsAdapter == null){
            contactsAdapter = new ContactsAdapter(this, models);
            listView.setAdapter(contactsAdapter);
            // list item click
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // open contact details screen
                    Intent intent = new Intent(MainActivity.this, ContactDetails.class);
                    intent.putExtra("id", models.get(position).getId());
                    intent.putExtra("name", models.get(position).getName());
                    intent.putExtra("phone", models.get(position).getPhone());
                    intent.putExtra("address", models.get(position).getAddress());
                    intent.putExtra("notes", models.get(position).getNotes());
                    startActivity(intent);
                }
            });
            // list item long click
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                    // show delete popup
                    PopupMenu popup = new PopupMenu(MainActivity.this, view);
                    popup.getMenuInflater().inflate(R.menu.delete, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            // delete contact
                            Realm realm = MyApp.getRealmDatabaseInstance();
                            if (realm.isInTransaction())
                                realm.cancelTransaction();
                            realm.beginTransaction();
                            ContactModel toDelete = realm.where(ContactModel.class).equalTo("id", models.get(position).getId()).findFirst();
                            if(toDelete != null){
                                toDelete.deleteFromRealm();
                            }
                            realm.commitTransaction();
                            loadContacts();
                            return true;
                        }
                    });
                    popup.show();
                    return true;
                }
            });
        }
        else{
            contactsAdapter.refreshAdapter(models);
        }

        if(models.size() == 0){
            noresult.setVisibility(View.VISIBLE);
        }
        else{
            noresult.setVisibility(View.GONE);
        }
    }

    /*
    click listener
     */
    @OnClick(R.id.fab)
    public void onViewClicked() {
        // open add contact screen
        Intent intent = new Intent(this, AddContact.class);
        startActivity(intent);
    }
}
