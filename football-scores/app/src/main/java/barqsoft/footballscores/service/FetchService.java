package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.provider.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.provider.Schema;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

/**
 * it was better to rewrite this class instead of fixing it (simplify it)
 */
public class FetchService extends IntentService {

    public static final String LOG_TAG = "FetchService";
    public static final String ACTION_DATA_UPDATED = "barqsoft.footballscores.ACTION_DATA_UPDATED";

    private static final String BASE_API_URI = "http://api.football-data.org/";
    private static String apiKey;
    // Time Frame settings
    private String[] apiTime = new String[] {"n3", "p3"};

    private FootballData footballData;

    public FetchService() {
        super("FetchService");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        footballData = retrofit.create(FootballData.class);
    }

    public interface FootballData {

        @GET("alpha/fixtures")
        Call<Schema.Fixtures> fixtures(
                @Header("X-Auth-Token") String apiKey,
                @Query("timeFrame") String timeFrame
        );

        //@GET("alpha/soccerseasons")
        //@GET("alpha/teams")
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // TODO change api into build config
        apiKey = getString(R.string.api_key);

        for (String timeFrame : apiTime) {
            getFixutes(timeFrame);
        }
    }

    private void getFixutes(String timeFrame) {
        Call<Schema.Fixtures> call = footballData.fixtures(apiKey, timeFrame);

        //call.execute()
        call.enqueue(new Callback<Schema.Fixtures>() {
            @Override
            public void onResponse(Response<Schema.Fixtures> response, Retrofit retrofit) {
                processData(response.body().fixtures);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(FetchService.class.getName(), t.getMessage());
            }
        });
    }

    private void processData(List<Schema.Fixture> fixtures) {

        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";

        int league;
        int matchId = 0;
        String mTime = null;
        String mDate = null;
        String status;
        int matchDay;
        String homeTeamName;
        int goalsHomeTeam;
        String awayTeamName;
        int goalsAwayTeam;

        //ContentValues to be inserted
        Vector<ContentValues> values = new Vector <ContentValues> (fixtures.size());

        int index = 0;
        for (Schema.Fixture fixture : fixtures) {
            league = getId(fixture.links.soccerseason, SEASON_LINK);

            //This if statement controls which leagues we're interested in the data from.
            //add leagues here in order to have them be added to the DB.
            //If you are finding no data in the app, check that this contains all the leagues.
            //If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
            if ((league == Schema.Leagues.BUNDESLIGA1) ||
                    (league == Schema.Leagues.BUNDESLIGA2) ||
                    (league == Schema.Leagues.LIGUE1) ||
                    (league == Schema.Leagues.LIGUE2) ||
                    (league == Schema.Leagues.PREMIER_LEAGUE) ||
                    (league == Schema.Leagues.PRIMERA_DIVISION) ||
                    (league == Schema.Leagues.SEGUNDA_DIVISION) ||
                    (league == Schema.Leagues.SERIE_A) ||
                    (league == Schema.Leagues.PRIMERA_LIGA) ||
                    (league == Schema.Leagues.BUNDESLIGA3) ||
                    (league == Schema.Leagues.EREDIVISIE) ||
                    (league == Schema.Leagues.CHAMPIONS_LEAGUE)) {

                matchId = getId(fixture.links.self, MATCH_LINK);
                mDate = fixture.date;
                mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                mDate = mDate.substring(0, mDate.indexOf("T"));

                SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.US);
                match_date.setTimeZone(TimeZone.getTimeZone("UTC"));

                try {
                    Date parsedDate = match_date.parse(mDate + mTime);
                    SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm", Locale.US);
                    new_date.setTimeZone(TimeZone.getDefault());
                    mDate = new_date.format(parsedDate);
                    mTime = mDate.substring(mDate.indexOf(":") + 1);
                    mDate = mDate.substring(0, mDate.indexOf(":"));

                    // will be good for esting
                    //if(!isReal){
                    //    //This if statement changes the dummy data's date to match our current date range.
                    //    Date fragmentdate = new Date(System.currentTimeMillis()+((index-2)*86400000));
                    //    SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                    //    mDate = mformat.format(fragmentdate);
                    //    index++;
                    //}
                }
                catch (Exception e) {
                    Log.d(LOG_TAG, "error here!");
                    Log.e(LOG_TAG,e.getMessage());
                }
            }

            status = fixture.status;
            matchDay = fixture.matchDay;

            homeTeamName = fixture.homeTeamName;
            goalsHomeTeam = fixture.result.goalsHomeTeam;

            awayTeamName = fixture.awayTeamName;
            goalsAwayTeam = fixture.result.goalsHomeTeam;

            ContentValues match_values = new ContentValues();
            match_values.put(DatabaseContract.scores_table.MATCH_ID, matchId);
            match_values.put(DatabaseContract.scores_table.DATE_COL, mDate);
            match_values.put(DatabaseContract.scores_table.TIME_COL, mTime);
            match_values.put(DatabaseContract.scores_table.STATUS, status);

            match_values.put(DatabaseContract.scores_table.HOME_COL, homeTeamName);
            match_values.put(DatabaseContract.scores_table.AWAY_COL, awayTeamName);

            match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL, goalsHomeTeam);
            match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL, goalsAwayTeam);

            match_values.put(DatabaseContract.scores_table.LEAGUE_COL, league);
            match_values.put(DatabaseContract.scores_table.MATCH_DAY, matchDay);

            // save all data
            values.add(match_values);
        }

        ContentValues[] insert_data = new ContentValues[values.size()];
        values.toArray(insert_data);

        //int inserted_data = 0;
        int inserted_data = getContentResolver().bulkInsert(DatabaseContract.BASE_CONTENT_URI,
                insert_data);

        Log.v(LOG_TAG, "Successfully Inserted : " + String.valueOf(inserted_data));
        getApplicationContext()
                .sendBroadcast(new Intent(ACTION_DATA_UPDATED).setPackage(getPackageName()));
    }

    private int getId(Schema.Href soccerSeason, String seasonLink) {
        String id = soccerSeason.href.replace(seasonLink, "");
        return (Integer.parseInt(id));
    }
}

