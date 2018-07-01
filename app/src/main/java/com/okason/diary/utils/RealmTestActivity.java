package com.okason.diary.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.okason.diary.R;
import com.okason.diary.models.ProntoTag;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class RealmTestActivity extends AppCompatActivity {

    public   Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_test);


        Button button1 = findViewById(R.id.button2);

        Button button2 = findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpRealm();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        ProntoTag tag = new ProntoTag();
                        tag.setTagName("Dance Tag");
                        tag.setId(UUID.randomUUID().toString());
                        realm.insert(tag);
                    }
                });
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpRealm();
                RealmResults<ProntoTag> tags = realm.where(ProntoTag.class).findAllAsync();
                int size = tags.size();
                Log.d("RealmTest", "Size " + size);
            }
        });
    }


    public void setUpRealm() {
        String url = "realms://pronto-diary.us1.cloud.realm.io/~/journal";
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(url)
                .build();
        realm = Realm.getInstance(configuration);
    }
}
