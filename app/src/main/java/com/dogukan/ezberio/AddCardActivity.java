package com.dogukan.ezberio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.dogukan.ezberio.api.GeminiApiService;
import com.dogukan.ezberio.api.GeminiClient;
import com.dogukan.ezberio.api.GeminiRequest;
import com.dogukan.ezberio.api.GeminiResponse;
import com.dogukan.ezberio.db.FlashcardDatabase;
import com.dogukan.ezberio.db.FlashcardStorage;
import com.dogukan.ezberio.model.Flashcard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCardActivity extends AppCompatActivity {
    private EditText inputTerm;
    private Button btnGenerate;
    private Button btnBackToMain;
    private static final String API_KEY = "AIzaSyC-ViQ6ixGnxg-5nR-XF5Jcu7qBThPk6KE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        inputTerm = findViewById(R.id.inputTerm);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBackToMain = findViewById(R.id.btnBackMain);

        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnGenerate.setOnClickListener(v -> {
            String term = inputTerm.getText().toString().trim();
            if (!term.isEmpty()) {
                String prompt = "Kavram: " + term + "\n\n" +
                        "Aşağıdaki yapıya uygun şekilde JSON yanıt ver:\n\n" +
                        "{\n" +
                        "  \"definition\": \"...\",\n" +
                        "  \"function\": \"...\",\n" +
                        "  \"question\": \"...\",\n" +
                        "  \"memoryTip\": \"...\"\n" +
                        "}\n\n" +
                        "Verilecek bilgiler: \n" +
                        "1. Bu kavramı sade ve açık şekilde tanımla (definition).\n" +
                        "2. Temel işlevini açıkla (function).\n" +
                        "3. 1 test sorusu üret (question).\n" +
                        "4. Hatırlatıcı ipucu ver (memoryTip).";
                fetchFromGemini(prompt);
            }
        });
    }

    public String extractField(String input, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private void fetchFromGemini(String prompt) {
        GeminiRequest request = new GeminiRequest(prompt);
        GeminiApiService service = GeminiClient.getService();

        service.generateContent(API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String text = response.body().candidates.get(0).content.parts.get(0).text;
                    Flashcard flashcard = new Flashcard();

                    String definitionPattern = "\"definition\":\\s*\"(.*?)\"";
                    String functionPattern = "\"function\":\\s*\"(.*?)\"";
                    String questionPattern = "\"question\":\\s*\"(.*?)\"";
                    String memoryTipPattern = "\"memoryTip\":\\s*\"(.*?)\"";

                    flashcard.term = inputTerm.getText().toString();
                    flashcard.definition = extractField(text, definitionPattern);
                    flashcard.function = extractField(text, functionPattern);
                    flashcard.question = extractField(text, questionPattern);
                    flashcard.memoryTip = extractField(text, memoryTipPattern);

                    FlashcardStorage.addCard(flashcard);
                    FlashcardDatabase db = FlashcardDatabase.getInstance(getApplicationContext());
                    db.flashcardDao().insert(flashcard);
                    Toast.makeText(AddCardActivity.this, "Kart oluşturuldu!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddCardActivity.this, FlashcardActivity.class));
                } else {
                    try {
                        Log.e("GeminiAPI", "Hata kodu: " + response.code());
                        Log.e("GeminiAPI", "Hata mesajı: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(AddCardActivity.this, "Gemini başarısız", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Toast.makeText(AddCardActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("GeminiAPI", "İstek hatası", t);
            }
        });
    }
}