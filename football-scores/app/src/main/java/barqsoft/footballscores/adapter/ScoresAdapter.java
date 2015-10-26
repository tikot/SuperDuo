package barqsoft.footballscores.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.ViewHolder;

public class ScoresAdapter extends RecyclerView.Adapter<ViewHolder> {

    public Cursor mCursor;

    private Context context;
    int COL_ID = 0;
    int COL_LEAGUE = 6;
    int COL_DATE = 1;
    int COL_MATCH_DAY = 10;
    int COL_MATCH_TIME = 2;
    int COL_STATUS = 4;
    int COL_HOME = 3;
    int COL_HOME_GOALS = 7;
    int COL_AWAY = 5;
    int COL_AWAY_GOALS = 8;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View mItem = LayoutInflater.from(context)
                .inflate(R.layout.scores_list_item, parent, false);

        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);

        return mHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Resources resources = context.getResources();
        mCursor.moveToPosition(position);

        holder.leagueName.setText(
                Utilities.getLeague(resources, mCursor.getInt(COL_LEAGUE))
        );
        holder.date.setText(mCursor.getString(COL_MATCH_TIME));
        holder.matchDay.setText(
                Utilities.getMatchDay(resources,
                        mCursor.getInt(COL_MATCH_DAY),
                        mCursor.getInt(COL_LEAGUE))
        );

        final String homeName = mCursor.getString(COL_HOME);
        final String awayName = mCursor.getString(COL_AWAY);
        final int homeScore = mCursor.getInt(COL_HOME_GOALS);
        final int awayScore = mCursor.getInt(COL_AWAY_GOALS);

        holder.homeName.setText(homeName);
        holder.awayName.setText(awayName);

        holder.score.setText(Utilities.getScores(resources,
                homeScore, awayScore));

        holder.status.setText(mCursor.getString(COL_STATUS));

        holder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                mCursor.getString(COL_HOME)));
        holder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                mCursor.getString(COL_AWAY)));

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = resources.getString(
                        R.string.share_score, homeName,  homeScore, awayName, awayScore );

                context.startActivity(createShareForecastIntent(shareText));
            }
        });

        holder.match_id = mCursor.getDouble(COL_ID);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Intent createShareForecastIntent(String shareText) {
        String FOOTBALL_SCORES_HASHTAG = " #Football_Scores";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText + FOOTBALL_SCORES_HASHTAG);

        return shareIntent;
    }
}
