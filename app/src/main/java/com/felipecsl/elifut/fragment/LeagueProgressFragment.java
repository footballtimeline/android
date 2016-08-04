package com.felipecsl.elifut.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.felipecsl.elifut.R;
import com.felipecsl.elifut.adapter.LeagueMatchesAdapter;
import com.felipecsl.elifut.models.Club;
import com.felipecsl.elifut.models.LeagueRound;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LeagueProgressFragment extends ElifutFragment {
  @BindView(R.id.recycler_next_matches) RecyclerView recyclerView;

  private final CompositeSubscription subscription = new CompositeSubscription();
  private LeagueMatchesAdapter adapter;

  public static LeagueProgressFragment newInstance() {
    return new LeagueProgressFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_league_progress, container, false);
    ButterKnife.bind(this, view);

    LinearLayoutManager layout = new LinearLayoutManager(
        getActivity(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layout);
    recyclerView.setHasFixedSize(true);
    initAdapter();

    subscription.add(leagueDetails
        .roundsObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(rounds -> {
          int roundsLeft = leagueDetails.rounds().size();
          if (roundsLeft < 0) {
            showLeagueEndResults();
            return;
          }
          LeagueRound round = rounds.get(0);
          String title = getActivity().getString(
              R.string.next_round_n_of_n, round.roundNumber(), roundsLeft + round.roundNumber());
          adapter.setRound(round, title);
        }));

    return view;
  }

  private void showLeagueEndResults() {
    Club club = userPreferences.clubPreference().get();
    int position = leagueDetails.clubPosition(club);
    new AlertDialog.Builder(getContext())
        .setTitle("League ended")
        .setMessage(getString(R.string.you_finished_position, String.valueOf(position)))
        .setOnDismissListener(dialog -> getActivity().finish())
        .show();
  }

  private void initAdapter() {
    Club club = userPreferences.club();
    adapter = new LeagueMatchesAdapter(club);
    adapter.setHasStableIds(true);
    StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(adapter);
    recyclerView.addItemDecoration(decoration);
    recyclerView.setAdapter(adapter);
    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onChanged() {
        super.onChanged();
        decoration.invalidateHeaders();
      }
    });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    subscription.clear();
  }
}
