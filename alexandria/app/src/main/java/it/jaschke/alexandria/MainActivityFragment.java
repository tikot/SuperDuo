package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView bookList;
    private BookListAdapter bookListAdapter;
    private StaggeredGridLayoutManager layoutManager;

    private String searchQuery = null;
    private TextView emptyText;


    private int position = RecyclerView.NO_POSITION;

    private final static int LOADER_ID = 10;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        emptyText = (TextView) rootView.findViewById(R.id.empty);
        bookList = (RecyclerView) rootView.findViewById(R.id.list_books);

        layoutManager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.columns), StaggeredGridLayoutManager.VERTICAL);
        bookList.setLayoutManager(layoutManager);

        bookListAdapter = new BookListAdapter(this);
        bookListAdapter.setViewClick(new BookListAdapter.OnClickHandler() {
            @Override
            public void onClickShare(String text) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, text));
                startActivity(shareIntent);
            }

            @Override
            public void onClickDelete(String id) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, id);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                restartLoader();
            }
        });
        bookList.setAdapter(bookListAdapter);


        FloatingActionButton addBook = (FloatingActionButton) rootView.findViewById(R.id.add_book);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddBook();
            }
        });

        // load cursor
        getLoaderManager().initLoader(LOADER_ID, null, this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        /**
         * restart the loader and show full list of books
         */
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                MainActivityFragment.this.restartLoader();
                return true;
            }
        });

        SearchView searchText = (SearchView) searchMenuItem.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivityFragment.this.searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (searchQuery != null) {
            final String selection = AlexandriaContract.BookEntry.TITLE + " LIKE ? OR " +
                    AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
            searchQuery = "%" + searchQuery + "%";
            CursorLoader cursorLoader = new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.FULL_CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchQuery, searchQuery},
                    null
            );
            //query = null;
            return cursorLoader;
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.FULL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookListAdapter.swapCursor(data);
        final int size = data.getCount();

        if (searchQuery == null && size == 0) {
            emptyText.setText(R.string.no_books);
            emptyText.setVisibility(TextView.VISIBLE);
        }
        else if (size == 0) {
            emptyText.setText(R.string.not_found);
            emptyText.setVisibility(TextView.VISIBLE);
            searchQuery = null;
        }
        else {
            emptyText.setVisibility(TextView.INVISIBLE);
        }

        if (position != RecyclerView.NO_POSITION) {
            bookList.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }

    private void startAddBook() {
        Intent intent = new Intent(getActivity(), AddBook.class);
        startActivity(intent);
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void searchBooks(String newQuery) {
        searchQuery = newQuery;
        restartLoader();
    }
}