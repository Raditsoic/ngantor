    package com.example.ngantor;

    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.TextView;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.lifecycle.ViewModelProvider;

    import com.example.ngantor.data.models.SleepSession;
    import com.example.ngantor.data.repositories.SleepSessionRepository;
    import com.example.ngantor.data.repositories.SoundLogRepository;
    import com.example.ngantor.utils.fetch.SleepViewModel;
    import com.example.ngantor.utils.fetch.SleepViewModelFactory;
    import com.github.mikephil.charting.charts.LineChart;
    import com.github.mikephil.charting.data.Entry;
    import com.github.mikephil.charting.data.LineData;
    import com.github.mikephil.charting.data.LineDataSet;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;

    public class LogDetailActivity extends AppCompatActivity {
        private TextView dateTextView;
        private TextView lightLevelTextView;
        private TextView sleepTimetextView;
        private TextView sleepQualityTextView;
        private LineChart lineChart;
        private SleepViewModel sleepViewModel;
        private SoundLogRepository soundLogRepository;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.detail_logs);

            dateTextView = findViewById(R.id.sleep_stats_date);
            lightLevelTextView = findViewById(R.id.light_level_value);
            sleepTimetextView = findViewById(R.id.sleep_time_value);
            sleepQualityTextView = findViewById(R.id.sleep_rating_value);
            lineChart = findViewById(R.id.graph);


            int sessionId = getIntent().getIntExtra("SLEEP_SESSION_ID", -1);

            if (sessionId != -1) {
                soundLogRepository = new SoundLogRepository(this);
                sleepViewModel = new ViewModelProvider(
                        this,
                        new SleepViewModelFactory(new SleepSessionRepository(this))
                ).get(SleepViewModel.class);

                sleepViewModel.getSleepSessionById(sessionId).observe(this, sleepSession -> {
                    if (sleepSession != null) {
                        populateDetails(sleepSession);
                        fetchAndDisplaySoundLogs(sessionId);
                    }
                });
            }
        }

        private void populateDetails(SleepSession sleepSession) {
            dateTextView.setText(formatDate(sleepSession.getStartTime()));
            lightLevelTextView.setText(sleepSession.getLightLevel() + " Lux");
            sleepQualityTextView.setText(formatQuality(sleepSession.getSleepQuality()) + "%");
            long startTime = sleepSession.getStartTime();
            long endTime = sleepSession.getEndTime();

            if (startTime > 0 && endTime > 0) {
                long durationMillis = endTime - startTime;
                String sleepTime = formatDuration(durationMillis);
                sleepTimetextView.setText(sleepTime);
            }
        }

        private int formatQuality(float quality) {
            return (int) (quality * 100);
        }

        private String formatDate(long timestamp) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            return formatter.format(new Date(timestamp));
        }

        private String formatDuration(long durationMillis) {
            long hours = durationMillis / (1000 * 60 * 60); // Calculate hours
            long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60); // Calculate minutes

            return hours + "h " + minutes + "min";
        }

        private void fetchAndDisplaySoundLogs(int sessionId) {
            soundLogRepository.getLogsBySleepId(sessionId).observe(this, soundLogs -> {
                if (soundLogs == null || soundLogs.isEmpty()) {

                    lineChart.setVisibility(View.GONE);

                    TextView noDataTextView = findViewById(R.id.no_sound_data_text);
                    noDataTextView.setVisibility(View.VISIBLE);
                    noDataTextView.setText("Can't show Sound Data");
                    noDataTextView.setTextColor(getResources().getColor(R.color.white));
                    return;
                }
                
                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < soundLogs.size(); i++) {
                    Log.i("Sounds", String.valueOf(soundLogs.get(i).getDecibel()));
                    entries.add(new Entry(i, soundLogs.get(i).getDecibel()));
                }

                Log.i("Sounds", "Done");



                LineDataSet lineDataSet = new LineDataSet(entries, "Decibel Level");
                lineDataSet.setColor(getResources().getColor(R.color.white));
                lineDataSet.setValueTextColor(getResources().getColor(R.color.black));

                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();
            });
        }
    }
