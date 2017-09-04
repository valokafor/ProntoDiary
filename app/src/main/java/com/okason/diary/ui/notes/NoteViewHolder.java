package com.okason.diary.ui.notes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.okason.diary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 9/3/17.
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {

    private NoteListAdapter noteListAdapter;
    @BindView(R.id.first_letter_icon)
    ImageView firstLetterIcon;
    @BindView(R.id.text_view_date)
    TextView date;
    @BindView(R.id.image_view_delete)
    ImageView delete;
    @BindView(R.id.text_view_note_title)
    TextView title;
    @BindView(R.id.text_view_note_summary)
    TextView contentSummary;
    @BindView(R.id.image_view_attachment)
    ImageView noteAttachment;
    @BindView(R.id.text_view_attachment_description)
    TextView attachmentDescription;
    @BindView(R.id.linear_layout_attachment)
    LinearLayout attachmentLayout;


    public void setDate(TextView date) {
        this.date = date;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public void setContentSummary(TextView contentSummary) {
        this.contentSummary = contentSummary;
    }

    public void setAttachmentDescription(TextView attachmentDescription) {
        this.attachmentDescription = attachmentDescription;
    }

    public NoteViewHolder(NoteListAdapter noteListAdapter, View itemView) {
        super(itemView);
        this.noteListAdapter = noteListAdapter;
        ButterKnife.bind(this, itemView);




//
//        title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = getAdapterPosition();
//                Note note = noteListAdapter.getItem(position);
//                noteListAdapter.mItemListener.onNoteClick(note);
//            }
//        });
//        contentSummary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = getAdapterPosition();
//                Note note = noteListAdapter.getItem(position);
//                noteListAdapter.mItemListener.onNoteClick(note);
//            }
//        });
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = getAdapterPosition();
//                Note note = noteListAdapter.getItem(position);
//                noteListAdapter.mItemListener.onDeleteButtonClicked(note);
//            }
//        });
//
//        noteAttachment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int postion = getAdapterPosition();
//                Note note = noteListAdapter.getItem(postion);
//                noteListAdapter.mItemListener.onAttachmentClicked(note, postion);
//            }
//        });
    }

}
