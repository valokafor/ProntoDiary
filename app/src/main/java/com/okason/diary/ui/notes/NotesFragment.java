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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.listeners.NoteItemListener;
import com.okason.diary.data.JournalDao;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Journal;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.ui.notedetails.NoteDetailActivity;
import com.okason.diary.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.okason.diary.R.style.dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, NoteItemListener
{

    private FirebaseAnalytics firebaseAnalytics;
    private Realm realm;
    private RealmResults<Journal> allJournals;
    private List<Journal> filteredJournals;
    private JournalDao journalDao;
    private final static String TAG = "NotesFragment";


    private MediaPlayer mPlayer = null;
    private boolean isAudioPlaying = false;

    private  String tagName = "";
    private String folderId = "";
    private String sortMethod = "";


    private View mRootView;
    private NotesAdapter mListAdapter;

    private boolean isDualScreen = false;
    private final static String LOG_TAG = "NoteListFragment";

    @BindView(R.id.note_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;
    @BindView(R.id.adView)
    AdView mAdView;






    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a Journal List Fragment
     *
     * @param dualScreen - indicates if this Fragment is participating in dual screen
     * @return - returns the created Fragment
     */
    public static NotesFragment newInstance(boolean dualScreen, String tagName, String folderId) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        if (dualScreen) {
            args.putBoolean(Constants.IS_DUAL_SCREEN, dualScreen);
        }
        if (!TextUtils.isEmpty(tagName)) {
            args.putString(Constants.TAG_FILTER, tagName);
        }
        if (!TextUtils.isEmpty(folderId)){
            args.putString(Constants.FOLDER_ID, folderId);
        }
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
        realm = Realm.getDefaultInstance();
        journalDao = new JournalDao(realm);
        filteredJournals = new ArrayList<>();
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        sortMethod = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort_options", "title");
        return mRootView;
    }



    private void goToImageGallery(Journal clickedJournal, Attachment clickedAttachment) {
        Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
        galleryIntent.putExtra(Constants.NOTE_ID, clickedJournal.getId());
        galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getLocalFilePath());
        startActivity(galleryIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
        fetchNotes();

        if (ProntoDiaryApplication.getProntoJournalUser() != null && ProntoDiaryApplication.getProntoJournalUser().isPremium()){
            //Do not show Ad
        }else {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    private void fetchNotes() {
        if (getArguments() != null && getArguments().containsKey(Constants.TAG_FILTER)){
            tagName = getArguments().getString(Constants.TAG_FILTER);
            allJournals = journalDao.getAllNotes(tagName).sort(sortMethod);
        } else if (getArguments() != null && getArguments().containsKey(Constants.FOLDER_ID)){
            folderId = getArguments().getString(Constants.FOLDER_ID);
            allJournals = journalDao.getNotesByFolder(folderId).sort(sortMethod);
        } else{
            allJournals = journalDao.getAllNotes("").sort(sortMethod);
        }



        allJournals.addChangeListener(changeListener);
        showNotes(allJournals);
    }


    @Override
    public void onPause() {
        super.onPause();
        allJournals.removeChangeListener(changeListener);
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
            filteredJournals = journalDao.filterNotes(query, tagName);
            showNotes(filteredJournals);
            return true;
        }
        return true;
    }



    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            filteredJournals = journalDao.filterNotes(newText, tagName);
            showNotes(filteredJournals);
            return true;
        }
        return true;
    }

    @Override
    public boolean onClose() {
        fetchNotes();
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListAdapter = new NotesAdapter(new ArrayList<>(), getContext());
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setNoteItemListener(this);
    }


    public void showNotes(List<Journal> journals) {
        if (journals != null && journals.size() > 0) {
            showEmptyText(false);
            mListAdapter = null;
            mListAdapter = new NotesAdapter(journals, getContext());
            mRecyclerView.setAdapter(mListAdapter);
            mListAdapter.setNoteItemListener(this);

        } else {
            showEmptyText(true);
        }


    }

    private final OrderedRealmCollectionChangeListener<RealmResults<Journal>> changeListener =
            new OrderedRealmCollectionChangeListener<RealmResults<Journal>>() {
        @Override
        public void onChange(RealmResults<Journal> allJournals, OrderedCollectionChangeSet changeSet) {
            // `null`  means the async query returns the first time.
            if (changeSet == null) {
                if (allJournals != null && allJournals.size() > 0) {
                    mListAdapter.replaceData(allJournals);
                    showEmptyText(false);
                }
                return;
            }

            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                mListAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                mListAdapter.notifyItemRangeInserted(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                mListAdapter.notifyItemRangeChanged(range.startIndex, range.length);
            }
        }
    };


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


    public void showDeleteConfirmation(Journal journal) {
        boolean shouldPromptForDelete = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("prompt_for_delete", true);
        if (shouldPromptForDelete) {
            promptForDelete(journal);
        } else {
            deleteNote(journal.getId());
        }

    }

    private void deleteNote(String noteId) {
        if (!TextUtils.isEmpty(noteId)) {
            //dataAccessManager.deleteJournal(journal.getId());
            new JournalDao(realm).deleteJournal(noteId);
        }
    }


    public void showSingleDetailUi(Journal selectedJournal) {
        String id = selectedJournal.getId();
        startActivity(NoteDetailActivity.getStartIntent(getContext(), id));
    }


    public void showDualDetailUi(Journal journal) {
        NoteListActivity activity = (NoteListActivity)getActivity();
     //   activity.showTwoPane(journal);
    }

    public void promptForDelete(final Journal journal) {
        String content;
        if (!TextUtils.isEmpty(journal.getContent())) {
            content = journal.getContent();
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
               deleteNote(journal.getId());
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
            Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT);
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


    @Override
    public void onDestroy() {
        if (realm != null){
            realm.close();;
            realm = null;
        }
        super.onDestroy();
    }

    @Override
    public void onNoteClick(Journal clickedJournal) {
        if (isDualScreen) {
            showDualDetailUi(clickedJournal);
        } else {
            showSingleDetailUi(clickedJournal);
        }

    }

    @Override
    public void onDeleteButtonClicked(Journal clickedJournal) {
        showDeleteConfirmation(clickedJournal);
    }

    @Override
    public void onAttachmentClicked(Journal clickedJournal, int position) {
        //An attachment in the Journal list has been clicked
        Attachment clickedAttachment = clickedJournal.getAttachments().get(clickedJournal.getAttachments().size() - 1);
        if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)){
            //Play Audio
            startPlaying(clickedAttachment, position);
        }else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_VIDEO)){
            //Play Video
            viewMedia(clickedAttachment);
        }else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)){
            //Show Image Gallery
            goToImageGallery(clickedJournal, clickedAttachment);
        }else if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)){
            //Show file

        }else {
            //Show details page
        }

    }
}
