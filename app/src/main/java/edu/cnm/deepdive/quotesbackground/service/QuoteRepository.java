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
package edu.cnm.deepdive.quotesbackground.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.quotesbackground.R;
import edu.cnm.deepdive.quotesbackground.model.dao.QuoteDao;
import edu.cnm.deepdive.quotesbackground.model.entity.Quote;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuoteRepository {

  private static final String LIKE_PATTERN = "%%%s%%";
  private static final int MAX_NETWORK_THREADS = 4;

  private final Context context;
  private final QuoteDao quoteDao;
  private final ForismaticService forismaticService;
  private final ExecutorService networkPool;
  private final String mruSizePrefKey;

  public QuoteRepository(Context context) {
    this.context = context;
    quoteDao = QuoteDatabase.getInstance().getQuoteDao();
    forismaticService = ForismaticService.getInstance();
    networkPool = Executors.newFixedThreadPool(MAX_NETWORK_THREADS);
    mruSizePrefKey = context.getString(R.string.mru_size_pref_key);
  }

  public LiveData<List<Quote>> list() {
    return quoteDao.selectAllOrderByAuthor();
  }

  public LiveData<List<Quote>> search(String fragment) {
    return quoteDao.selectAllByAuthorLikeOrTextLikeOrderByText(
        String.format(LIKE_PATTERN, fragment));
  }

  public Single<Quote> get(long id) {
    return quoteDao.selectById(id)
        .subscribeOn(Schedulers.io());
  }

  public LiveData<Quote> getMostRecent() {
    return quoteDao.selectMostRecent();
  }

  public Single<Quote> getRandom() {
    return quoteDao.selectRandom()
        .subscribeOn(Schedulers.io());
  }

  public Completable fetch() {
    return forismaticService.get()
        .subscribeOn(Schedulers.from(networkPool))
        .flatMapCompletable(this::save)
        .andThen(
            Completable.fromSingle((observer) -> {
              SharedPreferences preferences = PreferenceManager
                  .getDefaultSharedPreferences(context);
              int mruSize = preferences.getInt(mruSizePrefKey,
                  context.getResources().getInteger(R.integer.mru_size_pref_default));
              quoteDao.deleteLru(mruSize)
                  .subscribe(observer);
            })
        );
  }

  public Completable save(Quote quote) {
    return (
        (quote.getId() > 0)
            ? Completable.fromSingle(quoteDao.update(quote))
            : Completable.fromSingle(quoteDao.insert(quote))
    )
        .subscribeOn(Schedulers.io());
  }

  public Completable delete(Quote quote) {
    return (
        (quote.getId() > 0)
            ? Completable.fromSingle(quoteDao.delete(quote))
            : Completable.fromAction(() -> {})
    )
        .subscribeOn(Schedulers.io());
  }

}
