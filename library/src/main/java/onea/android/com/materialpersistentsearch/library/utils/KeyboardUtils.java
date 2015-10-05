package onea.android.com.materialpersistentsearch.library.utils;

import android.view.View;

public final class KeyboardUtils {

  private KeyboardUtils() {
  }

  public static boolean isKeyboardShowing(View view) {
    int heightDiff = view.getRootView().getHeight() - view.getHeight();
    return heightDiff > 100;
  }

}
