package org.bloostatics.models;

/**
 * Created by stdima on 23.04.16.
 */
public class GeneralBloodAnalysisTrainingSet
{
    int ageCategory;
    double leukocytes;
    double erythrocytes;
    double hemoglobin;
    double hematocrit;
    double erythrocytesMedian;
    double hemoglobinInErythrocyte;
    double hemoglobinAverageInErythrocyte;
    double platelets;

    public int getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(int ageCategory) {
        this.ageCategory = ageCategory;
    }

    public double getLeukocytes() {
        return leukocytes;
    }

    public void setLeukocytes(double leukocytes) {
        this.leukocytes = leukocytes;
    }

    public double getErythrocytes() {
        return erythrocytes;
    }

    public void setErythrocytes(double erythrocytes) {
        this.erythrocytes = erythrocytes;
    }

    public double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public double getHematocrit() {
        return hematocrit;
    }

    public void setHematocrit(double hematocrit) {
        this.hematocrit = hematocrit;
    }

    public double getErythrocytesMedian() {
        return erythrocytesMedian;
    }

    public void setErythrocytesMedian(double erythrocytesMedian) {
        this.erythrocytesMedian = erythrocytesMedian;
    }

    public double getHemoglobinInErythrocyte() {
        return hemoglobinInErythrocyte;
    }

    public void setHemoglobinInErythrocyte(double hemoglobinInErythrocyte) {
        this.hemoglobinInErythrocyte = hemoglobinInErythrocyte;
    }

    public double getHemoglobinAverageInErythrocyte() {
        return hemoglobinAverageInErythrocyte;
    }

    public void setHemoglobinAverageInErythrocyte(double hemoglobinAverageInErythrocyte) {
        this.hemoglobinAverageInErythrocyte = hemoglobinAverageInErythrocyte;
    }

    public double getPlatelets() {
        return platelets;
    }

    public void setPlatelets(double platelets) {
        this.platelets = platelets;
    }
}
