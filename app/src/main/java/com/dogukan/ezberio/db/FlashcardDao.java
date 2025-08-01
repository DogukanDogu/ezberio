package com.dogukan.ezberio.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dogukan.ezberio.model.Flashcard;

import java.util.List;

@Dao
public interface FlashcardDao {
    @Insert
    void insert(Flashcard card);

    @Query("SELECT * FROM flashcards ORDER BY id DESC")
    List<Flashcard> getAll();

    @Query("DELETE FROM flashcards WHERE id = :id")
    void deleteById(int id);
}

