package OtherClasses;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huddleup.R;
import com.example.huddleup.auth.and.database.User;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>{
    Context context;
    List<User> userList;
    private GestureDetector mGestureDetector;
    public RecyclerItemClickListener.OnItemClickListener listener;


    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        // Declare screen utilities
        public TextView dateTextView, userNameTextView, roleTextView, userDescTextView;
        public ImageView userProfileImageView;
        String userName, userRole;
        Integer icon;

        // Create screen constructor
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);

            // Reuse team_select_screen_item.xml layout
            dateTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamDateCreated);
            userNameTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamTeamName);
            roleTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamSport);
            userDescTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamTeamDescription);
            userProfileImageView = (ImageView) itemView.findViewById(R.id.ImageViewTeamSelectionScreen);
        }
    }

    // Pass in the teams array into the constructor
    public PlayerAdapter(List<User> users) {
        userList = users;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.team_select_screen_item, parent, false);

        // set the Context here
        context = parent.getContext();

        // Return a new holder instance
        PlayerAdapter.PlayerViewHolder viewHolder = new PlayerAdapter.PlayerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        User player = userList.get(position);
        Log.d("PlayerAdapter player", (player == null) + "");
        holder.dateTextView.setText("");
        holder.userNameTextView.setText(player.getFullName());
        Log.d("PlayerAdapter name", (player.getFullName()));
        holder.roleTextView.setText(player.getRole());
        holder.userDescTextView.setText("");
        Log.d("PlayerAdapter icon", (player.getProfileIcon()) + "");
        holder.userProfileImageView.setImageDrawable(context.getResources().getDrawable(player.getProfileIcon()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Return current list from adapter
    public List<User> getArrayList(){
        return userList;
    }
}