package com.example.ethan_perera_2331419;

import java.io.FileInputStream;
import java.io.InputStream;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.TokenizerME;

import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceDetectorME;

public class NLP_Pipeline {

    public static void main(String[] args) {
        // The content here tokenizes a sentence and detects a sentence and then classifes it
        // Having classified it, it is now possible to apply a pre-processing algorithm to minimize the provided data
        // and then utilize a seperate model to detect the general genre/content of a text based on the probability
        // of certain chunks of text consisting of content identifiers such as words like, "love, hate" etc.
        try {
            InputStream sentModelIn = new FileInputStream("src/main/java/com/example/ethan_perera_2331419/BIN/en-sent.bin");
            SentenceModel sentModel = new SentenceModel(sentModelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);

            InputStream tokenModelIn = new FileInputStream("src/main/java/com/example/ethan_perera_2331419/BIN/en-token.bin");
            TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
            TokenizerME tokenizer = new TokenizerME(tokenModel);

            String paragraph = "This is an example sentence. OpenNLP is a powerful tool for NLP.";

            String[] sentences = sentenceDetector.sentDetect(paragraph);

            for (String sentence : sentences) {
                System.out.println("Sentence: " + sentence);
                String[] tokens = tokenizer.tokenize(sentence);
                for (String token : tokens) {
                    System.out.println("Token: " + token);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}