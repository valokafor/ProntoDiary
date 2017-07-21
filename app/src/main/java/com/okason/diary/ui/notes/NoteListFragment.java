package com.okason.diary.ui.notes;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.listeners.NoteItemListener;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.ui.auth.AuthUiActivity;
import com.okason.diary.ui.notedetails.NoteDetailActivity;
import com.okason.diary.utils.Constants;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Realm mRealm;
    private RealmResults<Note> mNotes;


    private MediaPlayer mPlayer = null;
    private boolean isAudioPlaying = false;


    private View mRootView;
    private NoteListAdapter mListAdapter;

    private boolean isDualScreen = false;
    private final static String LOG_TAG = "NoteListFragment";

    @BindView(R.id.note_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;
//    @BindView(R.id.adView)
//    AdView mAdView;





    public NoteListFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a Note List Fragment
     * @param dualScreen - indicates if this Fragment is participating in dual screen
     * @return - returns the created Fragment
     */
    public static NoteListFragment newInstance(boolean dualScreen){
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_DUAL_SCREEN, dualScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args  != null && args.containsKey(Constants.IS_DUAL_SCREEN)){
            isDualScreen = args.getBoolean(Constants.IS_DUAL_SCREEN);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        ButterKnife.bind(this, mRootView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        return mRootView;
    }



    private void goToImageGallery(Note clickedNote, Attachment clickedAttachment) {
        Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
        galleryIntent.putExtra(Constants.NOTE_ID, clickedNote.getId());
        galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getLocalFilePath());
        startActivity(galleryIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListAdapter = null;

        if (ProntoDiaryApplication.isCloudSyncEnabled()) {
            try {

                mRealm = Realm.getDefaultInstance();
                mNotes = mRealm.where(Note.class).findAll();
                mNotes.addChangeListener(new RealmChangeListener<RealmResults<Note>>() {
                    @Override
                    public void onChange(RealmResults<Note> notes) {
                        showNotes(mNotes);
                    }
                });
                showNotes(mNotes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showEmptyText(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mRealm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id){
            case R.id.action_add:
                if (getActivity() != null) {
                    if (ProntoDiaryApplication.isCloudSyncEnabled()) {
                        startActivity(new Intent(getActivity(), AddNoteActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), AuthUiActivity.class));
                    }
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    public void showNotes(List<Note> notes) {
        if (notes != null && notes.size() > 0){
            showEmptyText(false);
            mListAdapter = null;


            mListAdapter = new NoteListAdapter(notes, getContext());
            mRecyclerView.setAdapter(mListAdapter);

            mListAdapter.setNoteItemListener(new NoteItemListener() {
                @Override
                public void onNoteClick(Note clickedNote) {
                    if (isDualScreen) {
                        showDualDetailUi(clickedNote);
                    } else {
                        showSingleDetailUi(clickedNote);
                    }
                }

                @Override
                public void onDeleteButtonClicked(Note clickedNote) {
                    showDeleteConfirmation(clickedNote);
                }

                @Override
                public void onAttachmentClicked(Note clickedNote, int position) {
                    //An attachment in the Note list has been clicked
                    Attachment clickedAttachment = clickedNote.getAttachments().get(clickedNote.getAttachments().size() - 1);
                    if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)){
                        //Play Audio
                        startPlaying(clickedAttachment, position);
                    }else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_VIDEO)){
                        //Play Video
                        viewMedia(clickedAttachment);
                    }else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)){
                        //Show Image Gallery
                        goToImageGallery(clickedNote, clickedAttachment);
                    }else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)){
                        //Show file

                    }else {
                        //Show details page
                    }
                }
            });

        }else {
            showEmptyText(true);
        }


    }


    public void showEmptyText(boolean showText) {
        if (showText){

            mEmptyText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
          //  mAdView.setVisibility(View.GONE);

        }else {
          //  mAdView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }


    public void showDeleteConfirmation(Note note) {
        boolean shouldPromptForDelete = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("prompt_for_delete", true);
        if (shouldPromptForDelete) {
            promptForDelete(note);
        } else {
            deleteNote(note);
        }

    }

    private void deleteNote(Note note) {
        if (!TextUtils.isEmpty(note.getId())) {
            new NoteRealmRepository().deleteNote(note.getId());
        }
    }



    public void showSingleDetailUi(Note selectedNote) {
        String id = selectedNote.getId();
        startActivity(NoteDetailActivity.getStartIntent(getContext(), id));
    }


    public void showDualDetailUi(Note note) {
//        NoteListActivity activity = (NoteListActivity)getActivity();
//        activity.showTwoPane(note);
    }

    public void promptForDelete(final Note note){
        String content;
        if (!TextUtils.isEmpty(note.getContent())) {
            content = note.getContent();
        } else {
            content = "";
        }
        String message =  getString(R.string.label_delete)  + " " + content.substring(0, Math.min(content.length(), 50)) + "  ... ?";


        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext(), R.style.dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(getString(R.string.warning_are_you_sure));
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(note);
            }
        });
        alertDialog.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void startPlaying(Attachment attachment, final int position) {
        if (isAudioPlaying) {
            mPlayer.stop();
            mPlayer.release();
            mListAdapter.setAudioPlaying(false, position);
            isAudioPlaying = false;

        } else {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(attachment.getFilePath());
                mPlayer.prepare();
                mPlayer.start();
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (!mp.isPlaying()){
                            mListAdapter.setAudioPlaying(false, position);
                            isAudioPlaying = false;
                        }
                    }
                });
                isAudioPlaying = true;
                mListAdapter.setAudioPlaying(true, position);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Play audio failed " + e.getLocalizedMessage());
            }
        }

    }

    private void viewMedia(Attachment attachment) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(attachment.getFilePath()), attachment.getMime_type());
        startActivity(intent);
    }







}
