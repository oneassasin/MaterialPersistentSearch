package onea.android.com.materialpersistentsearch.library.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;

import java.util.List;

public final class VoiceRecognitionUtils {

  private VoiceRecognitionUtils() {
  }

  public static boolean isActionRecognizeRecognize(Context context) {
    final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    final PackageManager mgr = context.getPackageManager();
    if (mgr != null) {
      List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
      return list.size() > 0;
    }
    return false;
  }

}
