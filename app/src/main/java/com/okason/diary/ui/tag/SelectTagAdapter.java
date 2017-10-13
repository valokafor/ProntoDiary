package com.okason.diary.ui.tag;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.models.Tag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 6/18/17.
 */

public class SelectTagAdapter extends RecyclerView.Adapter<SelectTagAdapter.ViewHolder> {
    private List<Tag> mTags;
    private List<String> selectTags;
    private final Context mContext;
    private final OnTagSelectedListener mListener;
    private FirebaseFirestore db;


    public SelectTagAdapter(final List<String> tags, Context context, OnTagSelectedListener listener) {
        selectTags = tags;
        mContext = context;
        mListener = listener;
        db = FirebaseFirestore.getInstance();

        //Get all Tags
        db.collection("tags")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()){
                                Tag tag = document.toObject(Tag.class);
                                mTags.add(tag);
                            }
                        }
                    }
                });
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View todoListView = inflater.inflate(R.layout.custom_layout_select_tag_list, parent, false);
        return new ViewHolder((LinearLayout) todoListView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Tag selectedTag = mTags.get(position);

        if (!TextUtils.isEmpty(selectedTag.getTagName())) {
            holder.tagCheckbox.setText(selectedTag.getTagName());

            String tagPath = "filterTags." + selectedTag.getTagName();

            db.collection("notes")
                    .whereEqualTo(tagPath, true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int count = task.getResult().size();
                                int color = ContextCompat.getColor(mContext, R.color.primary_text);
                                TextDrawable drawable = TextDrawable.builder()
                                        .beginConfig()
                                        .bold()
                                        .endConfig()
                                        .buildRound(String.valueOf(count), color);
                                holder.tagCountImage.setImageDrawable(drawable);

                            }
                        }
                    });


        }

        //Selec Checkbox for Tags that have already been added to the
        //Journal
        for (String tag : selectTags) {
            if (selectedTag.getTagName().equals(tag)) {
                holder.tagCheckbox.setChecked(true);
                break;
            }
        }

        holder.tagCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Tag checkedTag = mTags.get(position);
                    if (!selectTags.contains(checkedTag.getTagName())){
                        selectTags.add(checkedTag.getTagName());
                        mListener.onTagListUpdated(selectTags);
                    }

                } else {
                    Tag unCheckedTag = mTags.get(position);
                    for (int i = 0; i < selectTags.size(); i++){
                        String tagName = selectTags.get(i);
                        if (tagName.equals(unCheckedTag.getTagName())){
                            selectTags.remove(i);
                            break;
                        }
                    }
                    mListener.onTagListUpdated(selectTags);
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mTags != null) {
            return mTags.size();
        } else {
            return 0;
        }
    }

    private void setTasks(List<Tag> tags) {
        mTags = tags;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout container;

        @BindView(R.id.check_box_tag)
        CheckBox tagCheckbox;
        @BindView(R.id.image_view_note_count)
        ImageView tagCountImage;


        public ViewHolder(LinearLayout container) {
            super(container);
            ButterKnife.bind(this, container);

        }


    }


}
