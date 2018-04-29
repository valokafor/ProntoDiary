package com.okason.diary.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.okason.diary.models.realmentities.NoteEntity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by valokafor on 4/28/18.
 */

public class NoteViewModel extends ViewModel {
    private Realm realmDatabase;
    private NoteDao noteDao;

    private final MutableLiveData<List<NoteEntity>> observableNoteEntitys = new MutableLiveData<>();
    private RealmResults<NoteEntity> results;
    private RealmChangeListener<RealmResults<NoteEntity>> realmChangeListener = (results) -> {
        if (results.isLoaded()  && results.isValid()){
            observableNoteEntitys.setValue(results);
        }
    };



    public void NoteViewModel(){
        realmDatabase = Realm.getDefaultInstance();
        noteDao = new NoteDao(realmDatabase);
        //If using async Query API, the change listener will set the loaded results.
        observableNoteEntitys.setValue(null);
        results = noteDao.getAllNoteEntitys();
        results.addChangeListener(realmChangeListener);

    }

    public LiveData<List<NoteEntity>> getNotes(){
        return observableNoteEntitys;
    }



    @Override
    protected void onCleared() {
        results.removeChangeListener(realmChangeListener);
        realmDatabase.close();
        realmDatabase = null;


    }
}
