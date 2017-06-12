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
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.listeners.NoteItemListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.ui.notedetails.NoteDetailActivity;
import com.okason.diary.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mDatabase;
    private DatabaseReference noteCloudReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private ValueEventListener mValueEventListener;

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
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;



    public NoteListFragment() {
        // Required empty public constructor
    }

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        noteCloudReference =  mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_END_POINT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        ButterKnife.bind(this, mRootView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mListAdapter = new NoteListAdapter(new ArrayList<Note>(), getContext());
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Note> notes = new ArrayList<>();
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    Note note = noteSnapshot.getValue(Note.class);
                    notes.add(note);
                }
                if (notes != null && notes.size() > 0){
                    showEmptyText(false);
                    showNotes(notes);
                    setProgressIndicator(false);
                }else {
                    showEmptyText(true);
                    setProgressIndicator(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast(databaseError.getMessage());

            }
        };




        //Pull to refresh
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.primary),
                ContextCompat.getColor(getActivity(), R.color.accent),
                ContextCompat.getColor(getActivity(), R.color.primary_dark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noteCloudReference.removeEventListener(mValueEventListener);
                noteCloudReference.addValueEventListener(mValueEventListener);
            }
        });

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

        return mRootView;
    }

    private void goToImageGallery(Note clickedNote, Attachment clickedAttachment) {
        Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
        Gson gson = new Gson();
        String serializedNote = gson.toJson(clickedNote);
        galleryIntent.putExtra(Constants.SERIALIZED_NOTE, serializedNote);
        galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getLocalFilePath());
        startActivity(galleryIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        setProgressIndicator(true);
        noteCloudReference.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        noteCloudReference.removeEventListener(mValueEventListener);
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
                    startActivity(new Intent(getActivity(), AddNoteActivity.class));
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    public void showNotes(List<Note> notes) {
        mListAdapter.replaceData(notes);
    }


    public void showEmptyText(boolean showText) {
        if (showText){
            swipeRefreshLayout.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
          //  mAdView.setVisibility(View.GONE);

        }else {
          //  mAdView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
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
            noteCloudReference.child(note.getId()).removeValue();
        }
    }


    public void setProgressIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });

    }

    public void showSingleDetailUi(Note selectedNote) {
        Gson gson = new Gson();
        String serializedNote = gson.toJson(selectedNote);
       startActivity(NoteDetailActivity.getStartIntent(getContext(), serializedNote));
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


        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
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
