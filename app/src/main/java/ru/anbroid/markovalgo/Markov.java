package ru.anbroid.markovalgo;

import java.io.Serializable;

public class Markov implements Serializable
{
    private String sample;
    private String replacement;
    private String comment;

    public Markov(String sample, String replacement, String comment)
    {
        this.sample = sample;
        this.replacement = replacement;
        this.comment = comment;
    }

    public Markov(String sample, String replacement)
    {
        this.sample = sample;
        this.replacement = replacement;
        this.comment = "";
    }

    public Markov(String sample)
    {
        this.sample = sample;
        this.replacement = "";
        this.comment = "";
    }

    public Markov()
    {
        this.sample = "";
        this.replacement = "";
        this.comment = "";
    }

    public Markov(Markov markov)
    {
        this.sample = markov.sample;
        this.replacement = markov.replacement;
        this.comment = markov.comment;
    }

    public boolean hasText() { return !sample.isEmpty() || !replacement.isEmpty() || !comment.isEmpty(); }

    public String getSample() { return sample; }
    public String getReplacement() { return replacement; }
    public String getComment() { return comment; }

    public void setSample(String newValue) { sample = newValue; }
    public void setReplacement(String newValue) { replacement = newValue; }
    public void setComment(String newValue) { comment = newValue; }
}
