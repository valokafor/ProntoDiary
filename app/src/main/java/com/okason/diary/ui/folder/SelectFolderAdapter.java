package com.okason.diary.ui.folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.models.Folder;

import java.util.List;

/**
 * Created by Valentine on 2/17/2016.
 */
public class SelectFolderAdapter extends ArrayAdapter<Folder> {
    private List<Folder> mCategories;
    private Context mContext;




    public SelectFolderAdapter(Context context, List<Folder> categories){
        super(context, android.R.layout.simple_list_item_1, categories);
        mCategories = categories;
        mContext = context;

    }

    @Override
    public int getCount() {
        if (mCategories == null) {
            return 0;
        } else {
            return mCategories.size();
        }
    }


    @Override
    public Folder getItem(int position) {
        if (position < mCategories.size()) {
            return mCategories.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replaceData(List<Folder> folders){
        mCategories.clear();
        mCategories = folders;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Folder category = mCategories.get(position);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.folder_list_text, null);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        text1.setText(category.getFolderName());

        return view;
    }


}
