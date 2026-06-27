package com.eliza;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Semantic word-level understanding engine
 * Loads word embeddings (GloVe/Word2Vec) for semantic similarity
 * File size: 400K words × 300 dimensions × 4 bytes = ~480MB-1GB
 */
public class SemanticEngine {

    private Map<String, float[]> wordVectors;
    private int dimensions = 300;
    private int vocabularySize = 0;
    private boolean loaded = false;

    public SemanticEngine() {
        wordVectors = new HashMap<>();
    }

    /**
     * Load GloVe format embeddings
     * Format: word dim1 dim2 dim3 ... dimN
     */
    public void loadGloVeEmbeddings(String filePath) throws IOException {
        System.out.println("Loading word embeddings from " + filePath + "...");
        long startTime = System.currentTimeMillis();

        BufferedReader reader;
        if (filePath.endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new FileInputStream(filePath))));
        } else {
            reader = new BufferedReader(new FileReader(filePath));
        }

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length < 2) continue;

            String word = parts[0].toLowerCase();
            float[] vector = new float[parts.length - 1];

            for (int i = 1; i < parts.length; i++) {
                vector[i - 1] = Float.parseFloat(parts[i]);
            }

            wordVectors.put(word, vector);
            vocabularySize++;

            if (vocabularySize % 50000 == 0) {
                System.out.println("  Loaded " + vocabularySize + " words...");
            }
        }

        reader.close();
        dimensions = wordVectors.values().iterator().next().length;
        loaded = true;

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Loaded " + vocabularySize + " words with " + dimensions + " dimensions");
        System.out.println("Time: " + elapsed + "ms, Memory: ~" + estimateMemoryMB() + "MB");
    }

    /**
     * Load Word2Vec binary format
     */
    public void loadWord2VecBinary(String filePath) throws IOException {
        System.out.println("Loading Word2Vec binary from " + filePath + "...");
        DataInputStream dis = new DataInputStream(
            new BufferedInputStream(new FileInputStream(filePath)));

        // Read header
        String header = readString(dis);
        String[] headerParts = header.trim().split(" ");
        int vocabSize = Integer.parseInt(headerParts[0]);
        dimensions = Integer.parseInt(headerParts[1]);

        System.out.println("Vocabulary: " + vocabSize + ", Dimensions: " + dimensions);

        for (int i = 0; i < vocabSize; i++) {
            String word = readString(dis).toLowerCase();
            float[] vector = new float[dimensions];

            for (int j = 0; j < dimensions; j++) {
                vector[j] = readFloat(dis);
            }

            wordVectors.put(word, vector);

            if (i % 50000 == 0 && i > 0) {
                System.out.println("  Loaded " + i + " words...");
            }
        }

        dis.close();
        vocabularySize = vocabSize;
        loaded = true;
        System.out.println("Loaded complete. Memory: ~" + estimateMemoryMB() + "MB");
    }

    private String readString(DataInputStream dis) throws IOException {
        StringBuilder sb = new StringBuilder();
        char c;
        while ((c = (char) dis.readByte()) != ' ' && c != '\n') {
            sb.append(c);
        }
        return sb.toString();
    }

    private float readFloat(DataInputStream dis) throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(dis.readInt()));
    }

    /**
     * Get word vector
     */
    public float[] getVector(String word) {
        return wordVectors.get(word.toLowerCase());
    }

    /**
     * Check if word exists in vocabulary
     */
    public boolean hasWord(String word) {
        return wordVectors.containsKey(word.toLowerCase());
    }

    /**
     * Calculate cosine similarity between two words
     */
    public double wordSimilarity(String word1, String word2) {
        float[] v1 = getVector(word1);
        float[] v2 = getVector(word2);

        if (v1 == null || v2 == null) {
            return 0.0;
        }

        return cosineSimilarity(v1, v2);
    }

    /**
     * Calculate cosine similarity between vectors
     */
    public double cosineSimilarity(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vectors must have same dimensions");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Find most similar words
     */
    public List<SimilarWord> mostSimilar(String word, int topN) {
        float[] targetVector = getVector(word);
        if (targetVector == null) {
            return new ArrayList<>();
        }

        List<SimilarWord> similar = new ArrayList<>();

        for (Map.Entry<String, float[]> entry : wordVectors.entrySet()) {
            if (entry.getKey().equals(word.toLowerCase())) {
                continue;
            }

            double similarity = cosineSimilarity(targetVector, entry.getValue());
            similar.add(new SimilarWord(entry.getKey(), similarity));
        }

        similar.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        return similar.subList(0, Math.min(topN, similar.size()));
    }

    /**
     * Calculate sentence embedding (average of word vectors)
     */
    public float[] sentenceEmbedding(String sentence) {
        String[] words = sentence.toLowerCase().split("\\s+");
        float[] embedding = new float[dimensions];
        int count = 0;

        for (String word : words) {
            float[] vec = getVector(word);
            if (vec != null) {
                for (int i = 0; i < dimensions; i++) {
                    embedding[i] += vec[i];
                }
                count++;
            }
        }

        if (count > 0) {
            for (int i = 0; i < dimensions; i++) {
                embedding[i] /= count;
            }
        }

        return embedding;
    }

    /**
     * Calculate semantic similarity between two sentences
     */
    public double sentenceSimilarity(String s1, String s2) {
        float[] v1 = sentenceEmbedding(s1);
        float[] v2 = sentenceEmbedding(s2);
        return cosineSimilarity(v1, v2);
    }

    /**
     * Find semantically similar patterns from a list
     */
    public List<SemanticMatch> findSemanticMatches(String query, List<String> patterns, double threshold) {
        List<SemanticMatch> matches = new ArrayList<>();
        float[] queryVec = sentenceEmbedding(query);

        for (String pattern : patterns) {
            float[] patternVec = sentenceEmbedding(pattern);
            double similarity = cosineSimilarity(queryVec, patternVec);

            if (similarity >= threshold) {
                matches.add(new SemanticMatch(pattern, similarity));
            }
        }

        matches.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        return matches;
    }

    /**
     * Estimate memory usage
     */
    public int estimateMemoryMB() {
        return (vocabularySize * dimensions * 4) / (1024 * 1024);
    }

    public int getVocabularySize() {
        return vocabularySize;
    }

    public int getDimensions() {
        return dimensions;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public static class SimilarWord {
        public String word;
        public double similarity;

        public SimilarWord(String word, double similarity) {
            this.word = word;
            this.similarity = similarity;
        }

        @Override
        public String toString() {
            return String.format("%s (%.4f)", word, similarity);
        }
    }

    public static class SemanticMatch {
        public String pattern;
        public double similarity;

        public SemanticMatch(String pattern, double similarity) {
            this.pattern = pattern;
            this.similarity = similarity;
        }

        @Override
        public String toString() {
            return String.format("'%s' (%.4f)", pattern, similarity);
        }
    }
}
