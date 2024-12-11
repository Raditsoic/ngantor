    package com.example.ngantor.utils.fetch;

    import androidx.lifecycle.LiveData;
    import androidx.lifecycle.ViewModel;

    import com.example.ngantor.data.models.SleepSession;
    import com.example.ngantor.data.repositories.SleepSessionRepository;

    import java.util.List;

    public class SleepViewModel extends ViewModel {
        private final SleepSessionRepository repository;

        public SleepViewModel(SleepSessionRepository repository) {
            this.repository = repository;
        }

        public LiveData<List<SleepSession>> getAllSleepSessions() {
            return repository.getAllSleepSessions();
        }

        public LiveData<SleepSession> getSleepSessionById(int sessionId) {
            return repository.getSleepSessionById(sessionId); // This should return LiveData
        }
    }

