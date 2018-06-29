package com.okason.diary.ui.location;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.data.LocationDao;
import com.okason.diary.models.Location;
import com.okason.diary.utils.Constants;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddLocationDialogFragment extends DialogFragment {
    private EditText addressEditText;
    private EditText locationName;
    private Location mLocation = null;
    private Realm realm;
    private LocationDao locationDao;




    public AddLocationDialogFragment() {
        // Required empty public constructor
    }


    public static AddLocationDialogFragment newInstance(String folderId){
        AddLocationDialogFragment dialogFragment = new AddLocationDialogFragment();
        if (!TextUtils.isEmpty(folderId)) {
            Bundle args = new Bundle();
            args.putString(Constants.FOLDER_ID, folderId);
            dialogFragment.setArguments(args);
        }
        return dialogFragment;
    }

    /**
     * The method gets the Location that was passed in
     */
    public void getCurrentLocation(){
        if (getArguments() != null && getArguments().containsKey(Constants.FOLDER_ID)){
            String locationId = getArguments().getString(Constants.FOLDER_ID);
            if (!TextUtils.isEmpty(locationId)){
                mLocation = locationDao.getLocationById(locationId);

            }
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        locationDao = new LocationDao(realm);
        final AlertDialog.Builder addLocationDialog = new AlertDialog.Builder(getActivity(), R.style.dialog);

        getCurrentLocation();
        if (savedInstanceState == null){


            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = inflater.inflate(R.layout.fragment_add_location_dialog, null);
            addLocationDialog.setView(convertView);

            View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mLocation != null ? getString(R.string.title_edit_folder) : getString(R.string.title_add_folder));
            addLocationDialog.setCustomTitle(titleView);

            addressEditText = (EditText)convertView.findViewById(R.id.edit_text_add_category);


            addLocationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addLocationDialog.setPositiveButton(mLocation != null ? getString(R.string.label_update) : getString(R.string.label_add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });



            if (mLocation != null && !TextUtils.isEmpty(mLocation.getAddress())){
                populateFields(mLocation);
                addressEditText.setSelection(addressEditText.getText().length());
            }


        }

        return addLocationDialog.create();
    }

    private void populateFields(Location location) {
        addressEditText.setText(location.getAddress());
    }

    private boolean requiredFieldCompleted(){
        if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            addressEditText.setError(getString(R.string.required));
            addressEditText.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();


        if (d != null){
            Button positiveButton = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean readyToCloseDialog = false;
                    if (requiredFieldCompleted()) {
                        saveFolder();
                        readyToCloseDialog = true;
                    }
                    if (readyToCloseDialog)
                        dismiss();
                }
            });
        }
    }



    private void saveFolder() {
//        final String address = addressEditText.getText().toString().trim();
//        if (!TextUtils.isEmpty(categoryName)) {
//            if (mFolder == null){
//                mFolder = locationDao.createNewFolder();
//            }
//            locationDao.updatedFolderTitle(mFolder.getId(), categoryName);
//
//            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mFolder.getId());
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, categoryName);
//            FirebaseAnalytics.getInstance(getActivity()).logEvent("add_category", bundle);
//
//        }

    }



}
