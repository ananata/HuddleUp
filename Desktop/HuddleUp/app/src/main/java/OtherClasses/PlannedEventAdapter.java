package OtherClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huddleup.PlannedEvent;
import com.example.huddleup.R;
import com.example.huddleup.auth.and.database.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlannedEventAdapter extends RecyclerView.Adapter<PlannedEventAdapter.PlannedEventViewHolder> {
    Context context;
    List<PlannedEvent> eventList;
    private GestureDetector mGestureDetector;
    public RecyclerItemClickListener.OnItemClickListener listener;
    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    public PlannedEventAdapter(Context context, List<PlannedEvent> events) {
        this.context = context;
        this.eventList = events;
    }

    @NonNull
    @Override
    public PlannedEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.message_item_open, null);
        return new PlannedEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannedEventViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.details.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);

        // Get values
        PlannedEvent event = eventList.get(position);
        holder.eventTitle.setText(event.getName());

        // Format time
        String time = new SimpleDateFormat("h:mm a").format(event.getLongTime());
        Log.d("PlannedEventAdapter", "Formatted time: " + time);
        String eventInfo = "Event begins " + event.getStringDate() + " at " + time;
        eventInfo = eventInfo + "\nCheck messages for more details";
        holder.eventDateSent.setText(""); // Leave blank
        holder.eventDetails.setText(eventInfo);

        if (isExpanded)
            previousExpandedPosition = position;

        // Allow window to expand
        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                Drawable res = v.getResources().getDrawable(R.drawable.ic_arrow_up);
                holder.expand.setImageDrawable(res);
                holder.details.setVisibility(View.VISIBLE);
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class PlannedEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventDetails, eventDateSent;
        ImageButton expand;
        ConstraintLayout details;

        public PlannedEventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get variables
            eventTitle = itemView.findViewById(R.id.TextViewOpenMessageItemSubject);
            eventDetails = itemView.findViewById(R.id.TextViewOpenMessageItemMessageBody);
            eventDateSent = itemView.findViewById(R.id.TextViewOpenMessageDateTime);
            expand = itemView.findViewById(R.id.ImageButtonOpenMessageItem);
            details = itemView.findViewById(R.id.ConstraintLayoutMessageScreenHiddenItems);
        }

        // Getters and setters
        public TextView getEventTitle() { return eventTitle;  }
        public TextView getEventDetails() { return eventDetails;  }
        public TextView getEventDateSent() { return eventDateSent; }
        public ConstraintLayout getHiddenDetails() { return details; }
    }
}
