package com.example.ethan_perera_2331419;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class NaiveBayesClassifier {

    private final Map<String, Integer> sportsWords = new HashMap<>();
    private final Map<String, Integer> politicsWords = new HashMap<>();
    private final Map<String, Integer> horrorWords = new HashMap<>();
    private final Map<String, Integer> romanceWords = new HashMap<>();
    private final Map<String, Integer> scienceFictionWords = new HashMap<>();
    private final Map<String, Integer> comedyWords = new HashMap<>();

    private int totalSportsWords = 0;
    private int totalPoliticsWords = 0;
    private int totalHorrorWords = 0;
    private int totalRomanceWords = 0;
    private int totalScienceFictionWords = 0;
    private int totalComedyWords = 0;

    private int totalSportsDocs = 0;
    private int totalPoliticsDocs = 0;
    private int totalHorrorDocs = 0;
    private int totalRomanceDocs = 0;
    private int totalScienceFictionDocs = 0;
    private int totalComedyDocs = 0;

    // Step 1: Train the model by feeding sample data
    public void train(String category, String text) {
        Map<String, Integer> wordMap;
        switch (category) {
            case "Sports":
                wordMap = sportsWords;
                totalSportsDocs++;
                break;
            case "Politics":
                wordMap = politicsWords;
                totalPoliticsDocs++;
                break;
            case "Horror":
                wordMap = horrorWords;
                totalHorrorDocs++;
                break;
            case "Romance":
                wordMap = romanceWords;
                totalRomanceDocs++;
                break;
            case "Science Fiction":
                wordMap = scienceFictionWords;
                totalScienceFictionDocs++;
                break;
            case "Comedy":
                wordMap = comedyWords;
                totalComedyDocs++;
                break;
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }

        for (String word : text.toLowerCase().split(" ")) {
            wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
            switch (category) {
                case "Sports": totalSportsWords++; break;
                case "Politics": totalPoliticsWords++; break;
                case "Horror": totalHorrorWords++; break;
                case "Romance": totalRomanceWords++; break;
                case "Science Fiction": totalScienceFictionWords++; break;
                case "Comedy": totalComedyWords++; break;
            }
        }
    }

    // Step 2: Predict the category of a new document using Naive Bayes
    public String predict(String text) {
        double sportsProbability = Math.log((double) totalSportsDocs / (totalSportsDocs + totalPoliticsDocs + totalHorrorDocs + totalRomanceDocs + totalScienceFictionDocs + totalComedyDocs));
        double politicsProbability = Math.log((double) totalPoliticsDocs / (totalSportsDocs + totalPoliticsDocs + totalHorrorDocs + totalRomanceDocs + totalScienceFictionDocs + totalComedyDocs));
        double horrorProbability = Math.log((double) totalHorrorDocs / (totalSportsDocs + totalPoliticsDocs + totalHorrorDocs + totalRomanceDocs + totalScienceFictionDocs + totalComedyDocs));
        double romanceProbability = Math.log((double) totalRomanceDocs / (totalSportsDocs + totalPoliticsDocs + totalHorrorDocs + totalRomanceDocs + totalScienceFictionDocs + totalComedyDocs));
        double scienceFictionProbability = Math.log((double) totalScienceFictionDocs / (totalSportsDocs + totalPoliticsDocs + totalHorrorDocs + totalRomanceDocs + totalScienceFictionDocs + totalComedyDocs));
        double comedyProbability = Math.log((double) totalComedyDocs / (totalSportsDocs + totalPoliticsDocs + totalHorrorDocs + totalRomanceDocs + totalScienceFictionDocs + totalComedyDocs));

        for (String word : text.toLowerCase().split(" ")) {
            sportsProbability += Math.log((sportsWords.getOrDefault(word, 0) + 1) / (double) (totalSportsWords + sportsWords.size()));
            politicsProbability += Math.log((politicsWords.getOrDefault(word, 0) + 1) / (double) (totalPoliticsWords + politicsWords.size()));
            horrorProbability += Math.log((horrorWords.getOrDefault(word, 0) + 1) / (double) (totalHorrorWords + horrorWords.size()));
            romanceProbability += Math.log((romanceWords.getOrDefault(word, 0) + 1) / (double) (totalRomanceWords + romanceWords.size()));
            scienceFictionProbability += Math.log((scienceFictionWords.getOrDefault(word, 0) + 1) / (double) (totalScienceFictionWords + scienceFictionWords.size()));
            comedyProbability += Math.log((comedyWords.getOrDefault(word, 0) + 1) / (double) (totalComedyWords + comedyWords.size()));
        }

        Map<String, Double> probabilities = new HashMap<>();
        probabilities.put("Sports", sportsProbability);
        probabilities.put("Politics", politicsProbability);
        probabilities.put("Horror", horrorProbability);
        probabilities.put("Romance", romanceProbability);
        probabilities.put("Science Fiction", scienceFictionProbability);
        probabilities.put("Comedy", comedyProbability);

        return probabilities.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    public static void main(String[] args) {
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        Scanner sc = new Scanner(System.in);

        // Training data
        // Sports
        classifier.train("Sports", "The team executed a brilliant strategy to secure the championship, with players showcasing skill, speed, and endurance. In a display of athleticism and teamwork, the star player scored the winning goal in the final moments. Fans cheered wildly as the coach emphasized the importance of hard work and determination in his victory speech. This season, every game demonstrated the players' dedication to their craft, with countless hours of practice and preparation leading to this crowning achievement. The victory was a testament to their resilience and spirit, as they overcame many challenges to claim the title.");

        // Politics
        classifier.train("Politics", "The election brought together candidates from all walks of life, each proposing policies to address issues like healthcare, economy, and education. With televised debates and interviews, candidates discussed their visions and solutions for a better future. Voters lined up, eager to make their voices heard. Analysts predicted the race would be close, with pivotal swing states likely deciding the outcome. In the end, the newly elected government vowed to work towards unity, promising reforms and accountability. Political commentators highlighted the historic turnout, calling it a win for democracy and citizen engagement in shaping the nation's future.");

        // Horror
        classifier.train("Horror", "In the dead of night, the old mansion stood ominously, shrouded in mist. Shadows danced on the walls as eerie whispers filled the air, chilling anyone who dared enter. A distant howl echoed, sending shivers down the spine. The flickering candlelight barely illuminated the dark corridors, where strange noises lurked. As footsteps creaked on the wooden floorboards, fear gripped the hearts of those trapped inside, with the haunting feeling that something evil awaited in the shadows. Ghostly apparitions and chilling screams punctuated the silence, leaving a lingering sense of dread. Horror and terror hung thick in the air.");

        // Romance
        classifier.train("Romance", "She felt her heart flutter as they exchanged shy glances across the room. Every touch, every smile, and every laugh they shared brought them closer. Their love story blossomed amidst beautiful sunsets and moonlit walks, where they shared dreams and promises. He whispered sweet words, and she felt a warmth she'd never known. Each moment was filled with tenderness and affection, their love growing with each passing day. Through ups and downs, their bond deepened, making her feel cherished and adored. This love was pure, transcending all obstacles, creating a world of happiness and togetherness for them both.");

        // Science Fiction
        classifier.train("Science Fiction", "In a distant galaxy, advanced civilizations developed technologies that defied imagination. Interstellar ships traveled at light speed, enabling humans to explore faraway planets inhabited by alien species. Scientists worked tirelessly, developing artificial intelligence and nanotechnology, reshaping human potential. The universe held mysteries, with black holes and wormholes leading to unknown dimensions. Space colonies thrived, adapting to hostile environments. Humanity's quest for knowledge led to revolutionary discoveries, but with them came unforeseen dangers. Robots and cyborgs lived alongside humans, blurring the lines between man and machine. The vastness of space was both beautiful and terrifying, filled with endless possibilities.");

        // Comedy
        classifier.train("Comedy", "The comedian took the stage, his quick wit and hilarious anecdotes bringing the audience to tears of laughter. With impeccable timing, he joked about daily struggles, turning life's absurdities into comedic gold. From unexpected mishaps to embarrassing moments, his stories struck a chord, making everyone relate. He impersonated quirky characters, adding to the humor with exaggerated expressions. Each punchline landed perfectly, igniting bursts of laughter across the room. His antics kept the energy high, leaving everyone in stitches. The night was filled with joy and laughter, reminding everyone of the power of comedy to uplift and entertain.");


        // Predicting the category
        System.out.println("Enter a string of words:");
        String category = classifier.predict(sc.nextLine());
        System.out.println("Predicted category: " + category);
    }
}
