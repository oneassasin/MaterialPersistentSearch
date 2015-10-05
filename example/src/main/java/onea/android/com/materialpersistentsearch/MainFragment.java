package onea.android.com.materialpersistentsearch;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import onea.android.com.materialpersistentsearch.library.models.SearchResult;
import onea.android.com.materialpersistentsearch.library.views.widgets.SearchView;

public final class MainFragment extends Fragment {

  private SearchView searchView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(final View view, Bundle savedInstanceState) {
    searchView = (SearchView) view.findViewById(R.id.fragment_main_search_view);
    searchView.setMenuListener(new SearchView.MenuListener() {
      @Override
      public void onMenuClick() {
        Snackbar.make(view, "MenuClicked", Snackbar.LENGTH_LONG)
            .show();
      }
    });
    searchView.setBackButtonListener(new SearchView.BackButtonListener() {
      @Override
      public void onBackButtonListener() {
        Snackbar.make(view, "BackButtonClicked", Snackbar.LENGTH_LONG)
            .show();
      }
    });
    searchView.setSearchActionsListener(new SearchView.SearchActionsListener() {
      @Override
      public void onSearchOpened() {
        Snackbar.make(view, "SearchOpened", Snackbar.LENGTH_LONG)
            .show();
      }

      @Override
      public void onSearchClosed() {
        Snackbar.make(view, "SearchClosed", Snackbar.LENGTH_LONG)
            .show();
      }

      @Override
      public void onSearchChanged(String string) {
        Snackbar.make(view, "SearchChanged: " + string, Snackbar.LENGTH_LONG)
            .show();
      }

      @Override
      public void onSearchClear() {
        Snackbar.make(view, "SearchClear", Snackbar.LENGTH_LONG)
            .show();
      }
    });
    searchView.setSearchListener(new SearchView.SearchListener() {
      @Override
      public void onSearch(String searchable) {
        Snackbar.make(view, "Search: " + searchable, Snackbar.LENGTH_LONG)
            .show();
        searchView.showProgressBar();
        final ArrayList<SearchResult> searchResults = new ArrayList<>();
        Drawable drawable = getResources().getDrawable(R.drawable.ic_up);
        for (int i = 0; i < 5; ++i)
          searchResults.add(new SearchResult("SearchResult " + i + "" + i, drawable, i));
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            searchView.addSearchable(searchResults);
            searchView.hideProgressBar();
          }
        }, 3000);
      }

      @Override
      public void onSearchResultSelected(SearchResult searchResult) {
        Integer integer = (Integer) searchResult.object;
        Snackbar.make(view, "Search: " + searchResult.title + " " + integer.toString(), Snackbar.LENGTH_LONG)
            .show();
      }
    });
    searchView.setPopupMenu(R.menu.menu_fragment_main);
    searchView.setPopupMenuListener(new PopupMenu.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
          case R.id.action_main: {
            Snackbar.make(view, "PopupMenuClicked", Snackbar.LENGTH_LONG)
                .show();
            return true;
          }
        }
        return false;
      }
    });
    searchView.enableVoiceRecognition(this);
    searchView.setVoiceRecognitionListener(new SearchView.VoiceRecognitionListener() {
      @Override
      public void onMICButtonClick() {
        Snackbar.make(view, "VoiceRecognitionClicked", Snackbar.LENGTH_LONG)
            .show();
      }
    });
    searchView.setSearchHint(R.string.search_view_hint);
  }

}
