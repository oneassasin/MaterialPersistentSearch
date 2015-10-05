package onea.android.com.materialpersistentsearch.library.models;

import android.graphics.drawable.Drawable;

public final class SearchResult {

  public final String title;

  public final Drawable icon;

  public final Object object;

  private final int hash;

  /**
   * Create a search result with text and an icon
   *
   * @param title Title of element
   * @param icon  Icon of element
   */
  public SearchResult(String title, Drawable icon) {
    this(title, icon, null);
  }

  /**
   * Create a search result with text and an icon
   *
   * @param title  Title of element
   * @param icon   Icon of element
   * @param object User defined model object
   */
  public SearchResult(String title, Drawable icon, Object object) {
    this.title = title;
    this.icon = icon;
    this.object = object;
    int hash = (title.hashCode() << 15) ^ 0xFFFFCD7D;
    hash ^= icon.hashCode() << 15;
    hash ^= (hash >>> 10);
    hash += (hash << 3);
    hash ^= (hash >>> 16);
    hash += (hash << 2) + (hash << 14);
    this.hash = hash;
  }

  /**
   * Return the title of the result
   */
  @Override
  public String toString() {
    return title;
  }

  /**
   * Return a hash code of the search result
   */
  @Override
  public int hashCode() {
    return hash;
  }

}
