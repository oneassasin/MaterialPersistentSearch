package onea.android.com.materialpersistentsearch.library.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import onea.android.com.materialpersistentsearch.library.R;
import onea.android.com.materialpersistentsearch.library.models.SearchResult;
import onea.android.com.materialpersistentsearch.library.models.SearchViewHolder;
import onea.android.com.materialpersistentsearch.library.views.widgets.SearchView;

public final class RecyclerViewSearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

  private List<SearchResult> showedResultsList = new ArrayList<>();

  private SearchView.ViewHolderListener viewHolderListener;

  public RecyclerViewSearchAdapter(SearchView.ViewHolderListener viewHolderListener) {
    this.viewHolderListener = viewHolderListener;
  }

  public void updateResults(List<SearchResult> searchResults) {
    this.showedResultsList.clear();
    for (SearchResult searchResult : searchResults)
      this.showedResultsList.add(searchResult);
    notifyDataSetChanged();
  }

  @Override
  public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_single_item, parent, false);
    return new SearchViewHolder(view, viewHolderListener);
  }

  @Override
  public void onBindViewHolder(SearchViewHolder viewHolder, int position) {
    SearchResult searchResult = showedResultsList.get(position);
    viewHolder.setHash(searchResult.hashCode());
    viewHolder.setTitle(searchResult.title);
    viewHolder.setDrawable(searchResult.icon);
  }

  @Override
  public int getItemCount() {
    return this.showedResultsList.size();
  }

}