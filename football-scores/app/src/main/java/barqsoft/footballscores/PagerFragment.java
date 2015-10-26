package barqsoft.footballscores;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import barqsoft.footballscores.adapter.PagerAdapter;

public class PagerFragment extends Fragment {

    public static final int NUM_PAGES = 5;
    private ViewPager mPagerHandler;
    private TabLayout mTabLayout;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("TAB_POSITION", mTabLayout.getSelectedTabPosition());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);

        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);

        PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager(), getActivity());
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);

        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mPagerHandler);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPagerHandler.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        if (savedInstanceState != null)  {
            mPagerHandler.setCurrentItem(savedInstanceState.getInt("TAB_POSITION"));
        }

        return rootView;
    }
}