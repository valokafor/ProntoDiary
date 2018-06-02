package com.okason.diary.ui.notedetails;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.data.NoteDao;
import com.okason.diary.models.Journal;
import com.okason.diary.utils.Constants;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDetailDialogFragment extends DialogFragment {

    private View rootView;
    private Realm realm;
    private Journal currentJournal;
    private String title;

    public static NoteDetailDialogFragment newInstance(String noteId){
        NoteDetailDialogFragment fragment = new NoteDetailDialogFragment();
        if (!TextUtils.isEmpty(noteId)){
            Bundle args = new Bundle();
            args.putString(Constants.NOTE_ID, noteId);
            fragment.setArguments(args);
        }

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = realm.getDefaultInstance();
    }

    public NoteDetailDialogFragment() {
        // Required empty public constructor
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder addFolderDialog = new AlertDialog.Builder(getActivity(), R.style.dialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View convertView = inflater.inflate(R.layout.fragment_note_detail_dialog, null);
        addFolderDialog.setView(convertView);



        EditText noteSummary = convertView.findViewById(R.id.edit_text_summary);
        String currentNoteId = getPassedInNoteId();
        if (!TextUtils.isEmpty(currentNoteId)){
            currentJournal = new NoteDao(realm).getNoteEntityById(currentNoteId);
            if (currentJournal != null){
                title = currentJournal.getTitle();
                noteSummary.setText(currentJournal.getContent());
            }
        }

        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(TextUtils.isEmpty(title) ? "Journal Details" : title);
        addFolderDialog.setCustomTitle(titleView);

        addFolderDialog.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });



        Dialog dialog = addFolderDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    private String getPassedInNoteId() {
        if (getArguments() != null && getArguments().containsKey(Constants.NOTE_ID)){
            String noteId = getArguments().getString(Constants.NOTE_ID);
            return noteId;
        }
        return "";

    }


    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();


        if (dialog != null){
            if (dialog != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
