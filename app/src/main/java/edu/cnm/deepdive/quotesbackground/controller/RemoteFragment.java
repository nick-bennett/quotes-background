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
package edu.cnm.deepdive.quotesbackground.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.quotesbackground.R;
import edu.cnm.deepdive.quotesbackground.model.entity.Quote;
import edu.cnm.deepdive.quotesbackground.viewmodel.MainViewModel;

/**
 * Presents a display of the {@link Quote} instance most recently saved to the database, along with
 * a {@link com.google.android.material.floatingactionbutton.FloatingActionButton} that, when
 * pressed, causes a {@code Quote} to be retrieved from the remote web service. (As a rule, the most
 * recently saved {@code Quote} is the one most recently retrieved from the web service.)
 */
public class RemoteFragment extends Fragment {

  private String anonymousCitation;
  private TextView text;
  private TextView author;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    anonymousCitation = getString(R.string.anonymous_citation);
    View root = inflater.inflate(R.layout.fragment_remote, container, false);
    text = root.findViewById(R.id.text);
    author = root.findViewById(R.id.author);
    return root;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    viewModel.getMostRecentQuote().observe(getViewLifecycleOwner(), this::displayQuote);
    view.findViewById(R.id.refresh).setOnClickListener((v) -> viewModel.fetchRemote());
  }

  private void displayQuote(Quote quote) {
    if (quote != null) {
      text.setText(quote.getText());
      author.setText(getString(R.string.citation_format,
          (
              (quote.getAuthor() != null && !quote.getAuthor().isEmpty())
                  ? quote.getAuthor()
                  : anonymousCitation)
          )
      );
    }
  }

}