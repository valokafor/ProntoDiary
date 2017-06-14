package com.okason.diary.ui.notes;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.okason.diary.R;
import com.okason.diary.core.listeners.NoteItemListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.date.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Valentine on 2/6/2016.
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {
    private List<Note> mNotes;
    private final Context mContext;
    private NoteItemListener mItemListener;
    private View noteView;
    private boolean isAudioPlaying = false;


    public NoteListAdapter(List<Note> notes, Context mContext){
        mNotes = notes;
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
        final Note note = mNotes.get(position);
        if (note != null && !TextUtils.isEmpty(note.getContent()) && !TextUtils.isEmpty(note.getTitle())) {
            String firstLetter;
            ColorGenerator generator;
            TextDrawable drawable;
            int color;

            holder.title.setText(note.getTitle());
            holder.contentSummary.setText(note.getContent().substring(0, Math.min(100, note.getContent().length())));
            holder.date.setText(TimeUtils.getReadableModifiedDateWithTime(note.getDateModified()));

            if (note.getTasks() != null && note.getTasks().size() > 0){
                Glide.with(mContext).load(R.drawable.appointment_reminder).into(holder.firstLetterIcon);
            }else {
                firstLetter = note.getTitle().substring(0, 1);
                generator = ColorGenerator.MATERIAL;
                color = generator.getRandomColor();
                drawable = TextDrawable.builder()
                        .buildRound(firstLetter, color);
                holder.firstLetterIcon.setImageDrawable(drawable);
            }

            //Check to see if this Note has Attachments, if it does check if the attachment is image
            //If it is then show the thumbnail
            if (note.getAttachments() != null && note.getAttachments().size() > 0){
                holder.attachmentLayout.setVisibility(View.VISIBLE);

                Attachment lastAttachment = note.getAttachments().get(note.getAttachments().size() - 1);


                if (lastAttachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)){
                    if (isAudioPlaying) {
                        holder.noteAttachment.setImageResource(R.drawable.audio_pause);
                    } else {
                        holder.noteAttachment.setImageResource(R.drawable.play_button_75);
                    }

                } else if(lastAttachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)){
                    Glide.with(mContext)
                            .load(lastAttachment.getFilePath())
                            .placeholder(R.drawable.default_image)
                            .into(holder.noteAttachment);
                } else if (lastAttachment.getMime_type().equals(Constants.MIME_TYPE_VIDEO)){
                    holder.noteAttachment.setImageResource(R.drawable.video_icon);

                } else if (lastAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)){
                    //Attachment is file, show a PDF icon, if the attachment is a PDF else
                    //Show a document icon
                    String name = FileHelper.getNameFromUri(mContext, Uri.parse(lastAttachment.getUri()));
                    String extension = FileHelper.getFileExtension(name).toLowerCase();
                    if (extension.equals(".pdf")){
                        Glide.with(mContext)
                                .load(R.drawable.pdf_icon_2)
                                .placeholder(R.drawable.default_image)
                                .centerCrop()
                                .into(holder.noteAttachment);
                    } else {
                        Glide.with(mContext)
                                .load(R.drawable.ic_action_document)
                                .placeholder(R.drawable.default_image)
                                .centerCrop()
                                .into(holder.noteAttachment);
                    }


                } else {
                    Glide.with(mContext)
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
        return mNotes.size();
    }

    public Note getItem(int position) {
        return mNotes.get(position);
    }

    public void replaceData(List<Note> notes) {
        setList(notes);
        notifyDataSetChanged();
    }

    private void setList(List<Note> notes) {
        mNotes = notes;
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
                    Note note = getItem(position);
                    mItemListener.onNoteClick(note);
                }
            });
            contentSummary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Note note = getItem(position);
                    mItemListener.onNoteClick(note);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Note note = getItem(position);
                    mItemListener.onDeleteButtonClicked(note);
                }
            });

            noteAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    Note note = getItem(postion);
                    mItemListener.onAttachmentClicked(note, postion);
                }
            });
        }

    }

}
