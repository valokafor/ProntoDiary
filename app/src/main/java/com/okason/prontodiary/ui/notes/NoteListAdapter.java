package com.okason.prontodiary.ui.notes;

import android.content.Context;
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
import com.okason.prontodiary.R;
import com.okason.prontodiary.core.listeners.NoteItemListener;
import com.okason.prontodiary.models.Note;
import com.okason.prontodiary.utils.date.TimeUtils;

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
            holder.date.setText(TimeUtils.getReadableModifiedDate(note.getDateModified()));

//            switch (note.getNoteType()){
//                case Constants.NOTE_TYPE_AUDIO:
//                    holder.attachmentLayout.setVisibility(View.VISIBLE);
//                    Glide.with(mContext).load(R.drawable.play_button_96).into(holder.firstLetterIcon);
//                    break;
//                case Constants.NOTE_TYPE_REMINDER:
//                    Glide.with(mContext).load(R.drawable.appointment_reminder).into(holder.firstLetterIcon);
//                    break;
//                case Constants.NOTE_TYPE_IMAGE:
//                    firstLetter = note.getTitle().substring(0, 1);
//                    generator = ColorGenerator.MATERIAL;
//                    color = generator.getRandomColor();
//                    drawable = TextDrawable.builder()
//                            .buildRound(firstLetter, color);
//                    holder.firstLetterIcon.setImageDrawable(drawable);
//
//                    holder.attachmentLayout.setVisibility(View.VISIBLE);
//                    Glide.with(mContext)
//                            .load(note.getLocalImagePath())
//                            .placeholder(R.drawable.default_image)
//                            .centerCrop()
//                            .into(holder.noteAttachment);
//                    break;
//                case Constants.NOTE_TYPE_SKETCH:
//                    firstLetter = note.getTitle().substring(0, 1);
//                    generator = ColorGenerator.MATERIAL;
//                    color = generator.getRandomColor();
//                    drawable = TextDrawable.builder()
//                            .buildRound(firstLetter, color);
//                    holder.firstLetterIcon.setImageDrawable(drawable);
//
//                    holder.attachmentLayout.setVisibility(View.VISIBLE);
//                    Glide.with(mContext)
//                            .load(note.getLocalSketchImagePath())
//                            .placeholder(R.drawable.default_image)
//                            .centerCrop()
//                            .into(holder.noteAttachment);
//                    break;
//                case Constants.NOTE_TYPE_TEXT:
//                    firstLetter = note.getTitle().substring(0, 1);
//                    generator = ColorGenerator.MATERIAL;
//                    color = generator.getRandomColor();
//                    drawable = TextDrawable.builder()
//                            .buildRound(firstLetter, color);
//                    holder.firstLetterIcon.setImageDrawable(drawable);
//                    break;
//                default:
//                    firstLetter = note.getTitle().substring(0, 1);
//                    generator = ColorGenerator.MATERIAL;
//                    color = generator.getRandomColor();
//                    drawable = TextDrawable.builder()
//                            .buildRound(firstLetter, color);
//                    holder.firstLetterIcon.setImageDrawable(drawable);
//                    break;
//
//            }
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
        }

    }

}
