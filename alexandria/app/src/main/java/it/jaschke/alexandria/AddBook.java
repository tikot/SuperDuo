package it.jaschke.alexandria;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.scan.CaptureActivity;
import it.jaschke.alexandria.services.BookService;

public class AddBook extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private final static int LOADER_ID = 1;

    private final String EAN_CONTENT="eanContent";

    private CardView bookCard;
    private TextView bookTitle;
    private TextView bookSubTitle;
    private TextView authors;
    private TextView categories;
    private ImageView bookCover;

    private TextView bookStatus;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ean != null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bookCard = (CardView) findViewById(R.id.book_card);

        bookTitle = (TextView) findViewById(R.id.book_title);
        bookSubTitle = (TextView) findViewById(R.id.book_sub_title);
        authors = (TextView) findViewById(R.id.authors);
        categories = (TextView) findViewById(R.id.categories);
        bookCover = (ImageView) findViewById(R.id.book_cover);

        bookStatus = (TextView) findViewById(R.id.book_status);

        ean = (EditText) findViewById(R.id.isbn_number);
        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!Utilities.isConnected(getApplicationContext())) {
                    bookStatus.setText(R.string.net_work_error);
                    bookStatus.setVisibility(View.VISIBLE);
                    return;
                }

                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    hideBookCard(false);
                    return;
                }

                //Once we have an ISBN, start a book intent
                //EAN_13
                Intent bookIntent = new Intent(getApplicationContext(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                startService(bookIntent);
                restartLoader();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.camera_scan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddBook.this, CaptureActivity.class);
                startActivityForResult(intent, 0);

            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getApplicationContext(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                startService(bookIntent);
                ean.setText("");
            }
        });

        if (savedInstanceState != null) {
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (ean.getText().length() == 0) {
            return null;
        }
        String eanStr = ean.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978")){
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getApplicationContext(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            bookStatus.setText(R.string.not_found);
            bookStatus.setVisibility(View.VISIBLE);
            return;
        }

        bookStatus.setVisibility(View.INVISIBLE);

        String bookTitleData = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        bookTitle.setText(bookTitleData);

        String bookSubTitleData = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        bookSubTitle.setText(bookSubTitleData);


        String authorsData = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authorsData != null ? authorsData.split(",") : new String[0];


        authors.setLines(authorsArr.length);
        authors.setText(authorsData != null ? authorsData.replace(",", "\n") : "");

        String categoriesData = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        categories.setText(categoriesData);

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(this)
                .load(imgUrl)
                .crossFade()
                .placeholder(R.color.primary_light)
                .into(bookCover);

        hideBookCard(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        restartLoader();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String ean_number = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                if (!format.equals("EAN_13")) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.need_isbn,
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                ean.setText(ean_number);
            }
        }

    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void hideBookCard(boolean show) {
        if (show)
            bookCard.setVisibility(CardView.VISIBLE);
        else
            bookCard.setVisibility(CardView.INVISIBLE);
    }

}
