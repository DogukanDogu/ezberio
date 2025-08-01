package com.dogukan.ezberio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddCard = findViewById(R.id.btnAddCard);
        Button btnViewCards = findViewById(R.id.btnViewCards);

        btnAddCard.setOnClickListener(v -> startActivity(new Intent(this, AddCardActivity.class)));
        btnViewCards.setOnClickListener(v -> startActivity(new Intent(this, FlashcardActivity.class)));
    }
}