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
import com.okason.diary.data.JournalDao;
import com.okason.diary.data.RealmManager;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Journal;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class GalleryActivity extends AppCompatActivity {

    @BindView(R.id.gallery_root)
    LinearLayout galleryRootView;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

   // private List<Attachment> attachments;
    private List<Attachment> imageOnlyAttachments;
    private String title = "";

    private Journal parentJournal;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        realm = RealmManager.setUpRealm();
        imageOnlyAttachments = new ArrayList<>();

        //Pass in the Journal as a JSON to avoid having to query for the Journal from Firebase
        if (getIntent() != null && getIntent().hasExtra(Constants.NOTE_ID)) {
            getPassedInNote();
            initViews();
            initData();
        } else {
            finish();
        }
    }


    //Get the Journal object that was passed in
    private void getPassedInNote() {
        if (getIntent() != null && getIntent().hasExtra(Constants.NOTE_ID)){
            String noteId = getIntent().getStringExtra(Constants.NOTE_ID);
            parentJournal = new JournalDao(realm).getJournalById(noteId);
        }

    }


    private void initData(){
        String selectAttachmentPath = getIntent().getStringExtra(Constants.FILE_PATH);
        int selectedPosition = 0;

        if (parentJournal != null) {
            //Create an Arraylist to hold Ids of Attachments that are image or Video

            //Get the list of attachments in the Journal
            for (Attachment attachment : parentJournal.getAttachments()) {
                if (Constants.MIME_TYPE_IMAGE.equals(attachment.getMime_type())
                        || Constants.MIME_TYPE_SKETCH.equals(attachment.getMime_type())
                        || Constants.MIME_TYPE_VIDEO.equals(attachment.getMime_type())) {
                    imageOnlyAttachments.add(attachment);

                }
            }


            //Identify the attachment that was clicked in the list
            for (int i = 0; i < imageOnlyAttachments.size(); i++) {
                if (imageOnlyAttachments.get(i).getLocalFilePath().equals(selectAttachmentPath)) {
                    selectedPosition = i;
                    break;
                }
            }


            //Create a View Pager adapter to show the attachments
            AttachmentPagerAdapter pagerAdapter = new AttachmentPagerAdapter(getSupportFragmentManager(), imageOnlyAttachments);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.setAdapter(pagerAdapter);
            mViewPager.setCurrentItem(selectedPosition);

            getSupportActionBar().setTitle(parentJournal.getTitle());
            getSupportActionBar().setSubtitle("(" + (selectedPosition + 1) + "/" + imageOnlyAttachments.size() + ")");
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
                getSupportActionBar().setSubtitle("(" + (position + 1) + "/" + imageOnlyAttachments.size() + ")");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void viewMedia() {
        Attachment attachment = imageOnlyAttachments.get(mViewPager.getCurrentItem());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(attachment.getLocalFilePath()), attachment.getMime_type());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
