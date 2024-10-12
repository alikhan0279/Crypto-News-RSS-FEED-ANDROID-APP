package com.example.MADPROJECT;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NewsDetailActivity extends AppCompatActivity {

    private static final String TAG = "NewsDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        String headline = getIntent().getStringExtra("headline");
        String pubDate = getIntent().getStringExtra("pubDate");
        String content = getIntent().getStringExtra("content");
        String link = getIntent().getStringExtra("link");

        TextView headlineTextView = findViewById(R.id.headline_text_view);
        TextView pubDateTextView = findViewById(R.id.pub_date_text_view);
        //TextView contentTextView = findViewById(R.id.content_text_view);
        LinearLayout articleBodyLayout = findViewById(R.id.article_body_layout);

        headlineTextView.setText(headline);
        pubDateTextView.setText(pubDate);

        // Parse and display the HTML content
        // contentTextView.setText(Html.fromHtml(content));
        //  contentTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Enable clickable links

        // Fetch and display the full article content
        new FetchArticleContentTask(articleBodyLayout).execute(link);
    }

    private static class FetchArticleContentTask extends AsyncTask<String, Void, Document> {
        private LinearLayout articleBodyLayout;

        FetchArticleContentTask(LinearLayout articleBodyLayout) {
            this.articleBodyLayout = articleBodyLayout;
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                return Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                Log.e(TAG, "Error fetching article content", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Document doc) {
            if (doc != null) {
                Elements elements = doc.select("p, img"); // Select paragraphs and images
                for (Element element : elements) {
                    if (element.tagName().equals("p")) {
                        String text = element.text();
                        if (!text.isEmpty()) {
                            TextView textView = new TextView(articleBodyLayout.getContext());
                            textView.setTextSize(16);
                            textView.setText(text);
                            articleBodyLayout.addView(textView);
                        }
                    } else if (element.tagName().equals("img")) {
                        String imgUrl = element.attr("src");
                        if (!imgUrl.isEmpty()) {
                            ImageView imageView = new ImageView(articleBodyLayout.getContext());
                            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            imageView.setAdjustViewBounds(true);
                            Glide.with(articleBodyLayout.getContext())
                                    .load(imgUrl)
                                    .into(imageView);
                            articleBodyLayout.addView(imageView);
                        }
                    }
                }
            } else {
                TextView errorTextView = new TextView(articleBodyLayout.getContext());
                errorTextView.setText(R.string.error_loading_content);
                articleBodyLayout.addView(errorTextView);
            }
        }
    }
}