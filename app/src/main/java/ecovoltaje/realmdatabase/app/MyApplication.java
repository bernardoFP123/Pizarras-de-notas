package ecovoltaje.realmdatabase.app;

import android.app.Application;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

import ecovoltaje.realmdatabase.models.Board;
import ecovoltaje.realmdatabase.models.Note;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Bernardo_NoAdmin on 12/06/2017.
 */

public class MyApplication extends Application {

    public static AtomicInteger BoardID = new AtomicInteger();
    public static AtomicInteger NoteID = new AtomicInteger();
    @Override
    public void onCreate() {
        super.onCreate();


        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        BoardID = getIdByTable(realm,Board.class);
        NoteID = getIdByTable(realm, Note.class);
        realm.close();




    }

    private void setUpRealmConfig(){

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                                        .deleteRealmIfMigrationNeeded()
                                        .build();
        Realm.setDefaultConfiguration(config);
    }


    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0 ) ? new AtomicInteger( results.max("id").intValue()) : new AtomicInteger();

    }



}
