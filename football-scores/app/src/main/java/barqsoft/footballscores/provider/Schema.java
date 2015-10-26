package barqsoft.footballscores.provider;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * representation of a json from the API
 */
public class Schema {

    /**
     * endpoint alpha/fixtures
     */
    public class Fixtures {
        public int count;
        public List<Fixture> fixtures = new ArrayList<>();
    }

    public class Fixture {

        @SerializedName("_links")
        public Links links;
        public String date;
        public String status;
        @SerializedName("matchday")
        public int matchDay;
        public String homeTeamName;
        public String awayTeamName;
        public Result result;
    }

    public class Result {
        public int goalsHomeTeam;
        public int goalsAwayTeam;
    }

    public class Links {
        public Href self;
        public Href soccerseason;
        public Href homeTeam;
        public Href awayTeam;
    }

    public class Href {
        public String href;
    }

    public class Error {
        public String error;
    }

    /**
     * TODO improve to auto load a leagues
     * endpoint //api.football-data.org/alpha/soccerseasons
     *
     * This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
     * be updated. Feel free to use the codes
     */
    public interface Leagues {
        int BUNDESLIGA1 = 394; //BL1
        int BUNDESLIGA2 = 395; //BL2
        int LIGUE1 = 396; //FL1
        int LIGUE2 = 397; //FL2
        int PREMIER_LEAGUE = 398; //PL
        int PRIMERA_DIVISION = 399; //PD
        int SEGUNDA_DIVISION = 400; //SD
        int SERIE_A = 401; //SA
        int PRIMERA_LIGA = 402; //PPL
        int BUNDESLIGA3 = 403; //BL3
        int EREDIVISIE = 404; //DED
        int CHAMPIONS_LEAGUE = 405; //CL
    }
}