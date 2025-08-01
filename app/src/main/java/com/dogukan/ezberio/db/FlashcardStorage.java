package com.dogukan.ezberio.db;
import com.dogukan.ezberio.model.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class FlashcardStorage {
    private static final List<Flashcard> cards = new ArrayList<>();

    public static void addCard(Flashcard card) {
        cards.add(card);
    }

    public static List<Flashcard> getCards() {
        return cards;
    }
}
