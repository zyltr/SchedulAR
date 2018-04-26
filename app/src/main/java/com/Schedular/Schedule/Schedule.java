package com.Schedular.Schedule;

import android.graphics.Bitmap;

public class Schedule
{

    private String title;
    private String author;
    private String ratingAvg;
    private String ratingTotal;
    private String priceList;
    private String priceYour;
    private String targetId;
    private Bitmap thumb;
    private String bookUrl;

    private String target;
    private String course;
    private String schedule;
    private String professor;

    public Schedule () { }


    public void setTarget(String target) { this.target = target; }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getTarget() { return target; }

    public String getCourse() {
        return course;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getProfessor() {
        return professor;
    }

    // TODO -> IGNORE

    public String getTitle()
    {
        return title;
    }


    public void setTitle(String title)
    {
        this.title = title;
    }


    public String getAuthor()
    {
        return author;
    }


    public void setAuthor(String author)
    {
        this.author = author;
    }


    public String getRatingAvg()
    {
        return ratingAvg;
    }


    public void setRatingAvg(String ratingAvg)
    {
        this.ratingAvg = ratingAvg;
    }


    public String getRatingTotal()
    {
        return ratingTotal;
    }


    public void setRatingTotal(String ratingTotal)
    {
        this.ratingTotal = ratingTotal;
    }


    public String getPriceList()
    {
        return priceList;
    }


    public void setPriceList(String priceList)
    {
        this.priceList = priceList;
    }


    public String getPriceYour()
    {
        return priceYour;
    }


    public void setPriceYour(String priceYour)
    {
        this.priceYour = priceYour;
    }


    public String getTargetId()
    {
        return targetId;
    }


    public void setTargetId(String targetId)
    {
        this.targetId = targetId;
    }


    public Bitmap getThumb()
    {
        return thumb;
    }


    public void setThumb(Bitmap thumb)
    {
        this.thumb = thumb;
    }


    public String getBookUrl()
    {
        return bookUrl;
    }


    public void setBookUrl(String bookUrl)
    {
        this.bookUrl = bookUrl;
    }


    public void recycle()
    {
        // Cleans the Thumb bitmap variable
        thumb.recycle();
        thumb = null;
    }

}
