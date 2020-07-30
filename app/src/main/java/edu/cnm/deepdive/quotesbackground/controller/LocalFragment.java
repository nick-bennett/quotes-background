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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.quotesbackground.R;
import edu.cnm.deepdive.quotesbackground.view.QuoteAdapter;
import edu.cnm.deepdive.quotesbackground.viewmodel.MainViewModel;

public class LocalFragment extends Fragment implements OnQueryTextListener {

  private MainViewModel viewModel;
  private SearchView search;
  private RecyclerView quotes;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_local, container, false);
    search = root.findViewById(R.id.search);
    quotes = root.findViewById(R.id.quotes);
    search.setOnQueryTextListener(this);
    return root;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    viewModel.getFilteredQuotes().observe(getViewLifecycleOwner(), (quotes) ->
        this.quotes.setAdapter(new QuoteAdapter(getContext(), quotes)));
    viewModel.getFilter().observe(getViewLifecycleOwner(), (filter) -> {
      if (!search.getQuery().equals(filter)) {
        search.setQuery(filter, false);
      }
    });
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    viewModel.setFilter(newText);
    return true;
  }

}