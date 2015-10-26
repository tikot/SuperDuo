package barqsoft.footballscores;

import android.content.res.Resources;

import barqsoft.footballscores.provider.Schema;

public class Utilities {

    public static String getLeague(Resources resources, int league_id) {
        switch (league_id) {
            case Schema.Leagues.BUNDESLIGA1 : return resources.getString(R.string.BL1);
            case Schema.Leagues.BUNDESLIGA2 : return resources.getString(R.string.BL2);
            case Schema.Leagues.LIGUE1 : return resources.getString(R.string.FL1);
            case Schema.Leagues.LIGUE2 : return resources.getString(R.string.FL2);
            case Schema.Leagues.PREMIER_LEAGUE : return resources.getString(R.string.PL);
            case Schema.Leagues.PRIMERA_DIVISION : return resources.getString(R.string.PD);
            case Schema.Leagues.SEGUNDA_DIVISION : return resources.getString(R.string.SD);
            case Schema.Leagues.SERIE_A : return resources.getString(R.string.SA);
            case Schema.Leagues.PRIMERA_LIGA : return resources.getString(R.string.PPL);
            case Schema.Leagues.BUNDESLIGA3 : return resources.getString(R.string.BL3);
            case Schema.Leagues.EREDIVISIE : return resources.getString(R.string.DED);
            case Schema.Leagues.CHAMPIONS_LEAGUE : return resources.getString(R.string.CL);
            default: return resources.getString(R.string.unknown_league);
        }
    }

    public static String getMatchDay(Resources resources, int matchDay, int leagueNum) {
        if(leagueNum == Schema.Leagues.CHAMPIONS_LEAGUE) {
            if (matchDay <= 6) {
                return resources.getString(R.string.group_stages);
            }
            else if(matchDay == 7 || matchDay == 8) {
                return resources.getString(R.string.knockout_round);
            }
            else if(matchDay == 9 || matchDay == 10) {
                return resources.getString(R.string.quarter_final);
            }
            else if(matchDay == 11 || matchDay == 12) {
                return resources.getString(R.string.semi_final);
            }
            else {
                return resources.getString(R.string.final_text);
            }
        }
        else {
            return resources.getString(R.string.match_day, matchDay);
        }
    }

    public static String getScores(Resources resources, int homeGoals,int awayGoals) {
        if (homeGoals <= 0 || awayGoals <= 0) {
            return " - ";
        }
        else {
            return  resources.getString(R.string.score, homeGoals, awayGoals);
        }
    }

    // TODO load the icons
    public static int getTeamCrestByTeamName (String teamName) {
        if (teamName==null) {
            return R.drawable.no_icon;
        }

        //This is the set of icons that are currently in the app. Feel free to find and add more as you go.
        switch (teamName) {
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }
}
