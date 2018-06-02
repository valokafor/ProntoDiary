package com.okason.diary.ui.tag;

import android.content.Context;
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
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 6/18/17.
 */

public class SelectTagAdapter extends RecyclerView.Adapter<SelectTagAdapter.ViewHolder> {
    private List<ProntoTag> mProntoTags;
    private final Context mContext;
    private final OnTagSelectedListener mListener;

    //The id of the calling Journal
    private final String noteId;


    public SelectTagAdapter(List<ProntoTag> prontoTags, Context context, OnTagSelectedListener listener, String noteId) {
        mProntoTags = prontoTags;
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
        final ProntoTag selectedProntoTag = mProntoTags.get(position);

        if (!TextUtils.isEmpty(selectedProntoTag.getTagName())){
            holder.tagCheckbox.setText(selectedProntoTag.getTagName());

            int count = selectedProntoTag.getJournals().size();
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getRandomColor();
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(String.valueOf(count), color);
            holder.tagCountImage.setImageDrawable(drawable);
        }

        if (selectedProntoTag.getJournals().size() > 0){
            for (Journal journal : selectedProntoTag.getJournals()){
                if (journal.getId().equals(noteId)){
                    holder.tagCheckbox.setChecked(true);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        if (mProntoTags != null){
            return mProntoTags.size();
        }else {
            return 0;
        }
    }

    private void setTasks(List<ProntoTag> prontoTags) {
        mProntoTags = prontoTags;
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
                        ProntoTag checkedProntoTag = mProntoTags.get(getLayoutPosition());
                        mListener.onTagChecked(checkedProntoTag);
                    }else {
                        ProntoTag unCheckedProntoTag = mProntoTags.get(getLayoutPosition());
                        mListener.onTagUnChecked(unCheckedProntoTag);
                    }
                }
            });
        }


    }



}
