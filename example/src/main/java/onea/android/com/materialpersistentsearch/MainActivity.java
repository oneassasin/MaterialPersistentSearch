package onea.android.com.materialpersistentsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public final class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupFragment();
  }

  private void setupFragment() {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.activity_main_frame_layout, new MainFragment())
        .commit();
  }

}
