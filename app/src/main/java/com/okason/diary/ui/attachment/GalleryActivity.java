package com.okason.diary.ui.attachment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnViewTouchedListener;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Attachment;
import com.okason.diary.ui.notes.NoteListContract;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.StorageHelper;

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

    @BindView(R.id.gallery_root) InterceptorFrameLayout galleryRootView;
    @BindView(R.id.fullscreen_content)
    ViewPager mViewPager;

    private List<Attachment> images;
    private NoteListContract.Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().hasExtra(Constants.SERIALIZED_ATTACHMENT_ID)){
            initViews();
            initData();
        }else {
            finish();
        }



    }

    private void initData() {
        String serializedAttachmentsIds = getIntent().getStringExtra(Constants.SERIALIZED_ATTACHMENT_ID);
        String selectAttachmentId = getIntent().getStringExtra(Constants.SELECTED_ID);
        int selectedPosition = 0;

        Gson gson = new Gson();
        List<String> listOfIds = gson.fromJson(serializedAttachmentsIds, new TypeToken<List<String>>(){}.getType());

        if (listOfIds != null && listOfIds.size() > 0){
            images = new ArrayList<>();
            repository = new NoteRealmRepository();
            for (String id: listOfIds){
                //Get Attachment from Database
                Attachment attachment = repository.getAttachmentbyId(id);
                if (attachment != null){
                    images.add(attachment);
                }
                if (id.equals(selectAttachmentId)){
                    selectedPosition = listOfIds.indexOf(selectAttachmentId);
                }
            }
        }

        AttachmentPagerAdapter pagerAdapter = new AttachmentPagerAdapter(getSupportFragmentManager(), images);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(selectedPosition);

        getSupportActionBar().setTitle("sample Title");
        getSupportActionBar().setSubtitle("(" + (selectedPosition + 1) + "/" + images.size() + ")");

        // If selected attachment is a video it will be immediately played
//        if (images.get(clickedImage).getMime_type().equals(Constants.MIME_TYPE_VIDEO)) {
//            viewMedia();
//        }



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
                shareMedia();
                break;
            case R.id.menu_gallery:
                viewMedia();
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

        galleryRootView.setOnViewTouchedListener(screenTouches);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSubtitle("(" + (position + 1) + "/" + images.size() + ")");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void viewMedia() {
        Attachment attachment = images.get(mViewPager.getCurrentItem());
        String imageFilePath = TextUtils.isEmpty(attachment.getUriCloudPath())
                ? attachment.getUriLocalPath(): attachment.getUriCloudPath();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imageFilePath),
                StorageHelper.getMimeType(this, Uri.parse(imageFilePath)));
        startActivity(intent);
    }


    private void shareMedia() {

        Attachment attachment = images.get(mViewPager.getCurrentItem());
        String imageFilePath = TextUtils.isEmpty(attachment.getUriCloudPath())
                ? attachment.getUriLocalPath(): attachment.getUriCloudPath();

                Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(StorageHelper.getMimeType(this, Uri.parse(imageFilePath)));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageFilePath));
        startActivity(intent);
    }



    OnViewTouchedListener screenTouches = new OnViewTouchedListener() {
        private final int MOVING_THRESHOLD = 30;
        float x;
        float y;
        private boolean status_pressed = false;


        @Override
        public void onViewTouchOccurred(MotionEvent ev) {
            if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                x = ev.getX();
                y = ev.getY();
                status_pressed = true;
            }
            if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                float dx = Math.abs(x - ev.getX());
                float dy = Math.abs(y - ev.getY());
                double dxy = Math.sqrt(dx * dx + dy * dy);
                Log.d(Constants.TAG, "Moved of " + dxy);
                if (dxy >= MOVING_THRESHOLD) {
                    status_pressed = false;
                }
            }
            if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                if (status_pressed) {
                    click();
                    status_pressed = false;
                }
            }
        }


        private void click() {
            Attachment attachment = images.get(mViewPager.getCurrentItem());
            if (attachment.getMime_type().equals(Constants.MIME_TYPE_VIDEO)) {
                viewMedia();
            }
        }
    };

}
