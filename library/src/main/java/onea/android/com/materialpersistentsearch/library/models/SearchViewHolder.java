package onea.android.com.materialpersistentsearch.library.models;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import onea.android.com.materialpersistentsearch.library.R;
import onea.android.com.materialpersistentsearch.library.views.widgets.SearchView;

public final class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  private int hash;

  private TextView title;

  private ImageView logoImageView;

  private SearchView.ViewHolderListener recyclerViewListener;

  public SearchViewHolder(View itemView, SearchView.ViewHolderListener recyclerViewListener) {
    super(itemView);
    this.title = (TextView) itemView.findViewById(R.id.material_single_item_title);
    this.logoImageView = (ImageView) itemView.findViewById(R.id.material_single_item_icon);
    itemView.setOnClickListener(this);
    this.recyclerViewListener = recyclerViewListener;
  }

  public String getTitle() {
    return title.getText().toString();
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  public void setHash(int hash) {
    this.hash = hash;
  }

  public void setDrawable(Drawable drawable) {
    this.logoImageView.setImageDrawable(drawable);
  }

  @Override
  public void onClick(View v) {
    if (recyclerViewListener != null)
      recyclerViewListener.onItemClicked(this.hash);
  }

}
