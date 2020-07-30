/*
 *  Copyright 2020 Deep Dive Coding/CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.quotesbackground.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.Transformations;
import edu.cnm.deepdive.quotesbackground.model.entity.Quote;
import edu.cnm.deepdive.quotesbackground.service.QuoteRepository;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;

public class MainViewModel extends AndroidViewModel implements LifecycleObserver {

  private final QuoteRepository quoteRepository;
  private final MutableLiveData<String> filter;
  private final LiveData<List<Quote>> filteredQuotes;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  public MainViewModel(@NonNull Application application) {
    super(application);
    quoteRepository = new QuoteRepository(application);
    filter = new MutableLiveData<>("");
    filteredQuotes = Transformations.switchMap(filter, quoteRepository::search);
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public LiveData<String> getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter.setValue(filter);
  }

  public LiveData<List<Quote>> getFilteredQuotes() {
    return filteredQuotes;
  }

  public LiveData<List<Quote>> getQuotes() {
    return quoteRepository.list();
  }

  public LiveData<Quote> getMostRecentQuote() {
    return quoteRepository.getMostRecent();
  }

  public void fetchRemote() {
    pending.add(
        quoteRepository.fetch()
            .subscribe(
                () -> {},
                throwable::postValue
            )
    );
  }

  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }

}
