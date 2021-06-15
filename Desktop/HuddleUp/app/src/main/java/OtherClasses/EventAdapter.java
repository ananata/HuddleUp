package OtherClasses;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huddleup.R;

import java.util.List;

/**
 * Adapter class that wraps Event object so that it can be mounted on a Recyclerview object
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    Context context;
    List<Event> eventList;
    private GestureDetector mGestureDetector;
    public RecyclerItemClickListener.OnItemClickListener listener;

    public EventAdapter(Context context, List<Event> eventList){
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(com.example.huddleup.R.layout.home_screen_item, null);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {

        Event event = eventList.get(i);

        eventViewHolder.eventTitle.setText(event.getEventitle());
        eventViewHolder.eventCategory.setText(event.getEventcategory());
        eventViewHolder.eventPicture.setImageDrawable(context.getResources().getDrawable(event.getEventpicture()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle, eventCategory;
        ImageView eventPicture;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.ImageViewHomeScreenEventTitle);
            eventCategory = itemView.findViewById(com.example.huddleup.R.id.eventcategory);
            eventPicture = itemView.findViewById(R.id.ImageViewHomeScreenEventPicture);
        }

        public TextView getEventTitle() { return eventTitle;  }
        public TextView getEventCategory() { return eventCategory;  }
        public ImageView getEventPicture() { return eventPicture;  }
    }



}
