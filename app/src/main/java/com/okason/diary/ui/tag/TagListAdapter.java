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
import com.okason.diary.models.ProntoTag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Valentine on 5/26/2016.
 */
public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder>{
    private final static String LOG_CAT = TagListAdapter.class.getSimpleName();
    private final static boolean DEBUG = true;

    private List<ProntoTag> mProntoTags;
    private final OnTagSelectedListener mListener;
    private final Context mContext;



    public TagListAdapter(Context mContext, List<ProntoTag> mProntoTags, OnTagSelectedListener mListener) {
        this.mProntoTags = mProntoTags;
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

        final ProntoTag prontoTag = mProntoTags.get(position);
        String tagName = prontoTag.getTagName();
        holder.tagName.setText(tagName);
        int numNote = prontoTag.getJournals().size();
        String notes = numNote > 1 ? mContext.getString(R.string.label_journals) : mContext.getString(R.string.label_journal);
        holder.noteCountTextView.setText(numNote + " " + notes);





    }

    public void replaceData(List<ProntoTag> prontoTags){
        this.mProntoTags.clear();
        mProntoTags.addAll(prontoTags);
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return mProntoTags.size();
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
                    ProntoTag categoryToBeEdited = mProntoTags.get(getLayoutPosition());
                    mListener.onEditTagButtonClicked(categoryToBeEdited);
                }
            });
            deleteTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProntoTag prontoTagToBeDeleted = mProntoTags.get(getLayoutPosition());
                    mListener.onDeleteTagButtonClicked(prontoTagToBeDeleted);
                }
            });

            tagName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTagClicked(mProntoTags.get(getLayoutPosition()));
                }
            });

            noteCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTagClicked(mProntoTags.get(getLayoutPosition()));
                }
            });

        }
    }
}
