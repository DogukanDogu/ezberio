package com.dogukan.ezberio;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.dogukan.ezberio.db.FlashcardDatabase;
import com.dogukan.ezberio.model.Flashcard;
import java.util.ArrayList;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> cardSummaries;
    private List<Flashcard> cards = new ArrayList<>();
    private Button btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.listView);
        btnBackToMain = findViewById(R.id.btnBackMain);

        cardSummaries = new ArrayList<>();

        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cardSummaries);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < cards.size()) {
                Flashcard selected = cards.get(position);
                FlashcardDatabase db = FlashcardDatabase.getInstance(getApplicationContext());
                db.flashcardDao().deleteById(selected.id);
                cards.remove(position);
                cardSummaries.remove(position);
                adapter.notifyDataSetChanged();
                finish();
            }
            return true;
        });

        loadCards();
    }

    private void loadCards() {
        FlashcardDatabase db = FlashcardDatabase.getInstance(getApplicationContext());
        cards = db.flashcardDao().getAll();

        cardSummaries.clear();
        for (Flashcard card : cards) {
            String summary = "🧠 " + card.term + "\n" +
                    "Tanım : " + card.definition + "\n \n \n" +
                    "Görev : " + card.function + "\n \n \n " +
                    "Soru  : " + card.question + "\n \n \n" +
                    "Ezber : " + card.memoryTip;
            cardSummaries.add(summary);
        }
        adapter.notifyDataSetChanged();
    }


}