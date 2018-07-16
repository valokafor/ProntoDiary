package com.okason.diary.ui.folder;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.okason.diary.R;
import com.okason.diary.data.FolderDao;
import com.okason.diary.data.RealmManager;
import com.okason.diary.models.Folder;
import com.okason.diary.ui.notes.NotesFragment;
import com.okason.diary.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class FolderActivity extends AppCompatActivity {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private Realm realm;
    private FolderDao folderDao;
    private Folder folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm = RealmManager.setUpRealm();
        folderDao = new FolderDao(realm);



        if (getIntent() != null && getIntent().hasExtra(Constants.FOLDER_ID)){
            String folderId = getIntent().getStringExtra(Constants.FOLDER_ID);
            folder = folderDao.getFolderById(folderId);
            getSupportActionBar().setTitle(folder.getFolderName());
            setupViewPager(folderId);
        } else {
            finish();
        }
    }


    private void setupViewPager(String folderId) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), folderId);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final String folderId;
        public ViewPagerAdapter(FragmentManager fm, String folderId){
            super(fm);
            this.folderId = folderId;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Fragment fragment;
            switch (position){
                case 0:
                    fragment = NotesFragment.newInstance(false, "", folderId);
                    break;
                case 1:
                    fragment = NotesFragment.newInstance(false, "", folderId);
                    break;
                default:
                    fragment = NotesFragment.newInstance(false, "", folderId);
                    break;

            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position){
                case 0:
                    title = getString(R.string.label_journals) + " (" + folder.getJournals().size() + ")";
                    break;
                case 1:
                    title = getString(R.string.label_todo_list) + " (" + folder.getTasks().size() + ")";;
                    break;
            }
            return title;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
