package com.example.gabrielsaruhashi.ramp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.gabrielsaruhashi.ramp.R;
import com.example.gabrielsaruhashi.ramp.SectionAdapter;
import com.example.gabrielsaruhashi.ramp.models.Section;

import java.util.ArrayList;

public class GuideIndex extends AppCompatActivity {

    public final static int NUMBER_OF_SECTIONS = 3;
    public final static String[] SECTION_TITLES = {"What are treatment options?", "What are payment options?", "Where do I start?"};

    ArrayList<Section> sections;

    RecyclerView rvSections;
    SectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_index);

        sections = new ArrayList<>();
        adapter = new SectionAdapter(sections);
        rvSections = (RecyclerView) findViewById(R.id.rvSections);
        rvSections.setLayoutManager(new GridLayoutManager(this, 1));
        rvSections.setAdapter(adapter);

        final Context context = this;
        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), GuideView.class);
                startActivityForResult(myIntent, 0);
                ((Activity) context).overridePendingTransition(0, 0);
            }}
        );

        getCurrentCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    //get the list of current objects
    private void getCurrentCategories() {
        for(int i = 0; i < NUMBER_OF_SECTIONS; i++){
            Section section = new Section(SECTION_TITLES[i], null);
            sections.add(section);
            adapter.notifyItemInserted(sections.size() - 1);
        }
    }
}

