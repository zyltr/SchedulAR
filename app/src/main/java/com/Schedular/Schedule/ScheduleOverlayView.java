package com.Schedular.Schedule;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Schedular.R;

import org.w3c.dom.Text;


public class ScheduleOverlayView extends RelativeLayout
{
    public ScheduleOverlayView(Context context)
    {
        this(context, null);
    }


    public ScheduleOverlayView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }


    public ScheduleOverlayView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflateLayout(context);
    }


    // Inflates the Custom View Layout
    private void inflateLayout(Context context)
    {

        final LayoutInflater inflater = LayoutInflater.from(context);

        // Generates the layout for the view
        // TODO -> Replace With Our "Schedule Layout"
//        inflater.inflate(R.layout.bitmap_layout, this, true);
        inflater.inflate(R.layout.schedule_bitmap_layout, this, true);
    }

    // TODO -> Our Custom View Methods for Uppdating Information
    public void setCourse ( String course )
    {
        TextView courseTextView = (TextView) findViewById (R.id.courseTextView);
        courseTextView.setText (course);
    }

    public void setSchedule( String schedule )
    {
        TextView scheduleTextView = (TextView) findViewById (R.id.scheduleTextView);
        scheduleTextView.setText (schedule);
    }

    public void setProfessor ( String professor )
    {
        TextView professorTextView = (TextView) findViewById (R.id.professorTextView);
        professorTextView.setText (professor);
    }



    // Sets Book title in View
    public void setBookTitle(String bookTitle)
    {
//        TextView tv = (TextView) findViewById(R.id.custom_view_title);
//        tv.setText(bookTitle);
    }


    // Sets Book Author in View
    public void setBookAuthor(String bookAuthor)
    {
//        TextView tv = (TextView) findViewById(R.id.custom_view_author);
//        tv.setText(bookAuthor);
    }


    // Sets Book Price in View
    public void setBookPrice(String bookPrice)
    {
//        TextView tv = (TextView) findViewById(R.id.custom_view_price_old);
//        tv.setText(getContext().getString(R.string.string_usd) + bookPrice);
    }


    // Sets Book Number of Ratings in View
    public void setBookRatingCount(String ratingCount)
    {
//        TextView tv = (TextView) findViewById(R.id.custom_view_rating_text);
//        tv.setText(getContext().getString(R.string.string_openParentheses)
//                + ratingCount + getContext().getString(R.string.string_ratings)
//                + getContext().getString(R.string.string_closeParentheses));
    }


    // Sets Book Special Price in View
    public void setYourPrice(String yourPrice)
    {
//        TextView tv = (TextView) findViewById(R.id.badge_price_value);
//        tv.setText(getContext().getString(R.string.string_usd) + yourPrice);
    }


    // Sets Book Cover in View from a bitmap
    public void setCoverViewFromBitmap(Bitmap coverBook)
    {
//        ImageView iv = (ImageView) findViewById(R.id.custom_view_book_cover);
//        iv.setImageBitmap(coverBook);
    }


    // Sets Book Rating in View
    public void setRating(String rating)
    {
//        RatingBar rb = (RatingBar) findViewById(R.id.custom_view_rating);
//        rb.setRating(Float.parseFloat(rating));
    }
}
