package edu.cnm.deepdive.quotesbackground.service;

import android.annotation.SuppressLint;
import android.content.Context;

public class PeriodicUpdateService {

  @SuppressLint("StaticFieldLeak")
  private static Context context;

  public static void setContext(Context context) {
    PeriodicUpdateService.context = context;
  }


}
