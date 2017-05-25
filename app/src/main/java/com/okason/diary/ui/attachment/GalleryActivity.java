package com.okason.diary.ui.attachment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.okason.diary.R;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.notes.NoteListContract;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user interaction before hiding the
     * * system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility
     * * upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    @BindView(R.id.gallery_root)
    LinearLayout galleryRootView;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private List<Attachment> attachments;


    private NoteListContract.Repository repository;

    //This Id is used to go back to the Note that has this attachment
    private Note parentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().hasExtra(Constants.NOTE_ID)) {
            repository = new NoteRealmRepository();
            initViews();
            initData();
        } else {
            finish();
        }


    }

    private void initData() {
        String noteId = getIntent().getStringExtra(Constants.NOTE_ID);
        parentNote = repository.getNoteById(noteId);
        String selectAttachmentId = getIntent().getStringExtra(Constants.SELECTED_ID);
        int selectedPosition = 0;

        if (parentNote != null) {
            //Create an Arraylist to hold Ids of Attachments that are image or Video
            attachments = new ArrayList<Attachment>();

            for (Attachment attachment : parentNote.getAttachments()) {
                if (Constants.MIME_TYPE_IMAGE.equals(attachment.getMime_type())
                        || Constants.MIME_TYPE_SKETCH.equals(attachment.getMime_type())
                        || Constants.MIME_TYPE_VIDEO.equals(attachment.getMime_type())) {
                    attachments.add(attachment);

                }
            }


            for (int i = 0; i < attachments.size(); i++) {
                if (attachments.get(i).getId().equals(selectAttachmentId)) {
                    selectedPosition = i;
                    break;
                }
            }


            AttachmentPagerAdapter pagerAdapter = new AttachmentPagerAdapter(getSupportFragmentManager(), attachments);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.setAdapter(pagerAdapter);
            mViewPager.setCurrentItem(selectedPosition);

            getSupportActionBar().setTitle(parentNote.getTitle());
            getSupportActionBar().setSubtitle("(" + (selectedPosition + 1) + "/" + attachments.size() + ")");

        }


        //If selected attachment is a video it will be immediately played
        if (attachments.get(selectedPosition).getMime_type().equals(Constants.MIME_TYPE_VIDEO)) {
            viewMedia();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gallery, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_gallery_share:
                // shareMedia();
                break;
            case R.id.menu_gallery:
                //viewMedia();
                break;
            default:
                Log.e(Constants.TAG, "Wrong element choosen: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }


    private void initViews() {
        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSubtitle("(" + (position + 1) + "/" + attachments.size() + ")");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void viewMedia() {
        Attachment attachment = attachments.get(mViewPager.getCurrentItem());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(attachment.getFilePath()), attachment.getMime_type());
        startActivity(intent);
    }


//    private void shareMedia() {
//
//        Attachment attachment = attachments.get(mViewPager.getCurrentItem());
//        String imageFilePath = TextUtils.isEmpty(attachment.getUriCloudPath())
//                ? attachment.getUriLocalPath() : attachment.getUriCloudPath();
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType(StorageHelper.getMimeType(this, Uri.parse(imageFilePath)));
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageFilePath));
//        startActivity(intent);
//    }


}
