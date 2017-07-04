package com.okason.diary.ui.tag;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 6/18/17.
 */

public class SelectTagAdapter extends RecyclerView.Adapter<SelectTagAdapter.ViewHolder>  {
    private List<Tag> mTags;
    private final Context mContext;
    private final OnTagSelectedListener mListener;

    //The id of the calling Note
    private final String noteId;


    public SelectTagAdapter(List<Tag> tags, Context context, OnTagSelectedListener listener, String noteId) {
        mTags = tags;
        mContext = context;
        mListener = listener;
        this.noteId = noteId;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View todoListView = inflater.inflate(R.layout.custom_layout_select_tag_list, parent, false);
        return new ViewHolder((LinearLayout)todoListView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tag selectedTag = mTags.get(position);

        if (!TextUtils.isEmpty(selectedTag.getTagName())){
            holder.tagCheckbox.setText(selectedTag.getTagName());

            int count = selectedTag.getNotes().size();
            int color = ContextCompat.getColor(mContext, R.color.primary_text);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .endConfig()
                    .buildRound(String.valueOf(count), color);
            holder.tagCountImage.setImageDrawable(drawable);
        }

        if (selectedTag.getNotes().size() > 0){
            for (Note note : selectedTag.getNotes()){
                if (note.getId().equals(noteId)){
                    holder.tagCheckbox.setChecked(true);
                    break;
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        if (mTags != null){
            return mTags.size();
        }else {
            return 0;
        }
    }

    private void setTasks(List<Tag> tags) {
        mTags = tags;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout container;

        @BindView(R.id.check_box_tag)
        CheckBox tagCheckbox;
        @BindView(R.id.image_view_note_count)
        ImageView tagCountImage;



        public ViewHolder(LinearLayout container) {
            super(container);
            ButterKnife.bind(this, container);
            tagCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        Tag checkedTag = mTags.get(getLayoutPosition());
                        mListener.onTagChecked(checkedTag);
                    }else {
                        Tag unCheckedTag = mTags.get(getLayoutPosition());
                        mListener.onTagUnChecked(unCheckedTag);
                    }
                }
            });
        }


    }


}
