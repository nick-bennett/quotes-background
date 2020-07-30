package edu.cnm.deepdive.quotesbackground.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.RxWorker;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.WorkerParameters;
import edu.cnm.deepdive.quotesbackground.R;
import io.reactivex.Single;
import java.util.concurrent.TimeUnit;

public class PeriodicUpdateService
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  @SuppressLint("StaticFieldLeak")
  private static Context context;

  private final QuoteRepository quoteRepository;
  private final SharedPreferences preferences;
  private final String pollingIntervalPrefKey;
  private final int defaultPollingInterval;

  private WorkRequest request;

  private PeriodicUpdateService() {
    quoteRepository = new QuoteRepository(context);
    pollingIntervalPrefKey = context.getString(R.string.poll_interval_pref_key);
    defaultPollingInterval = context.getResources().getInteger(R.integer.poll_interval_pref_default);
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    preferences.registerOnSharedPreferenceChangeListener(this);
  }

  public static void setContext(Context context) {
    PeriodicUpdateService.context = context;
  }

  public static PeriodicUpdateService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(context.getString(R.string.poll_interval_pref_key))) {
      schedule();
    }
  }

  public synchronized void schedule() {
    int pollingInterval = preferences.getInt(pollingIntervalPrefKey, defaultPollingInterval);
    if (pollingInterval > 0) {
      request = new OneTimeWorkRequest.Builder(PeriodicUpdateService.Worker.class)
          .setInitialDelay(pollingInterval, TimeUnit.MINUTES)
          .build();
      WorkManager.getInstance(context).enqueue(request);
    }
  }

  private static class InstanceHolder {

    private static final PeriodicUpdateService INSTANCE = new PeriodicUpdateService();

  }

  public static class Worker extends RxWorker {

    private final PeriodicUpdateService service;
    private final QuoteRepository quoteRepository;

    public Worker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
      super(appContext, workerParams);
      service = PeriodicUpdateService.getInstance();
      quoteRepository = service.quoteRepository;
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
      if (getId().equals(service.request.getId())) {
        return quoteRepository.fetch()
            .andThen(Single.fromCallable(() -> {
              service.schedule();
              return Result.success();
            }));
      } else {
        return Single.just(Result.success());
      }
    }

  }

}
