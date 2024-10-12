package com.example.MADPROJECT;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class OfflineActivity extends AppCompatActivity {

    private NewsDatabaseHelper dbHelper;
    private ListView headlinesListView;
    private boolean isConnected = false;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new NewsDatabaseHelper(this);
        headlinesListView = findViewById(R.id.headlines_list_view);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                isConnected = true;
            }

            @Override
            public void onLost(Network network) {
                isConnected = false;
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);

        displayHeadlines();

        headlinesListView.setOnItemClickListener((parent, view, position, id) -> {
            if (isConnected) {
                // Open NewsDetailActivity
                Intent intent = new Intent(OfflineActivity.this, NewsDetailActivity.class);
                intent.putExtra("headline", (String) parent.getItemAtPosition(position));
                startActivity(intent);
            } else {
                // Show Toast message
                Toast.makeText(OfflineActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayHeadlines() {
        Cursor cursor = dbHelper.getRecentHeadlines();
        List<String> headlines = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String headline = cursor.getString(cursor.getColumnIndex("headline"));
                headlines.add(headline);
            } while (cursor.moveToNext());
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, headlines);

        headlinesListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
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
            if (isNetworkAvailable()) {
                // Start OfflineActivity if network is not available
                Intent offlineIntent = new Intent(OfflineActivity.this, MainActivity.class);
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
