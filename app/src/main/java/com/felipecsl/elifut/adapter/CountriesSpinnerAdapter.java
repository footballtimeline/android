package com.felipecsl.elifut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.felipecsl.elifut.R;
import com.felipecsl.elifut.models.Nation;
import com.felipecsl.elifut.widget.NationTextView;
import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import java.util.List;

public final class CountriesSpinnerAdapter extends BaseAdapter {
  private final Context context;
  private final List<Nation> countries;
  private final LayoutInflater layoutInflater;

  public CountriesSpinnerAdapter(Context context, List<Nation> countries) {
    this.context = Preconditions.checkNotNull(context);
    this.countries = Preconditions.checkNotNull(countries);
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public int getCount() {
    return countries.size();
  }

  @Override public Object getItem(int position) {
    return countries.get(position);
  }

  @Override public long getItemId(int position) {
    return ((Nation) getItem(position)).id();
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    final NationTextView v;
    if (convertView == null) {
      v = (NationTextView) layoutInflater.inflate(R.layout.layout_spinner_country, parent,
          false);
    } else {
      v = (NationTextView) convertView;
    }

    Nation country = (Nation) getItem(position);
    v.setText(country.toString());

    Picasso.with(context).cancelRequest(v);

    Picasso.with(context)
        .load(country.large_image())
//        .placeholder(R.drawable.image_placeholder)
        .into(v);

    return v;
  }
}
