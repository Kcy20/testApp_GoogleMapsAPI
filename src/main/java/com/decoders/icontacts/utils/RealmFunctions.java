package com.decoders.icontacts.utils;

import android.util.Log;

import com.decoders.icontacts.application.MyApp;
import com.decoders.icontacts.model.ContactModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmFunctions {

    /*
    function to add a new contact
     */
    public static void storeContact(ContactModel model){
        if(model == null)
            return;
        Realm realm = MyApp.getRealmDatabaseInstance();
        int maxId = getLastId();
        maxId++;
        model.setId(maxId);
        if(realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
    }

    /*
    inner function to set new contact id
     */
    private static int getLastId() {
        Realm realm = MyApp.getRealmDatabaseInstance();
        if(realm.where(ContactModel.class).max("id") != null){
            int maxId = realm.where(ContactModel.class).max("id").intValue();
            Log.e("getLastId", "maxId: "+maxId);
            return maxId;
        }
        return 0;
    }

    /*
    function to get list of all contacts
     */
    public static ArrayList<ContactModel> getAllContacts(){
        ArrayList<ContactModel> models = new ArrayList<>();
        Realm realm = MyApp.getRealmDatabaseInstance();
        RealmResults<ContactModel> realmResults = realm.where(ContactModel.class).findAllAsync().sort("id",Sort.DESCENDING);
        if(realmResults != null){
            models.addAll(realmResults);
        }
        return models;
    }
}
