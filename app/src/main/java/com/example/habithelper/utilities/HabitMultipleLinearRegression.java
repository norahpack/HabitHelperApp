package com.example.habithelper.utilities;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression;

public class HabitMultipleLinearRegression extends AbstractMultipleLinearRegression {

    private QRDecomposition qrDecomposition = null;

    /**
     * Loads model habitHistoryArray and moodArray sample data into a RealMatrix and RealVector (respectively)
     * and checks that their dimensions are suitable to perform MultipleLinearRegression
     *
     * @param moodArray the numDaysTracked by 1 array representing the User's mood history
     * @param habitHistoryArray the numDaysTracked by numHabits array representing the User's habit history
     * @throws MathIllegalArgumentException if the arrays are not the proper dimensions
     */
    public void loadAndCheckData(double[] moodArray, double[][] habitHistoryArray) throws MathIllegalArgumentException {
        // checks the dimensions and contents of habitHistoryArray and moodArray
        validateSampleData(habitHistoryArray, moodArray);
        newYSampleData(moodArray);
        newXSampleData(habitHistoryArray);
    }

    /**
     * Loads the contents of habitHistoryArray into a RealMatrix (which QR decomposition can be performed on)
     * Performs QR decomposition on the RealMatrix representing the User's habit history
     * Represents and stores the RealMatrix as a product QR of two RealMatrix Objects, an orthogonal matrix Q
     * and an upper triangular matrix R.
     */
    @Override
    protected void newXSampleData(double[][] habitHistoryArray) {
        super.newXSampleData(habitHistoryArray);
        qrDecomposition = new QRDecomposition(getX());
    }

    /**
     * Calculates the regression coefficients using Ordinary Least Squares.
     * qrDecomposition.getSolver().solve(RealVector y) gets a DecompositionSolver Object
     * which can find the least squares solution ^?? to X?? = y for a given RealVector y
     * and a RealMatrix X (the habitHistoryList RealMatrix) where X = QR
     * using the formula ^??  = R?????Q???y
     *
     * @return ^?? - a vector representing the least-squares solution to X?? = y
     * @throws org.apache.commons.math3.linear.SingularMatrixException if R is singular (i.e. if R????? does not exist)
     * @throws NullPointerException if the data for the model has not been loaded successfully
     */
    @Override
    public RealVector calculateBeta() {
        return qrDecomposition.getSolver().solve(getY());
    }

    /**
     * Calculates the variance-covariance matrix of the regression parameters.
     * The variance-covariance matrix can be calculated by (X???X)?????,
     * which can be reduced by QR decomposition to (R???R)????? (with only the top betaSize rows of R included)
     *
     * @return The variance-covariance matrix
     * @throws org.apache.commons.math3.linear.SingularMatrixException if R???R is singular (i.e. if (R???R)????? does not exist)
     * @throws NullPointerException if the data for the model has not been loaded successfully
     */
    @Override
    public RealMatrix calculateBetaVariance() {
        int betaSize = getX().getColumnDimension();
        RealMatrix rAugmented = qrDecomposition.getR().getSubMatrix(0, betaSize - 1, 0, betaSize - 1);
        RealMatrix rInverse = new LUDecomposition(rAugmented).getSolver().getInverse();
        return rInverse.multiply(rInverse.transpose());
    }
}
