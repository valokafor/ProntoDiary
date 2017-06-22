package com.okason.diary.ui.tag;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.models.Tag;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectTagDialogFragment extends DialogFragment {

    private View mRooView;
    private List<Tag> mTags;
    private SelectTagAdapter mTagAdapter;
    private OnTagSelectedListener mListener;
    private String noteId = "";


    public List<Tag> getTags() {
        return mTags;
    }

    public void setTags(List<Tag> tags) {
        mTags = tags;
    }

    public static SelectTagDialogFragment newInstance(){
        return new SelectTagDialogFragment();
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public OnTagSelectedListener getListener() {
        return mListener;
    }

    public void setListener(OnTagSelectedListener listener) {
        mListener = listener;
    }

    public SelectTagDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.fragment_select_tag, null);
        builder.setView(convertView);

        View titleView = (View)inflater.inflate(R.layout.dialog_title_layout, null);
        TextView addTagTitle = (TextView) titleView.findViewById(R.id.text_view_dialog_title);
        addTagTitle.setText(getString(R.string.select_tag));
        builder.setCustomTitle(titleView);


        RecyclerView tagRecyclerView = (RecyclerView) convertView.findViewById(R.id.tag_recyclerview);
        TextView emptyText = (TextView) convertView.findViewById(R.id.category_list_empty);


        final ImageButton addTagButton = (ImageButton)titleView.findViewById(R.id.image_button_add);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCategorySelectedListener.onAddCategoryButtonClicked();
            }
        });

        mTagAdapter = new SelectTagAdapter(mTags, getActivity(), mListener, noteId);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        tagRecyclerView.setLayoutManager(layoutManager);
        tagRecyclerView.setAdapter(mTagAdapter);

        builder.setPositiveButton(getString(R.string.label_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();

    }

}
