package barqsoft.footballscores;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView leagueName;
    public TextView date;
    public TextView matchDay;
    public TextView homeName;
    public TextView awayName;
    public TextView score;
    public TextView status;
    public ImageView homeCrest;
    public ImageView awayCrest;
    public Button shareButton;

    public double match_id;

    public ViewHolder(View view) {
        super(view);
        leagueName = (TextView) view.findViewById(R.id.league_name);
        date = (TextView) view.findViewById(R.id.date_text);
        matchDay = (TextView) view.findViewById(R.id.matchday);
        homeName = (TextView) view.findViewById(R.id.home_name);
        awayName = (TextView) view.findViewById(R.id.away_name);
        score = (TextView) view.findViewById(R.id.score_text);
        status = (TextView) view.findViewById(R.id.status);
        homeCrest = (ImageView) view.findViewById(R.id.home_crest);
        awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        shareButton = (Button) view.findViewById(R.id.share_button);
    }
}