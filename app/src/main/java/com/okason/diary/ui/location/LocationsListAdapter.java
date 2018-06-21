package com.okason.diary.ui.location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.models.Location;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.ViewHolder> {
    private final List<Location> locationList;
    private final Context context;

    public LocationsListAdapter(List<Location> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_locations_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.locationName.setText(location.getName());
        holder.locationAddress.setText(location.getAddress());

        int journalCount = location.getJournals().size();
        String label = journalCount > 1 ? context.getString(R.string.label_journals) : context.getString(R.string.label_journal);
        holder.noteCountTextView.setText(journalCount + " " + label);

        int taskCount = location.getTasks().size();
        String taskLabel = taskCount > 1 ? "Tasks" : "ProntoTask";
        holder.taskCountTextView.setText(taskCount + " " + taskLabel);

    }

    @Override
    public int getItemCount() {
        if (locationList != null && locationList.size() > 0) {
            return locationList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_button_edit_location) ImageButton editLocation;
        @BindView(R.id.image_button_delete_location) ImageButton deleteLocation;
        @BindView(R.id.text_view_location_name) TextView locationName;
        @BindView(R.id.text_view_location_address) TextView locationAddress;
        @BindView(R.id.text_view_note_count) TextView noteCountTextView;
        @BindView(R.id.text_view_task_count) TextView taskCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }
}
