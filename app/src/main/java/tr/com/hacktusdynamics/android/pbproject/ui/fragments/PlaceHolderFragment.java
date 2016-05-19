package tr.com.hacktusdynamics.android.pbproject.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.MyApplication;
import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.ui.activities.CreateAlarmActivity;
import tr.com.hacktusdynamics.android.pbproject.ui.view.AlarmCardView;

import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_NAME;


/**
 * A placeholder fragment containing a simple view.
 * TODO: onCreateView update the view from stored array longs (long[] alarms) in MyLab or Activity
 * so that we can store the state of the view while swapping
 */
public class PlaceHolderFragment extends Fragment {
    private static final String TAG = PlaceHolderFragment.class.getSimpleName();

    int mSectionNumber;
    AlarmCardView mAlarmCardView1, mAlarmCardView2, mAlarmCardView3;
    TextDrawable boxNumberDrawable;
    SharedPreferences sp;
    String userProfileId;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceHolderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceHolderFragment newInstance(int sectionNumber) {
        PlaceHolderFragment fragment = new PlaceHolderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        //fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_alarm, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        textView.setText(getString(R.string.section_format, mSectionNumber));
        sp = MyApplication.sApplicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userProfileId = sp.getString(Constants.PREF_CURRENT_USER_UUID, null);

        mAlarmCardView1 = (AlarmCardView) rootView.findViewById(R.id.alarm_card1);
        boxNumberDrawable = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .bold()
                .endConfig()
                .buildRoundRect(Integer.toString(mSectionNumber * 3), Color.GREEN, 40);
        mAlarmCardView1.setImageDrawable(boxNumberDrawable);
        mAlarmCardView1.setBoxNumber(mSectionNumber * 3);
        mAlarmCardView1.setOnTimeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "HiTimeButtonFromFragment", Toast.LENGTH_SHORT).show();
                //TODO: save the alarm to the mAlarms in Activity
                int boxNumber = mSectionNumber * 3;
                long alarmDateTime = mAlarmCardView1.getBoxAlarmDateTime();
                ((CreateAlarmActivity) getActivity()).addOrUpdateAlarm(boxNumber, alarmDateTime, userProfileId);
            }
        });
        mAlarmCardView1.setOnDeleteButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: delete the alarm from mAlarms in Activity
                int boxNumber = mSectionNumber * 3;
                //long alarmDateTime = mAlarmCardView1.getBoxAlarmDateTime();
                ((CreateAlarmActivity)getActivity()).removeAlarm(boxNumber);
                Toast.makeText(getActivity(), "HiDeleteButtonFromFragment_ boxNumber: " + boxNumber , Toast.LENGTH_SHORT).show();
            }
        });

        mAlarmCardView2 = (AlarmCardView) rootView.findViewById(R.id.alarm_card2);
        boxNumberDrawable = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .bold()
                .endConfig()
                .buildRoundRect(Integer.toString((mSectionNumber*3)-1), Color.GREEN, 40);
        mAlarmCardView2.setImageDrawable(boxNumberDrawable);
        mAlarmCardView2.setBoxNumber((mSectionNumber * 3) - 1);
        mAlarmCardView2.setOnTimeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int boxNumber = ((mSectionNumber*3)-1);
                long alarmDateTime = mAlarmCardView2.getBoxAlarmDateTime();
                ((CreateAlarmActivity) getActivity()).addOrUpdateAlarm(boxNumber, alarmDateTime, userProfileId);
            }
        });
        mAlarmCardView2.setOnDeleteButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int boxNumber = ((mSectionNumber*3)-1);
                //long alarmDateTime = mAlarmCardView2.getBoxAlarmDateTime();
                ((CreateAlarmActivity)getActivity()).removeAlarm(boxNumber);
            }
        });

        mAlarmCardView3 = (AlarmCardView) rootView.findViewById(R.id.alarm_card3);
        boxNumberDrawable = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .bold()
                .endConfig()
                .buildRoundRect(Integer.toString((mSectionNumber*3)-2), Color.GREEN, 40);
        mAlarmCardView3.setImageDrawable(boxNumberDrawable);
        mAlarmCardView3.setBoxNumber((mSectionNumber * 3) - 2);
        mAlarmCardView3.setOnTimeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int boxNumber = ((mSectionNumber * 3) - 2);
                long alarmDateTime = mAlarmCardView3.getBoxAlarmDateTime();
                ((CreateAlarmActivity)getActivity()).addOrUpdateAlarm(boxNumber, alarmDateTime, userProfileId);
            }
        });
        mAlarmCardView3.setOnDeleteButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int boxNumber = ((mSectionNumber * 3) - 2);
                ((CreateAlarmActivity)getActivity()).removeAlarm(boxNumber);
            }
        });

        Log.d(TAG, "onCreateView()" + mSectionNumber);
        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()" +mSectionNumber);
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()" + mSectionNumber);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop()" + mSectionNumber);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()" + mSectionNumber);
        super.onDestroy();
    }
}
