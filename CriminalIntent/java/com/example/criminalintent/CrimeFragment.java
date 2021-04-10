package com.example.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_TIME = "DialogTime";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_TIME = 1;
    public static final int REQUEST_CONTACT = 2;
    public static final int REQUEST_PHOTO = 3;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState){
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_data);
        mDateButton.setText(mCrime.getDate().toString());
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm  = getFragmentManager();
                DatePickerFragment datePicker = DatePickerFragment.newInstance(mCrime.getDate());
                datePicker.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                datePicker.show(fm, DIALOG_DATE);
            }
        });

        mCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });
        mCheckBox.setChecked(mCrime.isSolved());

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(view -> {
            Intent i = ShareCompat.IntentBuilder
                    .from(getActivity())
                    .setType("text/plain")
                    .setText(getCrimeReport())
                    .setSubject(getString(R.string.crime_report_subject))
                    .setChooserTitle(R.string.send_report)
                    .createChooserIntent();
            startActivity(i);
        });

        mSuspectButton = v.findViewById(R.id.crime_suspect);
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton.setOnClickListener(view -> {
            startActivityForResult(pickContact, REQUEST_CONTACT);
        });
        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        PackageManager pM = getActivity().getPackageManager();
        if(pM.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = v.findViewById(R.id.crime_camera);
        mPhotoView = v.findViewById(R.id.crime_photo);
        updatePhotoView();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canPhotoTake = mPhotoFile != null && captureImage.resolveActivity(pM) != null;
        mPhotoButton.setEnabled(canPhotoTake);

        mPhotoButton.setOnClickListener(view -> {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.criminalintent.fileprovider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                    .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
            for(ResolveInfo activity : cameraActivities){
                getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            startActivityForResult(captureImage, REQUEST_PHOTO);
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE){
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }
        if(requestCode == REQUEST_TIME){
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mCrime.getDate());
            Calendar time = Calendar.getInstance();
            time.setTime(date);
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int minute = time.get(Calendar.MINUTE);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            mCrime.setDate(calendar.getTime());
            updateDate();
        }
        if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //выполняем запрос
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try{
                if(c.getCount() == 0){
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
        if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }

    }

    private void updateDate() {
        mDateButton.setText(CustomDateFormat.dateToStringBuffer(mCrime.getDate()));
    }
    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        } else {
            mPhotoView.setImageBitmap(PicturesUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity()));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.remove_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime.getId());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
}
