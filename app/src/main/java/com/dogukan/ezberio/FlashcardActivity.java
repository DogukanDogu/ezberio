package com.dogukan.ezberio;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.dogukan.ezberio.db.FlashcardDatabase;
import com.dogukan.ezberio.model.Flashcard;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            SpannableStringBuilder questionBuilder = new SpannableStringBuilder();
            String cleanedQuestion = card.question.replace("\\n", "\n");

            if (card.question != null && !card.question.isEmpty()) {
                String[] questionParts = card.question.trim().split("\n");

                if (questionParts.length > 0) {
                    questionBuilder.append("📌 Soru: ").append(questionParts[0]).append("\n\n");
                }

                for (int i = 1; i < questionParts.length; i++) {
                    SpannableString option = new SpannableString("🔹 " + questionParts[i] + "\n");
                    option.setSpan(new BulletSpan(20), 0, option.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    questionBuilder.append(option);
                }
            }

            questionBuilder = formatQuestionWithOptions(cleanedQuestion);

            String summary = "🧠 " + card.term + "\n" +
                    "Tanım : " + card.definition + "\n\n\n" +
                    "Görev : " + card.function + "\n\n\n" +
                    "Ezber : " + card.memoryTip + "\n\n\n";

            SpannableStringBuilder fullBuilder = new SpannableStringBuilder();
            fullBuilder.append(summary);
            fullBuilder.append(questionBuilder);

            cardSummaries.add(fullBuilder.toString());
        }

        adapter.notifyDataSetChanged();
    }


    private SpannableStringBuilder formatQuestionWithOptions(String rawQuestion) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (rawQuestion == null || rawQuestion.trim().isEmpty()) {
            return builder;
        }

        Pattern optionPattern = Pattern.compile("(?=[A-D]\\))");
        String[] parts = optionPattern.split(rawQuestion.trim());

        builder.append("📌 Soru: ").append(parts[0].trim()).append("\n\n");

        Matcher matcher = optionPattern.matcher(rawQuestion);
        int index = 1;
        while (matcher.find() && index < parts.length) {
            String label = matcher.group();
            String option = parts[index].trim();
            SpannableString spannableOption = new SpannableString("🔹 " + label + " " + option + "\n");
            spannableOption.setSpan(new BulletSpan(20), 0, spannableOption.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(spannableOption);
            index++;
        }

        return builder;
    }


}