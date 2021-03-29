package com.example.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private TextView mTextView;
    private boolean mSubtitleVisible;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    public static class State {
        public final static int NoPolice = 0;
        public final static int RequiresPolice = 1;
    }
    private abstract class CrimeHolder extends RecyclerView.ViewHolder {
        protected Crime mCrime;
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int layoutId){
            super(inflater.inflate(layoutId, parent, false));
            //mPoliceCall = (Button)itemView.findViewById(R.id.police_call_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivity(intent);
                }
            });
        }
        public  void bind(Crime crime){
            mCrime = crime;
        }
    }
    public class NoPoliceCrimeHolder extends CrimeHolder{
        private TextView mTitleCrime;
        private TextView mTitleDate;
        private ImageView mImageView;
        public NoPoliceCrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater, parent, R.layout.list_item_crime);
            mTitleCrime = (TextView)itemView.findViewById(R.id.crime_title);
            mTitleDate = (TextView)itemView.findViewById(R.id.crime_date);
            mImageView = (ImageView)itemView.findViewById(R.id.crime_solved);
        }
        @Override
        public void bind(Crime crime){
            super.bind(crime);
            mTitleCrime.setText(crime.getTitle());
            DateFormat df = new CustomDateFormat();
            mTitleDate.setText(df.format(crime.getDate()));
            mImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }
    }
    public class RequiresPoliceCrimeHolder extends CrimeHolder{
        private TextView mTitleCrime;
        private TextView mTitleDate;
        private Button mPoliceButton;
        public RequiresPoliceCrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater, parent, R.layout.list_item_crime_police);
            mTitleCrime = (TextView)itemView.findViewById(R.id.crime_title);
            mTitleDate = (TextView)itemView.findViewById(R.id.crime_date);
            mPoliceButton = (Button)itemView.findViewById(R.id.police_call_button);
            mPoliceButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"Police called for " + mCrime.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void bind(Crime crime){
            super.bind(crime);
            mTitleCrime.setText(crime.getTitle());
            mTitleDate.setText(crime.getDate().toString());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public int getItemViewType(int position) {
            return (mCrimes.get(position).isRequiresPolice() ? State.RequiresPolice : State.NoPolice);
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if(viewType == State.RequiresPolice){
                return new RequiresPoliceCrimeHolder(inflater, parent);
            }
            return new NoPoliceCrimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            holder.bind(mCrimes.get(position));
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
    }
    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mCrimeAdapter == null){
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.setCrimes(crimes);
            mCrimeAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
        updateHintOfEmptyList();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTextView = (TextView) view.findViewById(R.id.text_view_hint_add_crime);
        if(savedState != null){
            mSubtitleVisible = savedState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get((getActivity())).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_format, crimeCount, crimeCount);
        if(!mSubtitleVisible){
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
    private void updateHintOfEmptyList(){
        int visibility = (mCrimeAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
        mTextView.setVisibility(visibility);
    }
}
