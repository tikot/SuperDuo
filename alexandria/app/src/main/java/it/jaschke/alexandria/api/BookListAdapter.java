package it.jaschke.alexandria.api;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.Utilities;
import it.jaschke.alexandria.data.AlexandriaContract;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {

    private Cursor cursor;
    private Fragment fragment;
    private static OnClickHandler clickHandler;


    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView bookCover;
        public final TextView authors;
        public final TextView bookTitle;
        public final TextView bookSubTitle;
        public final TextView categories;

        public final LinearLayout bookDesc;
        public final TextView theDesc;

        public final Button del;
        public final Button share;
        public final ImageButton show;

        public BookViewHolder(View view) {
            super(view);
            bookCover = (ImageView) view.findViewById(R.id.book_cover);
            authors = (TextView) view.findViewById(R.id.authors);
            bookTitle = (TextView) view.findViewById(R.id.book_title);
            bookSubTitle = (TextView) view.findViewById(R.id.book_sub_title);
            categories = (TextView) view.findViewById(R.id.categories);

            bookDesc = (LinearLayout) view.findViewById(R.id.book_description);
            theDesc = (TextView) view.findViewById(R.id.full_book_description);

            del = (Button) view.findViewById(R.id.delete_button);
            share = (Button) view.findViewById(R.id.share_button);
            show = (ImageButton) view.findViewById(R.id.show_desc);
            show.setOnClickListener(this);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickHandler.onClickShare(bookTitle.getText().toString());
                }
            });

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor.moveToPosition(getAdapterPosition());
                    clickHandler.onClickDelete(cursor.getString(
                            cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookViewHolder.this.onClick(show);
                }
            });
        }

        @Override
        public void onClick(View view) {
            view.setSelected(!view.isSelected());
            if (!view.isSelected()) {
                Utilities.ViewExpand.collapse(bookDesc);
                show.setImageResource(R.drawable.ic_expand_less_36);
            }
            else {
                Utilities.ViewExpand.expand(bookDesc);
                show.setImageResource(R.drawable.ic_expand_more_36);
            }
        }
    }


    public interface OnClickHandler {
        void onClickShare(String text);
        void onClickDelete(String id);
    }

    public BookListAdapter(Fragment newFragment) {
        fragment = newFragment;

    }

    public void setViewClick(OnClickHandler click) {
        clickHandler = click;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);

        BookViewHolder vh = new BookViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        cursor.moveToPosition(position);

        //int bookId = cursor.getInt(0);

        String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(fragment)
                .load(imgUrl)
                .crossFade()
                .placeholder(R.color.primary_light)
                .into(holder.bookCover);

        String authors = cursor.getString(cursor.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        holder.authors.setText(authors);

        String categories = cursor.getString(cursor.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        holder.categories.setText(categories);

        String bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        holder.bookTitle.setText(bookTitle);

        String bookSubTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        holder.bookSubTitle.setText(bookSubTitle);

        String description = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        holder.theDesc.setText(description);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0 ;
    }

    public void swapCursor(Cursor data) {
        cursor = data;
        notifyDataSetChanged();
    }
}
