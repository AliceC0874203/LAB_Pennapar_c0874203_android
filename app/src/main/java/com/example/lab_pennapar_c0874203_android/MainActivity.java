package com.example.lab_pennapar_c0874203_android;

import static com.example.lab_pennapar_c0874203_android.FavoritePlace.KEY_NAME;
import static com.example.lab_pennapar_c0874203_android.FavoritePlace.SHARED_PREFERENCES_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.lab_pennapar_c0874203_android.databinding.ActivityMainBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private List<FavoritePlace> favoritePlaceList;

    // instance of shared preferences
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // instantiate shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        favoritePlaceList = new ArrayList<>();

        binding.addNewPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        binding.listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra("favoritePlaceSelected",favoritePlaceList.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSavedFavoriteLocations();
        displayFavoriteList();
    }

    private void getSavedFavoriteLocations() {
        String receivedSerializedString = sharedPreferences.getString(KEY_NAME, null);
        try {
            favoritePlaceList = (ArrayList<FavoritePlace>) ObjectSerializer.deserialize(receivedSerializedString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayFavoriteList() {
        if (favoritePlaceList != null) {
            ArrayAdapter<FavoritePlace> itemsAdapter = new ArrayAdapter<FavoritePlace>(MainActivity.this, android.R.layout.simple_list_item_1, favoritePlaceList);
            binding.listItem.setAdapter(itemsAdapter);
        }
    }
}