package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.provider.DatabaseContract;

public class ScoresWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
       return new ScoresRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ScoresRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor cursor = null;

    final int MATCH_ID = 9;
    final int COL_LEAGUE = 6;
    final int COL_DATE = 1;
    final int COL_MATCH_DAY = 10;
    final int COL_MATCH_TIME = 2;
    final int COL_STATUS = 4;
    final int COL_HOME = 3;
    final int COL_HOME_GOALS = 7;
    final int COL_AWAY = 5;
    final int COL_AWAY_GOALS = 8;

    public ScoresRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }

        final long token = Binder.clearCallingIdentity();

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);

        String today = simpleDate.format(calendar.getTime());
        cursor = mContext.getContentResolver().query(
                DatabaseContract.scores_table.buildScoreWithDate(),
                null,
                null,
                new String[]{today}, null);

        Binder.restoreCallingIdentity(token);
    }

    @Override
    public void onDestroy() {
        if(cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (i == AdapterView.INVALID_POSITION ||
                cursor == null || !cursor.moveToPosition(i)) {
            return null;
        }

        Resources resources = mContext.getResources();

        final RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.scores_widget_list_item);

        views.setTextViewText(R.id.league_name,
                Utilities.getLeague(resources, cursor.getInt(COL_LEAGUE)) );

        views.setTextViewText(R.id.date_text, cursor.getString(COL_MATCH_TIME));

        views.setTextViewText(R.id.matchday,
                Utilities.getMatchDay(resources,
                        cursor.getInt(COL_MATCH_DAY),
                        cursor.getInt(COL_LEAGUE)) );

        views.setTextViewText(R.id.home_name, cursor.getString(COL_HOME));
        views.setTextViewText(R.id.away_name, cursor.getString(COL_AWAY));

        views.setTextViewText(R.id.score_text,
                Utilities.getScores(resources,
                        cursor.getInt(COL_HOME_GOALS),
                        cursor.getInt(COL_AWAY_GOALS)));

        views.setTextViewText(R.id.status, cursor.getString(COL_STATUS));

        views.setImageViewResource(R.id.home_crest,
                Utilities.getTeamCrestByTeamName(cursor.getString(COL_HOME)) );
        views.setImageViewResource(R.id.away_crest,
                Utilities.getTeamCrestByTeamName(cursor.getString(COL_AWAY)) );

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.scores_widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        if (cursor.moveToPosition(i))
            return cursor.getLong(MATCH_ID);
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}