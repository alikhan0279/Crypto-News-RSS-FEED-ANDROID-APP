package com.example.MADPROJECT;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private List<NewsItem> originalItems;
    private List<NewsItem> filteredItems;
    private ItemFilter itemFilter = new ItemFilter();

    public NewsAdapter(Context context, List<NewsItem> newsItems) {
        super(context, 0, newsItems);
        this.originalItems = newsItems;
        this.filteredItems = newsItems;
    }
    public void updateList(List<NewsItem> updatedItems) {
        clear();
        addAll(updatedItems);
        notifyDataSetChanged();
    }

    public void filterList(List<NewsItem> filteredItems) {
        clear();
        addAll(filteredItems);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        NewsItem newsItem = getItem(position);

        TextView headlineTextView = convertView.findViewById(R.id.headline_text_view);
        TextView dateTextView = convertView.findViewById(R.id.date_text_view);

        headlineTextView.setText(newsItem.getHeadline());
        dateTextView.setText(formatPubDate(newsItem.getPubDate()));

        return convertView;
    }

    private String formatPubDate(String pubDate) {
        try {
            Date date = dateFormat.parse(pubDate);
            return DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return pubDate;
        }
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<NewsItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalItems);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (NewsItem item : originalItems) {
                    if (item.getHeadline().toLowerCase().contains(filterPattern) ||
                            item.getPubDate().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (List<NewsItem>) results.values;
            clear();
            addAll(filteredItems);
            notifyDataSetChanged();
        }
    }
}
