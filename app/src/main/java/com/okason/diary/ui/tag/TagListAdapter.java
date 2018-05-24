package com.okason.diary.ui.tag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.models.realmentities.TagEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Valentine on 5/26/2016.
 */
public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder>{
    private final static String LOG_CAT = TagListAdapter.class.getSimpleName();
    private final static boolean DEBUG = true;

    private List<TagEntity> mTags;
    private final OnTagSelectedListener mListener;
    private final Context mContext;



    public TagListAdapter(Context mContext, List<TagEntity> mTags, OnTagSelectedListener mListener) {
        this.mTags = mTags;
        this.mContext = mContext;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_tag_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final TagEntity tag = mTags.get(position);
        String tagName = tag.getTagName();
        holder.tagName.setText(tagName);
        int numNote = tag.getNotes().size();
        String notes = numNote > 1 ? mContext.getString(R.string.label_journals) : mContext.getString(R.string.label_journal);
        holder.noteCountTextView.setText(numNote + " " + notes);





    }

    public void replaceData(List<TagEntity> tags){
        this.mTags.clear();
        mTags.addAll(tags);
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_button_edit_tag) ImageButton editTag;
        @BindView(R.id.image_button_delete_tag) ImageButton deleteTag;
        @BindView(R.id.text_view_tag_name)  TextView tagName;
        @BindView(R.id.text_view_note_count) TextView noteCountTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagEntity categoryToBeEdited = mTags.get(getLayoutPosition());
                    mListener.onEditTagButtonClicked(categoryToBeEdited);
                }
            });
            deleteTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagEntity tagToBeDeleted = mTags.get(getLayoutPosition());
                    mListener.onDeleteTagButtonClicked(tagToBeDeleted);
                }
            });

            tagName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTagClicked(mTags.get(getLayoutPosition()));
                }
            });

            noteCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTagClicked(mTags.get(getLayoutPosition()));
                }
            });

        }
    }
}
