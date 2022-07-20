package com.example.habithelper.utilities;

import android.util.Log;
import com.example.habithelper.models.TrackDay;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.List;

public class LinearRegressionCalculator {

    double[] moodArray;
    double[][] habitHistoryArray;
    public double[] leastSquaresResult;

    public LinearRegressionCalculator() {
    }

    /**
     * Performs least squares linear regression on the user's list of moods and habits
     *
     * @param daysTracked the list holding every TrackDay Parse object corresponding to the currentUser
     * @param numDaysTracked the number of days the user has tracked so far
     * @param numHabits the number of habits the user is tracking
     * @return an array of coefficients corresponding to the correlation of each habit to the user's mood
     */
    public void performLeastSquares(List<TrackDay> daysTracked, int numDaysTracked, int numHabits) {
        moodArray = new double[numDaysTracked];
        habitHistoryArray = new double[numDaysTracked][numHabits];

        putParseDataIntoLists(daysTracked, numDaysTracked);

        HabitMultipleLinearRegression habitMLR = new HabitMultipleLinearRegression();
        try {
            habitMLR.loadAndCheckData(moodArray, habitHistoryArray);
        }
        catch(Exception e) {
            if (e instanceof MathIllegalArgumentException){
                Log.e("LinearRegressionCalculator", "data does not fit parameters for MLR");
            } else {
                Log.e("LinearRegressionCalculator", "something went wrong");
            }
        }
        try{
            double[] tempArray = habitMLR.calculateBeta().toArray();
            // removes the y-intercept value from the array of predictor coefficients
            double[] resultsArray = new double[tempArray.length - 1];
            for (int i = 1; i < tempArray.length; i++) {
                resultsArray[i - 1] = tempArray[i];
            }
            leastSquaresResult = resultsArray;
        } catch (Exception e) {
            Log.e("LinearRegressionCalculator", "error with performing linear regression");
        }
    }

    /**
     * Obtain the data from the Parse database and put it into arrays that we can perform HabitMultipleLinearRegression on
     * @param daysTracked the list holding every TrackDay Parse object corresponding to the currentUser
     * @param numDaysTracked the number of days the user has tracked so far
     */
    private void putParseDataIntoLists(List<TrackDay> daysTracked, int numDaysTracked) {
        for (int i = 0; i < numDaysTracked; i++) {
            List<Integer> trackDayArray = daysTracked.get(i).getTrackArray();
            moodArray[i] = daysTracked.get(i).getMood();
            double[] coefficientList = new double[trackDayArray.size()];
            for (int k = 0; k < trackDayArray.size(); k++) {
                coefficientList[k] = (double) trackDayArray.get(k);
            }
            habitHistoryArray[i] = coefficientList;
        }
    }

    /**
     * returns the index of the largest element in an array
     * @param array the array to find the largest element in
     * @return the index of the largest element in the array
     */
    public int getIndexOfLargest(double[] array) {
        if (array == null || array.length == 0) {
            return -1; // null or empty
        }
        int largest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[largest]) largest = i;
        }
        return largest; // index of the first largest element found
    }

    /**
     * returns the index of the second largest element in an array
     *
     * @param array the array to find the second largest element in
     * @return the index of the second largest element in the array
     */
    public int getIndexOfSecondLargest(double[] array) {
        int secondLargest;
        if (array == null || array.length < 2) {
            return -1; // null or array length less than 2
        }
        int largest = getIndexOfLargest(array);
        if (largest == 0) {
            secondLargest = 1;
        } else {
            secondLargest = 0;
        }
        for (int i = secondLargest + 1; i < array.length; i++) {
            if (array[i] > array[secondLargest] && array[i] < array[largest]) {
                secondLargest = i;
            }
        }
        return secondLargest; // index of the second largest element found
    }

    /**
     * returns the index of the third largest element in an array
     *
     * @param array the array to find the third largest element in
     * @return the index of the third largest element in the array
     */
    public int getIndexOfThirdLargest(double[] array) {
        int thirdLargest;
        if (array == null || array.length < 3) {
            return -1; // null or length less than 3
        }
        int largest = getIndexOfLargest(array);
        int secondLargest = getIndexOfSecondLargest(array);
        if (largest == 0 || secondLargest == 0) {
            if (largest == 1 || secondLargest == 1) {
                thirdLargest = 2;
            } else {
                thirdLargest = 1;
            }
        } else {
            thirdLargest = 0;
        }
        for (int i = thirdLargest + 1; i < array.length; i++) {
            if (array[i] > array[thirdLargest] && array[i] < array[secondLargest]) {
                thirdLargest = i;
            }
        }
        return thirdLargest; // index of the third largest element found
    }
}
