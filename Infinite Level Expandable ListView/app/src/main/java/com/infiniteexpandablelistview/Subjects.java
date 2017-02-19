package com.infiniteexpandablelistview;

import java.util.ArrayList;

/**
 * Created by Krunal-PC on 2/13/2017.
 */

public class Subjects {

    private ArrayList<Subjects> subList = new ArrayList<>();//second level sublist

    public Subjects(int id, String title, int sort, boolean sub) {
        this.id = id;
        this.title = title;
        this.sort = sort;
        this.hasSub = sub;
    }

    public Subjects(int id, String title) {
        this.id = id;
        this.title = title;
    }

    private int id;

    public Subjects() {

    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasSub() {
        return hasSub;
    }

    public void setHasSub(boolean hasSub) {
        this.hasSub = hasSub;
    }

    private int sort;
    private String title;
    private boolean hasSub;

    //second level sublist
    public ArrayList<Subjects> getSubList() {
        return subList;
    }

    // add second level sublist
    public void addSubject(Subjects subject) {
        this.subList.add(subject);
    }


    // add second level sublist
    public void addSubjects(ArrayList<Subjects> subject) {
        this.subList = (subject);
    }
}
