package org.example.paperreview.model;

import java.util.HashMap;
import java.util.Map;

public class ModelMetrics {
    private String modelName;
    private String modelType; // Supervised, Unsupervised, Deep Learning, Reinforcement
    private double accuracy;
    private double precision;
    private double recall;
    private double f1Score;
    private double specificity;
    private double aucScore;
    private double mse; // Mean Squared Error
    private double rmse; // Root Mean Squared Error
    private double mae; // Mean Absolute Error
    private double r2Score; // R-squared Score
    private double silhouetteScore; // For clustering
    private double daviesBouldinScore; // For clustering
    private int trainingTime;
    private int inferenceTime;
    private String bestFor; // What this model is best for
    private Map<String, Double> classWiseMetrics;

    // Constructor with one parameter (for backward compatibility)
    public ModelMetrics(String modelName) {
        this.modelName = modelName;
        this.modelType = "Unknown";
        this.classWiseMetrics = new HashMap<>();
    }

    // Constructor with two parameters
    public ModelMetrics(String modelName, String modelType) {
        this.modelName = modelName;
        this.modelType = modelType;
        this.classWiseMetrics = new HashMap<>();
    }

    // Getters and Setters
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public double getPrecision() { return precision; }
    public void setPrecision(double precision) { this.precision = precision; }

    public double getRecall() { return recall; }
    public void setRecall(double recall) { this.recall = recall; }

    public double getF1Score() { return f1Score; }
    public void setF1Score(double f1Score) { this.f1Score = f1Score; }

    public double getSpecificity() { return specificity; }
    public void setSpecificity(double specificity) { this.specificity = specificity; }

    public double getAucScore() { return aucScore; }
    public void setAucScore(double aucScore) { this.aucScore = aucScore; }

    public double getMse() { return mse; }
    public void setMse(double mse) { this.mse = mse; }

    public double getRmse() { return rmse; }
    public void setRmse(double rmse) { this.rmse = rmse; }

    public double getMae() { return mae; }
    public void setMae(double mae) { this.mae = mae; }

    public double getR2Score() { return r2Score; }
    public void setR2Score(double r2Score) { this.r2Score = r2Score; }

    public double getSilhouetteScore() { return silhouetteScore; }
    public void setSilhouetteScore(double silhouetteScore) { this.silhouetteScore = silhouetteScore; }

    public double getDaviesBouldinScore() { return daviesBouldinScore; }
    public void setDaviesBouldinScore(double daviesBouldinScore) { this.daviesBouldinScore = daviesBouldinScore; }

    public int getTrainingTime() { return trainingTime; }
    public void setTrainingTime(int trainingTime) { this.trainingTime = trainingTime; }

    public int getInferenceTime() { return inferenceTime; }
    public void setInferenceTime(int inferenceTime) { this.inferenceTime = inferenceTime; }

    public String getBestFor() { return bestFor; }
    public void setBestFor(String bestFor) { this.bestFor = bestFor; }

    public Map<String, Double> getClassWiseMetrics() { return classWiseMetrics; }
    public void setClassWiseMetrics(Map<String, Double> classWiseMetrics) { this.classWiseMetrics = classWiseMetrics; }

    public void addClassMetric(String className, double value) {
        classWiseMetrics.put(className, value);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Acc: %.2f%%, F1: %.2f%%",
                modelType, modelName, accuracy * 100, f1Score * 100);
    }
}