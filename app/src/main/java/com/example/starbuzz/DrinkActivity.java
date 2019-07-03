package com.example.starbuzz;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkActivity extends AppCompatActivity {

    public static final String EXTRA_DRINK_ID = "drinkId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) return;

        Integer drinkId = (Integer) extras.get(EXTRA_DRINK_ID);
        if (drinkId == null) return;

        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try (SQLiteDatabase db = starbuzzDatabaseHelper.getReadableDatabase();
             Cursor cursor = db.query("DRINK",
                     new String[]{"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID", "FAVORITE"},
                     "_id = ?",
                     new String[]{Integer.toString(drinkId)},
                     null, null, null)) {

            if (cursor.moveToFirst()) {
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int photoId = cursor.getInt(2);
                boolean isFavorite = (cursor.getInt(3) == 1);

                TextView name = findViewById(R.id.name);
                name.setText(nameText);

                TextView description = findViewById(R.id.description);
                description.setText(descriptionText);

                ImageView photo = findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(descriptionText);

                CheckBox favorite = findViewById(R.id.favorite);
                favorite.setChecked(isFavorite);
            }
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onFavoriteClicked(View view) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) return;

        Integer drinkId = (Integer) extras.get(EXTRA_DRINK_ID);
        if (drinkId == null) return;

        CheckBox favorite = findViewById(R.id.favorite);
        ContentValues drinkValues = new ContentValues();
        drinkValues.put("FAVORITE", favorite.isChecked());

        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try (SQLiteDatabase db = starbuzzDatabaseHelper.getWritableDatabase()) {
            db.update("DRINK",
                    drinkValues,
                    "_id = ?",
                    new String[]{Integer.toString(drinkId)});
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
