package com.infiniteexpandablelistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Subjects> mExampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mExampleList = generateList(1);

        ExpandableListView mainListView = (ExpandableListView) findViewById(R.id.ParentLevel);
        mainListView.setAdapter(new InfiniteListAdapter(this, mExampleList));
    }

    private ArrayList<Subjects> generateList(int level) {
        ArrayList<Subjects> subjectses = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Subjects subjects = new Subjects();
            subjects.setTitle("Level " + level + " -(" + i + ")");
            subjects.setId(i);
            if (level < 5)
                subjects.addSubjects(generateList(level + 1));
            subjectses.add(subjects);
        }

        return subjectses;
    }
}
