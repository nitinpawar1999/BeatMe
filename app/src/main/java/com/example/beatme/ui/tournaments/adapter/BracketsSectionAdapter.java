package com.example.beatme.ui.tournaments.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.beatme.ui.tournaments.Fragment.BracketsColumnFragment;
import com.example.beatme.ui.tournaments.model.ColumnData;

import java.util.ArrayList;

public class BracketsSectionAdapter  extends FragmentStatePagerAdapter {

    private ArrayList<ColumnData> sectionList;


    public BracketsSectionAdapter(FragmentManager fm, ArrayList<ColumnData> sectionList) {
        super(fm);
        this.sectionList =sectionList;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("colomn_data", this.sectionList.get(position));
        BracketsColumnFragment fragment = new BracketsColumnFragment();
        bundle.putInt("section_number", position);
        if (position > 0)
            bundle.putInt("previous_section_size", sectionList.get(position - 1).getMatches().size());
        else if (position == 0)
            bundle.putInt("previous_section_size", sectionList.get(position).getMatches().size());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return this.sectionList.size();
    }
}
