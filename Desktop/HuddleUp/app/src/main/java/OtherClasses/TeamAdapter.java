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
import com.example.huddleup.auth.and.database.Team;

import java.util.List;

public class TeamAdapter extends  RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    Context context;
    List<Team> teamList;
    private GestureDetector mGestureDetector;
    public RecyclerItemClickListener.OnItemClickListener listener;

    public class TeamViewHolder extends RecyclerView.ViewHolder {
        // Declare screen utilities
        public TextView dateCreatedTextView, teamNameTextView, teamSportTextView, teamDescTextView;
        public ImageView sportSelected;
        String dateCreated, teamName, teamSport, teamDesc;
        Integer icon;

        // Create screen constructor
        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);

            dateCreatedTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamDateCreated);
            teamNameTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamTeamName);
            teamSportTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamSport);
            teamDescTextView = (TextView) itemView.findViewById(R.id.TextViewSelectTeamTeamDescription);
            sportSelected = (ImageView) itemView.findViewById(R.id.ImageViewTeamSelectionScreen);
        }
    }

    // Pass in the teams array into the constructor
    public TeamAdapter(List<Team> teams) {
        teamList = teams;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.team_select_screen_item, parent, false);

        // set the Context here
        context = parent.getContext();

        // Return a new holder instance
        TeamViewHolder viewHolder = new TeamViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teamList.get(position);
        String dateCreatedText = "Created " + team.getCreateDate();
        holder.dateCreatedTextView.setText(dateCreatedText);
        holder.teamNameTextView.setText(team.getTeamName());
        holder.teamSportTextView.setText(team.getSport());
        holder.teamDescTextView.setText(team.getTeamDescription());
        holder.sportSelected.setImageDrawable(context.getResources().getDrawable(team.getImageIcon()));
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    // Return current list from adapter
    public List<Team> getTeamList(){
        return teamList;
    }


}
