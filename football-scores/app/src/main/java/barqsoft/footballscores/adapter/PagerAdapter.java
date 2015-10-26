package barqsoft.footballscores.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Locale;

import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 5;
    private Context mContent;

    public PagerAdapter(FragmentManager fm, Context content) {
        super(fm);
        mContent = content;
    }

    @Override
    public Fragment getItem(int position) {
        return MainScreenFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return getDayName(mContent, System.currentTimeMillis()+((position-2)*86400000));
    }

    public String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.
        //TODO change deprecated Time
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay)
            return context.getString(R.string.today);
        else if ( julianDay == currentJulianDay +1 )
            return context.getString(R.string.tomorrow);
        else if ( julianDay == currentJulianDay -1)
            return context.getString(R.string.yesterday);
        else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            return dayFormat.format(dateInMillis);
        }
    }
}