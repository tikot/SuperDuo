package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import barqsoft.footballscores.adapter.ScoresAdapter;
import barqsoft.footballscores.provider.DatabaseContract;
import barqsoft.footballscores.service.FetchService;

/**
 * A placeholder fragment containing a simple view.
 *
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {

    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String fragmentDate;

    private View emptyScore;
    private int last_selected_item = -1;
    private int position;


    public static final String PAGE_POSITION = "POSITION";
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    public static MainScreenFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(PAGE_POSITION, position);
        args.putString("PAGE_DATE", getFragmentDate(position));

        MainScreenFragment fragment = new MainScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(PAGE_POSITION);
        fragmentDate = getArguments().getString("PAGE_DATE");
        update_scores();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        //update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        emptyScore = rootView.findViewById(R.id.scores_empty);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.scores_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a grid layout
        mLayoutManager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.columns), StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ScoresAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        return rootView;
    }

    public void update_scores() {
        Intent service_start = new Intent(getActivity(), FetchService.class);
        getActivity().startService(service_start);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{fragmentDate}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final int size = data.getCount();
        mAdapter.swapCursor(data);

        if (size == 0)
            emptyScore.setVisibility(View.VISIBLE);
        else
            emptyScore.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private static String getFragmentDate(int position) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.roll(Calendar.DAY_OF_MONTH, position - 2);
        SimpleDateFormat mDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        return mDate.format(calendar.getTime());
    }

    @Override
    public void onRefresh() {
        update_scores();
    }
}
