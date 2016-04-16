package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import tr.com.hacktusdynamics.android.pbproject.MyApplication;
import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.Box;
import tr.com.hacktusdynamics.android.pbproject.models.MyLab;

public class AlarmPagerActivity extends AppCompatActivity {
    private static final String TAG = AlarmPagerActivity.class.getSimpleName();
    public static final String EXTRA_ALARM_ID = "tr.com.hacktusdynamics.android.pbproject.alarm_id";

    private MyLab myLab = null;

    private RecyclerView mBoxRecyclerView;
    private BoxAdapter mBoxAdapter;
    private TextView mEmptyElementTextView;

    public static Intent newIntent(Context applicationContext, int mAlarmId) {
        Intent intent = new Intent(applicationContext, AlarmPagerActivity.class);
        intent.putExtra(EXTRA_ALARM_ID, mAlarmId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_alarm_pager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myLab = MyLab.get(MyApplication.sApplicationContext);

        mBoxRecyclerView = (RecyclerView) findViewById(R.id.alarm_pager__recycler_view);
        mBoxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEmptyElementTextView = (TextView) findViewById(R.id.alarm_pager_empty_element_text_view);

        UpdateContentUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(AlarmPagerActivity.this, "Setting clicked!", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class BoxHolder extends RecyclerView.ViewHolder {
        private Box _box;
        private TextView boxNumberView;
        private TextView alarmDateTimeView;
        private TextView boxStateView;

        public BoxHolder(View itemView) {
            super(itemView);

            boxNumberView = (TextView) itemView.findViewById(R.id.box_number);
            alarmDateTimeView = (TextView) itemView.findViewById(R.id.alarm_datetime);
            boxStateView = (TextView) itemView.findViewById(R.id.box_state);
        }

        public void bindBox(Box box){
            _box = box;
            boxNumberView.setText(Integer.toString(_box.getBoxNumber()+1));
            alarmDateTimeView.setText(_box.getAlarmTime().toString());
            boxStateView.setText(_box.getBoxState().toString());
        }

    }

    private class BoxAdapter extends RecyclerView.Adapter<BoxHolder>{
        private List<Box> _boxes;

        //Constructor
        public BoxAdapter(List<Box> boxes){
            setBoxes(boxes);
        }
        //setters getters
        public void setBoxes(List<Box> boxes){
            _boxes = boxes;
        }

        /**
         * Called by RecyclerView when its need a new View to display an item.
         * You create a View and wrap it in a ViewHolder.
         */
        @Override
        public BoxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.list_item_box, parent, false);
            return new BoxHolder(itemView);
        }

        /**
         * This method will bind a ViewHolder's View to your model object
         * It receives ViewHolder and a position in your dataset.
         */
        @Override
        public void onBindViewHolder(BoxHolder holder, int position) {
            Box box = _boxes.get(position);
            holder.bindBox(box);
        }

        @Override
        public int getItemCount() {
            return _boxes.size();
        }
    }

    private void UpdateContentUI() {
        int alarmId = getIntent().getIntExtra(EXTRA_ALARM_ID, 0);
        Log.d(TAG, "UpdateContentUI called with alarmId: " + alarmId);

        List<Box> boxes = myLab.getBoxes(alarmId);
        if(mBoxAdapter == null){
            mBoxAdapter = new BoxAdapter(boxes);
            mBoxRecyclerView.setAdapter(mBoxAdapter);
        }else {
            mBoxAdapter.setBoxes(boxes);
            mBoxAdapter.notifyDataSetChanged();
        }

        //update toolbar subtitle
        int boxCount = boxes.size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, boxCount, boxCount);
        getSupportActionBar().setSubtitle(subtitle);

        mEmptyElementTextView.setVisibility(boxCount < 1 ? View.VISIBLE : View.GONE);
    }
}
