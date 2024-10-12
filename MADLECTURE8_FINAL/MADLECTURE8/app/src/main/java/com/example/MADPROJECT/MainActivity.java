package com.example.MADPROJECT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG= "MainActivity";
    private static final String[] RSS_FEED_URLS = {
           // "https://www.cryptonewsz.com/feed/",
           // "https://decrypt.co/feed",
           // "https://cryptoslate.com/feed",
           // "https://www.crypto-news.net/feed/",
           // "https://www.globalcryptopress.com/feeds/posts/default?alt=rss",
          //  "https://insidebitcoins.com/feed",
           // "https://www.thecoinspost.com/feed/",


            "https://www.globalcryptopress.com/feeds/posts/default?alt=rss",
            "https://www.cryptonewsz.com/feed/",
            "https://blockonomi.com/feed/",
            "https://cryptobriefing.com/feed/",
            "https://coinjournal.net/news/feed/",
            "https://bitcoinist.com/feed/",
            "https://cryptoslate.com/feed",
            "https://www.crypto-news.net/feed/",
            "https://insidebitcoins.com/feed",
            "https://coinsutra.com/feed/",
            "https://www.newsbtc.com/feed",
    };


    private List<NewsItem> originalNewsItems;
    private List<NewsItem> newsItems;
    private NewsAdapter adapter;
    private EditText searchEditText;
    private NewsDatabaseHelper dbHelper;
    private Button save_button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originalNewsItems = new ArrayList<>();
        Log.d(TAG, "onCreate");

        newsItems = new ArrayList<>();
        adapter = new NewsAdapter(this, newsItems);




        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        dbHelper = new NewsDatabaseHelper(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem newsItem = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                intent.putExtra("headline", newsItem.getHeadline());
                intent.putExtra("pubDate", newsItem.getPubDate());
                intent.putExtra("content", newsItem.getContent());
                intent.putExtra("link", newsItem.getLink()); // Pass the link to the full article
                startActivity(intent);
            }
        });





        Spinner spinner = findViewById(R.id.website_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.website_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new SearchTextWatcher());

        //button
        save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHeadlines();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new FetchNewsTask().execute(RSS_FEED_URLS[position]);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Spinner filterSpinner = findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle filter selection here
                String selectedFilter = getResources().getStringArray(R.array.filter_array)[position];
                filterNews(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




    }

    private void saveHeadlines() {
        for (NewsItem item : newsItems) {
            dbHelper.insertNews(item.getHeadline(), item.getLink());
        }
        Toast.makeText(this, "Headlines saved to database", Toast.LENGTH_SHORT).show();
    }



    private void filterNews(String selectedFilter) {
        switch (selectedFilter) {
            case "New First":
                // Sort news items by publication date (newest first)
                Collections.sort(newsItems, new Comparator<NewsItem>() {
                    @Override
                    public int compare(NewsItem item1, NewsItem item2) {
                        // Parse publication dates and compare
                        return comparePubDates(item2.getPubDate(), item1.getPubDate());
                    }
                });
                break;
            case "Old First":
                // Sort news items by publication date (oldest first)
                Collections.sort(newsItems, new Comparator<NewsItem>() {
                    @Override
                    public int compare(NewsItem item1, NewsItem item2) {
                        // Parse publication dates and compare
                        return comparePubDates(item1.getPubDate(), item2.getPubDate());
                    }
                });

                break;
            // Add more cases for other filter options
            // Handle cases like "Last 1 Hour", "Last 2 Hours", etc.
            default:
                // If no specific filter selected, do nothing
                break;
        }
        // Update the adapter with the filtered news items
        adapter.notifyDataSetChanged();
    }
    private int comparePubDates(String pubDate1, String pubDate2) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        try {
            Date date1 = format.parse(pubDate1);
            Date date2 = format.parse(pubDate2);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Default comparison result
        }
    }


    private class FetchNewsTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            if (!isNetworkConnected()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
            String urlStr = urls[0];
            newsItems.clear();
            try {
                URL url = new URL(urlStr);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");
                boolean insideItem = false;
                String title = null, link = null, pubDate = null, content = null;
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) title = xpp.nextText();
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) link = xpp.nextText();
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) pubDate = xpp.nextText();
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) content = xpp.nextText(); // Fetching content
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                        if (title != null && link != null && pubDate != null && content != null) {
                            newsItems.add(new NewsItem(title, link, pubDate, content));
                            title = null;
                            link = null;
                            pubDate = null;
                            content = null;
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            // After fetching news items, update the original list
            originalNewsItems.clear();
            originalNewsItems.addAll(newsItems);
            adapter.notifyDataSetChanged();
        }

        private InputStream getInputStream(URL url) {
            try {
                return url.openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String searchText = s.toString().toLowerCase(); // Convert search text to lowercase for case-insensitive search
            if (searchText.isEmpty()) {
                // If search text is empty, update the adapter with the original list
                adapter.updateList(originalNewsItems);
            } else {
                // If search text is not empty, filter the news items based on the search text
                List<NewsItem> filteredItems = new ArrayList<>();
                for (NewsItem item : originalNewsItems) {
                    if (item.getHeadline().toLowerCase().contains(searchText)) {
                        filteredItems.add(item);
                    }
                }
                adapter.filterList(filteredItems);
            }
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkReceiver();
    }

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private void unregisterNetworkReceiver() {
        unregisterReceiver(networkReceiver);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isNetworkAvailable()) {
                // Start OfflineActivity if network is not available
                Intent offlineIntent = new Intent(MainActivity.this, OfflineActivity.class);
                startActivity(offlineIntent);
            }
        }
    };

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
