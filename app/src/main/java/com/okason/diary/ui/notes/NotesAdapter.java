package com.okason.diary.ui.notes;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.okason.diary.R;
import com.okason.diary.core.GlideApp;
import com.okason.diary.core.listeners.NoteItemListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Journal;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.date.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Valentine on 2/6/2016.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<Journal> mJournals;
    private final Context mContext;
    private NoteItemListener mItemListener;
    private View noteView;
    private boolean isAudioPlaying = false;


    public NotesAdapter(List<Journal> journals, Context mContext){
        mJournals = journals;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        noteView = inflater.inflate(R.layout.custom_row_layout_note_list, parent, false);
        return new ViewHolder(noteView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Journal journal = mJournals.get(position);
        if (journal != null && !TextUtils.isEmpty(journal.getContent()) && !TextUtils.isEmpty(journal.getTitle())) {
            String firstLetter;
            TextDrawable drawable;
            int color;

            holder.title.setText(journal.getTitle());
            holder.contentSummary.setText(journal.getContent().substring(0, Math.min(100, journal.getContent().length())));
            holder.date.setText(TimeUtils.getReadableModifiedDateWithTime(journal.getDateModified()));

            firstLetter = journal.getTitle().substring(0, 1);
            color = Color.GRAY;
            drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color);
            holder.firstLetterIcon.setImageDrawable(drawable);

            //Check to see if this Journal has Attachments, if it does check if the attachment is image
            //If it is then show the thumbnail
            if (journal.getAttachments() != null && journal.getAttachments().size() > 0){
                holder.attachmentLayout.setVisibility(View.VISIBLE);

                Attachment lastAttachment = journal.getAttachments().get(journal.getAttachments().size() - 1);


                if (lastAttachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)){
                    if (isAudioPlaying) {
                        holder.noteAttachment.setImageResource(R.drawable.audio_pause);
                    } else {
                        holder.noteAttachment.setImageResource(R.drawable.play_button_75);
                    }

                } else if(lastAttachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)){
                    GlideApp.with(mContext)
                            .load(lastAttachment.getFilePath())
                            .placeholder(R.drawable.default_image)
                            .into(holder.noteAttachment);
                } else if (lastAttachment.getMime_type().equals(Constants.MIME_TYPE_VIDEO)){
                    holder.noteAttachment.setImageResource(R.drawable.video_icon);

                } else if (lastAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)){
                    //Attachment is file, show a PDF icon, if the attachment is a PDF else
                    //Show a document icon

                    String extension = FileHelper.getFileExtension(lastAttachment.getFilePath()).toLowerCase();
                    if (extension.contains("pdf")){
                        GlideApp.with(mContext)
                                .load(R.drawable.pdf_icon_2)
                                .placeholder(R.drawable.default_image)
                                .centerCrop()
                                .into(holder.noteAttachment);
                    } else {
                        GlideApp.with(mContext)
                                .load(R.drawable.ic_action_document)
                                .placeholder(R.drawable.default_image)
                                .centerCrop()
                                .into(holder.noteAttachment);
                    }


                } else {
                    GlideApp.with(mContext)
                            .load(lastAttachment.getFilePath())
                            .placeholder(R.drawable.default_image)
                            .centerCrop()
                            .into(holder.noteAttachment);
                }

            }else {
                holder.attachmentLayout.setVisibility(View.GONE);
            }


        }


    }



    @Override
    public int getItemCount() {
        return mJournals.size();
    }

    public Journal getItem(int position) {
        return mJournals.get(position);
    }

    public void replaceData(List<Journal> journals) {
        setList(journals);
        notifyDataSetChanged();
    }

    public void addNote(Journal journal){
        mJournals.add(journal);
        notifyItemInserted(mJournals.size() - 1);
    }

    private void setList(List<Journal> journals) {
        mJournals = journals;
    }

    public void setNoteItemListener(NoteItemListener listener){
        mItemListener = listener;
    }

    public boolean isAudioPlaying() {
        return isAudioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying, int position) {
        isAudioPlaying = audioPlaying;
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

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




        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Journal journal = getItem(position);
                    mItemListener.onNoteClick(journal);
                }
            });
            contentSummary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Journal journal = getItem(position);
                   mItemListener.onNoteClick(journal);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Journal journal = getItem(position);
                    mItemListener.onDeleteButtonClicked(journal);
                }
            });

            noteAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    Journal journal = getItem(postion);
                    mItemListener.onAttachmentClicked(journal, postion);
                }
            });
        }

    }

}
