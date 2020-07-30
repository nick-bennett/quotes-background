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
package edu.cnm.deepdive.quotesbackground.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.quotesbackground.R;
import edu.cnm.deepdive.quotesbackground.view.QuoteAdapter.Holder;
import edu.cnm.deepdive.quotesbackground.model.entity.Quote;
import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<Holder> {

  private final Context context;
  private final List<Quote> quotes;
  private final String citationFormat;
  private final String anonymousCitation;

  public QuoteAdapter(@NonNull Context context, @NonNull List<Quote> quotes) {
    this.context = context;
    this.quotes = quotes;
    citationFormat = context.getString(R.string.citation_format);
    anonymousCitation = context.getString(R.string.anonymous_citation);
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(LayoutInflater.from(context).inflate(R.layout.item_quote, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(position);
  }

  @Override
  public int getItemCount() {
    return quotes.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    private final TextView text;
    private final TextView author;

    private Holder(@NonNull View itemView) {
      super(itemView);
      text = itemView.findViewById(R.id.text);
      author = itemView.findViewById(R.id.author);
    }

    private void bind(int position) {
      Quote quote = quotes.get(position);
      text.setText(quote.getText());
      author.setText(String.format(citationFormat,
          (
              (quote.getAuthor() != null && !quote.getAuthor().isEmpty())
                  ? quote.getAuthor()
                  : anonymousCitation)
          )
      );
    }

  }

}
