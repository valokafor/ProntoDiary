package com.okason.diary.ui.folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.models.viewModel.FolderViewModel;

import java.util.List;

/**
 * Created by Valentine on 2/17/2016.
 */
public class FolderListViewAdapter extends ArrayAdapter<FolderViewModel> {
    private List<FolderViewModel> mCategories;
    private Context mContext;




    public FolderListViewAdapter(Context context, List<FolderViewModel> categories){
        super(context, android.R.layout.simple_list_item_1, categories);
        mCategories = categories;
        mContext = context;

    }

    @Override
    public int getCount() {
        return mCategories.size();
    }


    @Override
    public FolderViewModel getItem(int position) {
        if (position < mCategories.size()) {
            return mCategories.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FolderViewModel category = mCategories.get(position);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.folder_list_text, null);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        text1.setText(category.getCategoryName());

        return view;
    }


}
