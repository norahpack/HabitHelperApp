package com.example.habithelper.utilities;

import android.view.View;

import com.example.habithelper.models.TrackDay;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class LinearRegressionCalculator {

    private ParseUser currentUser;
    double[] moodList;
    double[][] habitHistoryList;
    public int numDaysTracked;
    private int numHabits;
    public double[] leastSquaresResult;

    public LinearRegressionCalculator() {
        this.currentUser=ParseUser.getCurrentUser();
    }

    /**
     * Performs least squares linear regression on the user's list of moods and habits
     *
     * @param daysTracked the list holding every TrackDay Parse object corresponding to the currentUser
     * @return an array of coefficients corresponding to the correlation of each habit to the user's mood
     */
    public void performLeastSquares(List<TrackDay> daysTracked, int numDaysTracked, int numHabits) {
        moodList = new double[numDaysTracked];
        habitHistoryList = new double[numDaysTracked][numHabits]; //first is data, second is predictors

        // putting the data from the Parse database into lists that we can perform OLSMultipleLinearRegression on
        for (int i = 0; i < numDaysTracked; i++) {
            List<Integer> trackDayArray = daysTracked.get(i).getTrackArray();
            moodList[i] = daysTracked.get(i).getMood();
            double[] coefficientList = new double[trackDayArray.size()];
            for (int k = 0; k < trackDayArray.size(); k++) {
                coefficientList[k] = (double) trackDayArray.get(k);
            }
            habitHistoryList[i] = coefficientList;
        }

        OLSMultipleLinearRegression myMLR = new OLSMultipleLinearRegression();
        myMLR.newSampleData(moodList, habitHistoryList);
        RealVector resultsVector = myMLR.calculateBeta();
        double[] tempArray = resultsVector.toArray();

        // removes the y-intercept value from the array of predictor coefficients
        double[] resultsArray = new double[tempArray.length - 1];
        for (int i = 1; i < tempArray.length; i++) {
            resultsArray[i - 1] = tempArray[i];
        }
        leastSquaresResult=resultsArray;
    }

    /**
     * returns the index of the largest element in an array
     *
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
        if (array == null || array.length == 0) {
            return -1; // null or empty
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
        if (array == null || array.length == 0) {
            return -1; // null or empty
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
