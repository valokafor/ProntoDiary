package com.okason.diary.ui.attachment;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnAttachmentClickedListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.DateHelper;
import com.okason.diary.utils.date.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Valentine on 5/14/2017.
 */

public class AttachmentListAdapter extends RecyclerView.Adapter<AttachmentListAdapter.ViewHolder> {
    private final List<Attachment> attachmentList;
    private final Context context;
    private final int screenWidth;
    private OnAttachmentClickedListener listener;

    public AttachmentListAdapter(List<Attachment> attachmentList, Context context) {
        this.attachmentList = attachmentList;
        this.context = context;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public void setListener(OnAttachmentClickedListener listener) {
        this.listener = listener;
    }

    @Override
    public AttachmentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView  = inflater.inflate(R.layout.custom_row_attachment_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AttachmentListAdapter.ViewHolder holder, int position) {
        Attachment selectedAttachment = attachmentList.get(position);
        if (!TextUtils.isEmpty(selectedAttachment.getName())){
            holder.text.setVisibility(View.VISIBLE);
        }

        // Draw name in case the type is an audio recording
        if (selectedAttachment.getMime_type() != null && selectedAttachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)) {
            String text;

            if (selectedAttachment.getLength() > 0) {
                // Recording duration
                text = DateHelper.formatShortTime(context, selectedAttachment.getLength());
            } else {
                // Recording date otherwise
                text = TimeUtils.getDueDate(selectedAttachment.getDateCreated());
            }

            if (text == null) {
                text = context.getString(R.string.attachment);
            }
            holder.text.setText(text);
            holder.text.setVisibility(View.VISIBLE);
        } else {
            holder.text.setVisibility(View.GONE);
        }

        // Draw name in case the type is an audio recording (or file in the future)
        if (selectedAttachment.getMime_type() != null && selectedAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)) {
            Glide.with(context.getApplicationContext())
                    .load(selectedAttachment.getUri())
                    .centerCrop()
                    .crossFade()
                    .placeholder(R.drawable.image_broken)
                    .into(holder.image);
        } else {
            Glide.with(context.getApplicationContext())
                    .load(selectedAttachment.getFilePath())
                    .centerCrop()
                    .crossFade()
                    .placeholder(R.drawable.image_broken)
                    .into(holder.image);
        }



    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    public void addAttachment(Attachment attachment){
        attachmentList.add(attachment);
        notifyItemInserted(attachmentList.size() - 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.attachment_item_text)
        TextView text;

        @BindView(R.id.attachment_item_picture)
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAttachmentClicked(attachmentList.get(getLayoutPosition()));
                }
            });
        }
    }


}
