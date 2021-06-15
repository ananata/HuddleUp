package OtherClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.example.huddleup.R;
import com.example.huddleup.auth.and.database.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    Context context;
    List<Message> messageList;
    private GestureDetector mGestureDetector;
    public RecyclerItemClickListener.OnItemClickListener listener;
    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    public MessageAdapter(Context context, List<Message> messages){
        this.context = context;
        this.messageList = messages;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.message_item_open, null);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        /**
         * Expansion code adapted from:
         * https://stackoverflow.com/questions/27203817/recyclerview-expand-collapse-items
         */
        final boolean isExpanded = position==mExpandedPosition;
        holder.details.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);

        // Get values
        Message message = messageList.get(position);
        holder.messageBody.setText(message.getMessage());
        String dateTime = "Sent " + message.getDateSent();
        holder.messageDateCreated.setText(dateTime);
        holder.messageSubject.setText(message.getSubject());

        if (isExpanded)
            previousExpandedPosition = position;

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


    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageSubject, messageBody, messageDateCreated;
        ImageButton expand;
        ConstraintLayout details;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get variables
            messageSubject = itemView.findViewById(R.id.TextViewOpenMessageItemSubject);
            messageBody = itemView.findViewById(R.id.TextViewOpenMessageItemMessageBody);
            messageDateCreated = itemView.findViewById(R.id.TextViewOpenMessageDateTime);
            expand = itemView.findViewById(R.id.ImageButtonOpenMessageItem);
            details = itemView.findViewById(R.id.ConstraintLayoutMessageScreenHiddenItems);

        }

        // Getters and setters
        public TextView getMessageSubject() { return messageSubject;  }
        public TextView getMessageBody() { return messageBody;  }
        public TextView getMessageDateCreated() { return messageDateCreated; }
        public ConstraintLayout getHiddenDetails() { return details; }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


}
