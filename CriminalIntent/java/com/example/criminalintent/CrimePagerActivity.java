package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.crime_id";
    private ViewPager mViewPager;
    private Button mToFirstButton;
    private Button mToLastButton;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context context, UUID crimeId){
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager = (ViewPager)findViewById(R.id.crime_view_pager);
        mToFirstButton = (Button)findViewById(R.id.crime_view_first_button);
        mToLastButton = (Button)findViewById(R.id.crime_view_last_button);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return CrimeFragment.newInstance(mCrimes.get(position).getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        for(int i = 0;i<mCrimes.size();++i){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mToFirstButton.setEnabled(position != 0);
                mToLastButton.setEnabled(position != mCrimes.size()-1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mToFirstButton.setEnabled(mViewPager.getCurrentItem() != 0);
        mToLastButton.setEnabled(mViewPager.getCurrentItem() != mCrimes.size()-1);
        mToFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mToLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size()-1);
            }
        });
    }
}
