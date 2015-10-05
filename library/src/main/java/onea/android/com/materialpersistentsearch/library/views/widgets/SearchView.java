package onea.android.com.materialpersistentsearch.library.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import onea.android.com.materialpersistentsearch.library.R;
import onea.android.com.materialpersistentsearch.library.utils.KeyboardUtils;
import onea.android.com.materialpersistentsearch.library.utils.LogoTypeEnum;
import onea.android.com.materialpersistentsearch.library.utils.MenuTypeEnum;
import onea.android.com.materialpersistentsearch.library.models.SearchResult;
import onea.android.com.materialpersistentsearch.library.utils.VoiceRecognitionUtils;
import onea.android.com.materialpersistentsearch.library.views.adapters.RecyclerViewSearchAdapter;

public final class SearchView extends FrameLayout {

  public static final int VOICE_RECOGNITION_CODE = 60456;

  private Context context;
  private RecyclerViewSearchAdapter recyclerViewSearchAdapter;

  private List<SearchResult> searchResultList = new ArrayList<>();

  private EditText searchEditText;
  private TextView logoTextView;
  private ImageView logoImageView;
  private MaterialMenuView materialMenuView;
  private RecyclerView recyclerView;
  private ProgressBar progressBar;
  private ImageView micImageView;
  private ImageView clearImageView;
  private ImageView popupImageView;
  private LogoTypeEnum logoTypeEnum = null;
  private MenuTypeEnum menuTypeEnum = null;

  private long lastBackButtonClickTime;

  private OnClickListener logoOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      hideLogo();
      showSearchEditText();
      showKeyboard();
      setArrowState();
      if (searchEditText.length() > 0) {
        showClearImageView();
        hideVoiceRecognition();
        updateResults();
      }
    }
  };
  private OnClickListener menuOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      if (isSearchOpen) {
        hideResultsList();
        hideSearchEditText();
        hideKeyboard();
        hideClearImageView();
        showVoiceRecognition();
        showLogo();
        setupMenuIcon();
        return;
      }
      if (menuTypeEnum == MenuTypeEnum.BACK) {
        if (backButtonListener != null)
          backButtonListener.onBackButtonListener();
      } else {
        if (menuListener != null)
          menuListener.onMenuClick();
      }
    }
  };
  private OnClickListener micOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      if (isVoiceRecognitionIntentSupported) {
        startVoiceRecognition();
        if (voiceRecognitionListener != null)
          voiceRecognitionListener.onMICButtonClick();
      }
    }
  };
  private OnClickListener popupMenuOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      popupMenu.show();
    }
  };
  private OnClickListener clearOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      searchEditText.setText("");
      hideClearImageView();
      hideResultsList();
      searchResultList.clear();
      if (searchActionsListener != null)
        searchActionsListener.onSearchClear();
    }
  };
  private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        if (searchListener != null && !searchEditText.getText().toString().equals("")) {
          searchListener.onSearch(searchEditText.getText().toString());
          if (KeyboardUtils.isKeyboardShowing(SearchView.this))
            hideKeyboard();
          return true;
        }
      }
      return false;
    }
  };
  private TextWatcher textWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
      if (s.length() > 0) {
        showClearImageView();
        hideVoiceRecognition();
        updateResults();
      } else {
        hideClearImageView();
        showVoiceRecognition();
      }
      if (searchActionsListener != null)
        searchActionsListener.onSearchChanged(s.toString());
    }
  };
  private ViewHolderListener viewHolderListener = new ViewHolderListener() {
    @Override
    public void onItemClicked(int hashCode) {
      if (searchListener == null)
        return;
      for (SearchResult searchResult : searchResultList) {
        if (searchResult.hashCode() == hashCode) {
          searchEditText.setText(searchResult.title);
          searchEditText.clearFocus();
          hideKeyboard();
          hideResultsList();
          searchListener.onSearchResultSelected(searchResult);
          break;
        }
      }
    }
  };
  private boolean isSearchOpen = false;
  private boolean isAnimate = false;
  private boolean isVoiceRecognitionIntentSupported = false;
  private PopupMenu popupMenu;

  private BackButtonListener backButtonListener;
  private VoiceRecognitionListener voiceRecognitionListener;
  private MenuListener menuListener;
  private SearchListener searchListener;
  private SearchActionsListener searchActionsListener;
  private WeakReference<Object> container;

  public SearchView(Context context) {
    this(context, null);
  }

  public SearchView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    init();
  }

  private void init() {
    inflate(context, R.layout.search_view, this);
    this.isVoiceRecognitionIntentSupported = VoiceRecognitionUtils.isActionRecognizeRecognize(context);
    this.recyclerViewSearchAdapter = new RecyclerViewSearchAdapter(viewHolderListener);
    setupViews();
    setupLogo();
    setupListeners();
    this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    this.recyclerView.setAdapter(recyclerViewSearchAdapter);
  }

  private void setupViews() {
    this.searchEditText = (EditText) findViewById(R.id.search_view_search_edit_text);
    this.logoTextView = (TextView) findViewById(R.id.search_view_logo_text_view);
    this.logoImageView = (ImageView) findViewById(R.id.search_view_logo_image_view);
    this.materialMenuView = (MaterialMenuView) findViewById(R.id.search_view_material_menu_view);
    this.recyclerView = (RecyclerView) findViewById(R.id.search_view_recycler_view);
    this.progressBar = (ProgressBar) findViewById(R.id.search_view_progress_bar);
    this.micImageView = (ImageView) findViewById(R.id.search_view_mic_image_view);
    this.clearImageView = (ImageView) findViewById(R.id.search_view_clear_image_view);
    this.popupImageView = (ImageView) findViewById(R.id.search_view_popup_menu);
  }

  private void setupLogo() {
    if (logoImageView.getVisibility() == GONE && logoTextView.getVisibility() == GONE)
      throw new IllegalArgumentException("SearchView does not display logo!");
    if (logoTextView.getVisibility() == VISIBLE) {
      logoTypeEnum = LogoTypeEnum.TEXT_VIEW;
      logoTextView.setOnClickListener(logoOnClickListener);
    }
    if (logoImageView.getVisibility() == VISIBLE) {
      if (logoTypeEnum != null)
        throw new IllegalArgumentException("SearchView already have displayed logo!");
      logoTypeEnum = LogoTypeEnum.IMAGE_VIEW;
      logoImageView.setOnClickListener(logoOnClickListener);
    }
  }

  private void setupListeners() {
    this.searchEditText.addTextChangedListener(textWatcher);
    this.searchEditText.setOnEditorActionListener(onEditorActionListener);
    this.materialMenuView.setOnClickListener(menuOnClickListener);
    if (this.micImageView.getVisibility() == VISIBLE)
      this.micImageView.setOnClickListener(micOnClickListener);
    if (this.popupImageView.getVisibility() == VISIBLE)
      this.popupImageView.setOnClickListener(popupMenuOnClickListener);
    this.clearImageView.setOnClickListener(clearOnClickListener);
  }

  private void setupMenuIcon() {
    if (menuTypeEnum == MenuTypeEnum.BURGER) {
      setArrowState();
      setBurgerState();
    } else {
      setBurgerState();
      setArrowState();
    }
  }

  private void startVoiceRecognition() {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speak_now));
    Object object = container.get();
    if (object == null)
      return;
    if (object instanceof Activity) {
      Activity activity = (Activity) object;
      activity.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
    } else if (object instanceof Fragment) {
      Fragment fragment = (Fragment) object;
      fragment.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
    } else if (object instanceof android.app.Fragment
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      android.app.Fragment fragment = (android.app.Fragment) object;
      fragment.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
    }
  }

  private void showLogo() {
    switch (this.logoTypeEnum) {
      case TEXT_VIEW: {
        logoTextView.setVisibility(VISIBLE);
        break;
      }
      case IMAGE_VIEW: {
        logoImageView.setVisibility(VISIBLE);
        break;
      }
    }
  }

  private void hideLogo() {
    switch (this.logoTypeEnum) {
      case TEXT_VIEW: {
        logoTextView.setVisibility(GONE);
        break;
      }
      case IMAGE_VIEW: {
        logoImageView.setVisibility(GONE);
        break;
      }
    }
  }

  private void showSearchEditText() {
    searchEditText.setVisibility(VISIBLE);
    searchEditText.requestFocus();
    this.isSearchOpen = true;
    if (searchActionsListener != null)
      searchActionsListener.onSearchOpened();
  }

  private void hideSearchEditText() {
    searchEditText.setVisibility(GONE);
    this.isSearchOpen = false;
    if (searchActionsListener != null)
      searchActionsListener.onSearchClosed();
  }

  private void showKeyboard() {
    InputMethodManager imm = (InputMethodManager) getContext()
        .getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.showSoftInput(searchEditText, 0);
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getContext()
        .getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getRootView().getWindowToken(), 0);
  }

  private void showClearImageView() {
    if (clearImageView.getVisibility() == GONE)
      clearImageView.setVisibility(VISIBLE);
  }

  private void hideClearImageView() {
    if (clearImageView.getVisibility() == VISIBLE)
      clearImageView.setVisibility(GONE);
  }

  private void showResultsList() {
    if (recyclerView.getVisibility() == GONE)
      recyclerView.setVisibility(VISIBLE);
  }

  private void hideResultsList() {
    if (recyclerView.getVisibility() == VISIBLE)
      recyclerView.setVisibility(GONE);
  }

  private void setArrowState() {
    this.materialMenuView.animateState(MaterialMenuDrawable.IconState.ARROW);
  }

  private void setBurgerState() {
    this.materialMenuView.animateState(MaterialMenuDrawable.IconState.BURGER);
  }

  private void showVoiceRecognition() {
    if (this.micImageView.getVisibility() == GONE && isVoiceRecognitionIntentSupported)
      this.micImageView.setVisibility(VISIBLE);
  }

  private void hideVoiceRecognition() {
    if (this.micImageView.getVisibility() == VISIBLE)
      this.micImageView.setVisibility(GONE);
  }

  private void updateResults() {
    String searchable = searchEditText.getText().toString();
    List<SearchResult> filteringResults = new ArrayList<>();
    for (SearchResult searchResult : searchResultList) {
      if (filteringResults.size() == 5)
        break;
      if (searchResult.title.toLowerCase().startsWith(searchable.toLowerCase()))
        filteringResults.add(searchResult);
    }
    if (filteringResults.size() == 0)
      hideResultsList();
    else
      showResultsList();
    recyclerViewSearchAdapter.updateResults(filteringResults);
  }

  public void showProgressBar() {
    if (this.progressBar.getVisibility() != VISIBLE)
      this.progressBar.setVisibility(VISIBLE);
  }

  public void hideProgressBar() {
    if (this.progressBar.getVisibility() != GONE)
      this.progressBar.setVisibility(GONE);
  }

  public void setBackButtonListener(BackButtonListener backButtonListener) {
    this.backButtonListener = backButtonListener;
    this.menuTypeEnum = MenuTypeEnum.BACK;
    setupMenuIcon();
  }

  public void setMenuListener(MenuListener menuListener) {
    this.menuListener = menuListener;
    this.menuTypeEnum = MenuTypeEnum.BURGER;
    setupMenuIcon();
  }

  public void setVoiceRecognitionListener(VoiceRecognitionListener voiceRecognitionListener) {
    this.voiceRecognitionListener = voiceRecognitionListener;
  }

  public void setSearchListener(SearchListener searchListener) {
    this.searchListener = searchListener;
  }

  public void setSearchActionsListener(SearchActionsListener searchActionsListener) {
    this.searchActionsListener = searchActionsListener;
  }

  public void addSearchable(List<SearchResult> searchableList) {
    searchResultList.clear();
    for (SearchResult searchResult : searchableList)
      searchResultList.add(searchResult);
    if (searchableList.size() != 0) {
      showResultsList();
      updateResults();
    }
  }

  public void setSearchHint(int stringResID) {
    if (stringResID <= 0) {
      searchEditText.setHint("");
      return;
    }
    Resources resources = context.getResources();
    String string = resources.getString(stringResID);
    setSearchHint(string);
  }

  public void setSearchHint(String string) {
    if (string == null || string.equals("")) {
      searchEditText.setHint("");
      return;
    }
    searchEditText.setHint(string);
  }

  public void setPopupMenu(int popupMenuRes) {
    if (popupMenuRes <= 0) {
      popupImageView.setVisibility(GONE);
      popupMenu = null;
      setupListeners();
      return;
    }
    popupImageView.setVisibility(VISIBLE);
    popupMenu = new PopupMenu(context, popupImageView);
    popupMenu.getMenuInflater().inflate(popupMenuRes, popupMenu.getMenu());
    setupListeners();
  }

  public void setPopupMenuListener(PopupMenu.OnMenuItemClickListener onMenuItemClickListener) {
    if (this.popupMenu != null)
      this.popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
  }

  public void enableVoiceRecognition(Object activity) {
    if (activity == null) {
      container = new WeakReference<>(null);
      hideVoiceRecognition();
      setupListeners();
      return;
    }
    container = new WeakReference<>(activity);
    if (isVoiceRecognitionIntentSupported)
      showVoiceRecognition();
    setupListeners();
  }

  public boolean isSearchOpen() {
    return isSearchOpen;
  }

  @Override
  public boolean dispatchKeyEventPreIme(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && SystemClock.elapsedRealtime() - lastBackButtonClickTime > 300) {
      lastBackButtonClickTime = SystemClock.elapsedRealtime();
      if (menuTypeEnum == MenuTypeEnum.BACK && !isSearchOpen && backButtonListener != null)
        backButtonListener.onBackButtonListener();
      if (KeyboardUtils.isKeyboardShowing(this)) {
        hideKeyboard();
        return true;
      }
      if (isSearchOpen) {
        hideResultsList();
        hideSearchEditText();
        hideClearImageView();
        showVoiceRecognition();
        showLogo();
        setupMenuIcon();
        return true;
      }
    }
    return super.dispatchKeyEventPreIme(event);
  }

  public interface BackButtonListener {
    /**
     * Called when MaterialMenuView in ARROW state and SearchEditText in GONE state clicked
     */
    void onBackButtonListener();
  }

  public interface VoiceRecognitionListener {
    /**
     * Called when MICImageView clicked
     */
    void onMICButtonClick();
  }

  public interface MenuListener {
    /**
     * Called when MaterialMenuView in BURGER state clicked
     */
    void onMenuClick();
  }

  public interface SearchListener {
    /**
     * Called when Search action mode send
     *
     * @param searchable text for searching
     */
    void onSearch(String searchable);

    /**
     * Called when user click on some SearchResult from results list
     *
     * @param searchResult clicked element
     */
    void onSearchResultSelected(SearchResult searchResult);
  }

  public interface SearchActionsListener {
    /**
     * Called when SearchEditText showed
     */
    void onSearchOpened();

    /**
     * Called when SearchEditText hided
     */
    void onSearchClosed();

    /**
     * Called when text SearchEditText changed
     *
     * @param string changed string
     */
    void onSearchChanged(String string);

    /**
     * Called when ClearImageView clicked
     */
    void onSearchClear();
  }

  public interface ViewHolderListener {
    /**
     * Called when SearchViewHolder clicked
     *
     * @param hashCode hash code of clicked item
     */
    void onItemClicked(int hashCode);
  }

}
