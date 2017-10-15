package com.okason.diary.ui.notes;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.JournalListChangeEvent;
import com.okason.diary.core.listeners.NoteItemListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.ui.addnote.DataAccessManager;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.ui.notedetails.NoteDetailActivity;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.okason.diary.R.style.dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteListFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private FirebaseAnalytics firebaseAnalytics;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DataAccessManager dataAccessManager;


    private List<Note> unFilteredNotes;
    private List<Note> filteredNotes;


    private MediaPlayer mPlayer = null;
    private boolean isAudioPlaying = false;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private String sortColumn = "";
    private  String tagName = "";


    private View mRootView;
    private NoteListAdapter mListAdapter;

    private boolean isDualScreen = false;
    private final static String LOG_TAG = "NoteListFragment";

    @BindView(R.id.note_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;
    @BindView(R.id.adView)
    AdView mAdView;

    private FloatingActionButton floatingActionButton;
    private ValueEventListener valueEventListener;



    public NoteListFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a Note List Fragment
     *
     * @param dualScreen - indicates if this Fragment is participating in dual screen
     * @return - returns the created Fragment
     */
    public static NoteListFragment newInstance(boolean dualScreen, String tagName) {
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_DUAL_SCREEN, dualScreen);
        args.putString(Constants.TAG_FILTER, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.IS_DUAL_SCREEN)) {
            isDualScreen = args.getBoolean(Constants.IS_DUAL_SCREEN);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        ButterKnife.bind(this, mRootView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            //User must be logged in to proceed
            startActivity(new Intent(getActivity(), NoteListActivity.class));
        }

        filteredNotes = new ArrayList<>();
        unFilteredNotes = new ArrayList<>();


        floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddNoteActivity.class));
            }
        });
        return mRootView;
    }


    private void goToImageGallery(Note clickedNote, Attachment clickedAttachment) {
        Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
        Gson gson = new Gson();
        String attachmentJson = gson.toJson(clickedNote.getAttachments());
        galleryIntent.putExtra(Constants.SERIALIZED_ATTACHMENT_ID, attachmentJson);
        galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getLocalFilePath());
        galleryIntent.putExtra(Constants.NOTE_TITLE, clickedNote.getTitle());
        startActivity(galleryIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (firebaseUser != null){

            dataAccessManager = new DataAccessManager(firebaseUser.getUid());
            sortColumn = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.label_title),
                    getString(R.string.label_title));

            sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            boolean first_run = sharedPreferences.getBoolean(Constants.FIRST_RUN, true);
            if (first_run) {
                dataAccessManager.addInitialNotesToFirebase();

                editor.putBoolean(Constants.FIRST_RUN, false).commit();
            }
            if (getArguments() != null && getArguments().containsKey(Constants.TAG_FILTER)){
                tagName = getArguments().getString(Constants.TAG_FILTER);
            }
            dataAccessManager.getAllJournal(tagName);

        }
        if (ProntoDiaryApplication.getProntoDiaryUser() != null && ProntoDiaryApplication.getProntoDiaryUser().isPremium()){
            //Do not show Ad
        }else {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJournalListChange(JournalListChangeEvent event){
        showNotes(event.getJournalList());
    }





//    private void populateNoteList() {
//

//        journalCloudReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    unFilteredNotes.clear();
//                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
//                        Note note = noteSnapshot.getValue(Note.class);
//                        if (TextUtils.isEmpty(tagName)) {
//                            unFilteredNotes.add(note);
//                        } else {
////                            for (Tag tag: note.getTags()){
////                                if (tag.getTagName().equals(tagName)){
////                                    unFilteredNotes.add(note);
////                                    break;
////                                }
////                            }
//                        }
//                    }
//                    showNotes(unFilteredNotes);
//                }else {
//                    showEmptyText(true);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                makeToast("Error fetching data " + databaseError.getMessage());
//            }
//        });
//
//
//    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_note_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * Handles Toolbar Search
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() > 0) {
            filteredNotes = filterNotes(query);
            showNotes(filteredNotes);
            return true;
        }
        return true;
    }

    private List<Note> filterNotes(String query) {
        return null;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }

    @Override
    public boolean onClose() {
        showNotes(unFilteredNotes);
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    public void showNotes(List<Note> notes) {
        if (notes != null && notes.size() > 0) {
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
                    List<Attachment> attachmentList = clickedNote.getAttachments();
                    Attachment clickedAttachment = attachmentList.get(attachmentList.size() - 1);
                    if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)) {
                        //Play Audio
                        startPlaying(clickedAttachment, position);
                    } else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_VIDEO)) {
                        //Play Video
                        viewMedia(clickedAttachment);
                    } else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)) {
                        //Show Image Gallery
                        goToImageGallery(clickedNote, clickedAttachment);
                    } else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)) {
                        //Show file

                    } else {
                        //Show details page
                    }
                }
            });

        } else {
            showEmptyText(true);
        }


    }


    public void showEmptyText(boolean showText) {
        if (showText) {

            mEmptyText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            //  mAdView.setVisibility(View.GONE);

        } else {
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
        dataAccessManager.deleteNote(note.getId());
    }


    public void showSingleDetailUi(Note selectedNote) {
        Gson gson = new Gson();
        String serializedNote = gson.toJson(selectedNote);
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
        intent.putExtra(Constants.SERIALIZED_JOURNAL, serializedNote);
        startActivity(intent);
    }


    public void showDualDetailUi(Note note) {
//        NoteListActivity activity = (NoteListActivity)getActivity();
//        activity.showTwoPane(note);
    }

    public void promptForDelete(final Note note) {
        String content;
        if (!TextUtils.isEmpty(note.getContent())) {
            content = note.getContent();
        } else {
            content = "";
        }
        String message = getString(R.string.label_delete) + " " + content.substring(0, Math.min(content.length(), 50)) + "  ... ?";


        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext(), dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View) inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView) titleView.findViewById(R.id.text_view_dialog_title);
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

    private void makeToast(String message) {
        try {
            Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
            TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        if (!mp.isPlaying()) {
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
