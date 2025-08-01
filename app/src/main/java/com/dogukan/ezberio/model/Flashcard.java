package com.dogukan.ezberio.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "flashcards")

public class Flashcard {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String term;
    public String definition;
    public String function;
    public String question;
    public String memoryTip;
    public boolean isFavorite;
}
