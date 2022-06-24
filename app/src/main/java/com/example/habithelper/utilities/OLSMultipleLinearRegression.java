package com.example.habithelper.utilities;

/*
002 * Licensed to the Apache Software Foundation (ASF) under one or more
003 * contributor license agreements.  See the NOTICE file distributed with
004 * this work for additional information regarding copyright ownership.
005 * The ASF licenses this file to You under the Apache License, Version 2.0
006 * (the "License"); you may not use this file except in compliance with
007 * the License.  You may obtain a copy of the License at
008 *
009 *      http://www.apache.org/licenses/LICENSE-2.0
010 *
011 * Unless required by applicable law or agreed to in writing, software
012 * distributed under the License is distributed on an "AS IS" BASIS,
013 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
014 * See the License for the specific language governing permissions and
015 * limitations under the License.
016 */
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression;

/**
 029 * <p>Implements ordinary least squares (OLS) to estimate the parameters of a
 030 * multiple linear regression model.</p>
 031 *
 032 * <p>The regression coefficients, <code>b</code>, satisfy the normal equations:
 033 * <pre><code> X<sup>T</sup> X b = X<sup>T</sup> y </code></pre></p>
 034 *
 035 * <p>To solve the normal equations, this implementation uses QR decomposition
 036 * of the <code>X</code> matrix. (See {@link QRDecomposition} for details on the
 037 * decomposition algorithm.) The <code>X</code> matrix, also known as the <i>design matrix,</i>
 038 * has rows corresponding to sample observations and columns corresponding to independent
 039 * variables.  When the model is estimated using an intercept term (i.e. when
 040 * {@link #isNoIntercept() isNoIntercept} is false as it is by default), the <code>X</code>
 041 * matrix includes an initial column identically equal to 1.  We solve the normal equations
 042 * as follows:
 043 * <pre><code> X<sup>T</sup>X b = X<sup>T</sup> y
 044 * (QR)<sup>T</sup> (QR) b = (QR)<sup>T</sup>y
 045 * R<sup>T</sup> (Q<sup>T</sup>Q) R b = R<sup>T</sup> Q<sup>T</sup> y
 046 * R<sup>T</sup> R b = R<sup>T</sup> Q<sup>T</sup> y
 047 * (R<sup>T</sup>)<sup>-1</sup> R<sup>T</sup> R b = (R<sup>T</sup>)<sup>-1</sup> R<sup>T</sup> Q<sup>T</sup> y
 048 * R b = Q<sup>T</sup> y </code></pre></p>
 049 *
 050 * <p>Given <code>Q</code> and <code>R</code>, the last equation is solved by back-substitution.</p>
 051 *
 052 * @since 2.0
 053 */
public class OLSMultipleLinearRegression extends AbstractMultipleLinearRegression {

    /** Cached QR decomposition of X matrix */
    private QRDecomposition qr = null;

    /** Singularity threshold for QR decomposition */
    private final double threshold;

    /**
     063     * Create an empty OLSMultipleLinearRegression instance.
     064     */
    public OLSMultipleLinearRegression() {
        this(0d);
    }

    /**
     070     * Create an empty OLSMultipleLinearRegression instance, using the given
     071     * singularity threshold for the QR decomposition.
     072     *
     073     * @param threshold the singularity threshold
     074     * @since 3.3
     075     */
    public OLSMultipleLinearRegression(final double threshold) {
        this.threshold = threshold;
    }

    /**
     081     * Loads model x and y sample data, overriding any previous sample.
     082     *
     083     * Computes and caches QR decomposition of the X matrix.
     084     * @param y the [n,1] array representing the y sample
     085     * @param x the [n,k] array representing the x sample
     086     * @throws MathIllegalArgumentException if the x and y array data are not
     087     *             compatible for the regression
     088     */
    public void newSampleData(double[] y, double[][] x) throws MathIllegalArgumentException {
        validateSampleData(x, y);
        newYSampleData(y);
        newXSampleData(x);
    }

    /**
     096     * {@inheritDoc}
     097     * <p>This implementation computes and caches the QR decomposition of the X matrix.</p>
     098     */
    @Override
    public void newSampleData(double[] data, int nobs, int nvars) {
        super.newSampleData(data, nobs, nvars);
        qr = new QRDecomposition(getX(), threshold);
    }

    /**
     106     * <p>Compute the "hat" matrix.
     107     * </p>
     108     * <p>The hat matrix is defined in terms of the design matrix X
     109     *  by X(X<sup>T</sup>X)<sup>-1</sup>X<sup>T</sup>
     110     * </p>
     111     * <p>The implementation here uses the QR decomposition to compute the
     112     * hat matrix as Q I<sub>p</sub>Q<sup>T</sup> where I<sub>p</sub> is the
     113     * p-dimensional identity matrix augmented by 0's.  This computational
     114     * formula is from "The Hat Matrix in Regression and ANOVA",
     115     * David C. Hoaglin and Roy E. Welsch,
     116     * <i>The American Statistician</i>, Vol. 32, No. 1 (Feb., 1978), pp. 17-22.
     117     * </p>
     118     * <p>Data for the model must have been successfully loaded using one of
     119     * the {@code newSampleData} methods before invoking this method; otherwise
     120     * a {@code NullPointerException} will be thrown.</p>
     121     *
     122     * @return the hat matrix
     123     * @throws NullPointerException unless method {@code newSampleData} has been
     124     * called beforehand.
     125     */
    public RealMatrix calculateHat() {
        // Create augmented identity matrix
        RealMatrix Q = qr.getQ();
        final int p = qr.getR().getColumnDimension();
        final int n = Q.getColumnDimension();
        // No try-catch or advertised NotStrictlyPositiveException - NPE above if n < 3
        Array2DRowRealMatrix augI = new Array2DRowRealMatrix(n, n);
        double[][] augIData = augI.getDataRef();
        for (int i = 0; i < n; i++) {
            for (int j =0; j < n; j++) {
                if (i == j && i < p) {
                    augIData[i][j] = 1d;
                } else {
                    augIData[i][j] = 0d;
                }
            }
        }

        // Compute and return Hat matrix
        // No DME advertised - args valid if we get here
        return Q.multiply(augI).multiply(Q.transpose());
    }

    /**
     150     * <p>Returns the sum of squared deviations of Y from its mean.</p>
     151     *
     152     * <p>If the model has no intercept term, <code>0</code> is used for the
     153     * mean of Y - i.e., what is returned is the sum of the squared Y values.</p>
     154     *
     155     * <p>The value returned by this method is the SSTO value used in
     156     * the {@link #calculateRSquared() R-squared} computation.</p>
     157     *
     158     * @return SSTO - the total sum of squares
     159     * @throws NullPointerException if the sample has not been set
     160     * @see #isNoIntercept()
     161     * @since 2.2
     162     */
    public double calculateTotalSumOfSquares() {
        if (isNoIntercept()) {
            return StatUtils.sumSq(getY().toArray());
        } else {
            return new SecondMoment().evaluate(getY().toArray());
        }
    }

    /**
     172     * Returns the sum of squared residuals.
     173     *
     174     * @return residual sum of squares
     175     * @since 2.2
     176     * @throws org.apache.commons.math3.linear.SingularMatrixException if the design matrix is singular
     177     * @throws NullPointerException if the data for the model have not been loaded
     178     */
    public double calculateResidualSumOfSquares() {
        final RealVector residuals = calculateResiduals();
        // No advertised DME, args are valid
        return residuals.dotProduct(residuals);
    }

    /**
     186     * Returns the R-Squared statistic, defined by the formula <pre>
     187     * R<sup>2</sup> = 1 - SSR / SSTO
     188     * </pre>
     189     * where SSR is the {@link #calculateResidualSumOfSquares() sum of squared residuals}
     190     * and SSTO is the {@link #calculateTotalSumOfSquares() total sum of squares}
     191     *
     192     * <p>If there is no variance in y, i.e., SSTO = 0, NaN is returned.</p>
     193     *
     194     * @return R-square statistic
     195     * @throws NullPointerException if the sample has not been set
     196     * @throws org.apache.commons.math3.linear.SingularMatrixException if the design matrix is singular
     197     * @since 2.2
     198     */
    public double calculateRSquared() {
        return 1 - calculateResidualSumOfSquares() / calculateTotalSumOfSquares();
    }

    /**
     204     * <p>Returns the adjusted R-squared statistic, defined by the formula <pre>
     205     * R<sup>2</sup><sub>adj</sub> = 1 - [SSR (n - 1)] / [SSTO (n - p)]
     206     * </pre>
     207     * where SSR is the {@link #calculateResidualSumOfSquares() sum of squared residuals},
     208     * SSTO is the {@link #calculateTotalSumOfSquares() total sum of squares}, n is the number
     209     * of observations and p is the number of parameters estimated (including the intercept).</p>
     210     *
     211     * <p>If the regression is estimated without an intercept term, what is returned is <pre>
     212     * <code> 1 - (1 - {@link #calculateRSquared()}) * (n / (n - p)) </code>
     213     * </pre></p>
     214     *
     215     * <p>If there is no variance in y, i.e., SSTO = 0, NaN is returned.</p>
     216     *
     217     * @return adjusted R-Squared statistic
     218     * @throws NullPointerException if the sample has not been set
     219     * @throws org.apache.commons.math3.linear.SingularMatrixException if the design matrix is singular
     220     * @see #isNoIntercept()
     221     * @since 2.2
     222     */
    public double calculateAdjustedRSquared() {
        final double n = getX().getRowDimension();
        if (isNoIntercept()) {
            return 1 - (1 - calculateRSquared()) * (n / (n - getX().getColumnDimension()));
        } else {
            return 1 - (calculateResidualSumOfSquares() * (n - 1)) /
                    (calculateTotalSumOfSquares() * (n - getX().getColumnDimension()));
        }
    }

    /**
     234     * {@inheritDoc}
     235     * <p>This implementation computes and caches the QR decomposition of the X matrix
     236     * once it is successfully loaded.</p>
     237     */
    @Override
    protected void newXSampleData(double[][] x) {
        super.newXSampleData(x);
        qr = new QRDecomposition(getX(), threshold);
    }

    /**
     245     * Calculates the regression coefficients using OLS.
     246     *
     247     * <p>Data for the model must have been successfully loaded using one of
     248     * the {@code newSampleData} methods before invoking this method; otherwise
     249     * a {@code NullPointerException} will be thrown.</p>
     250     *
     251     * @return beta
     252     * @throws org.apache.commons.math3.linear.SingularMatrixException if the design matrix is singular
     253     * @throws NullPointerException if the data for the model have not been loaded
     254     */
    @Override
    public RealVector calculateBeta() {
        return qr.getSolver().solve(getY());
    }

    /**
     261     * <p>Calculates the variance-covariance matrix of the regression parameters.
     262     * </p>
     263     * <p>Var(b) = (X<sup>T</sup>X)<sup>-1</sup>
     264     * </p>
     265     * <p>Uses QR decomposition to reduce (X<sup>T</sup>X)<sup>-1</sup>
     266     * to (R<sup>T</sup>R)<sup>-1</sup>, with only the top p rows of
     267     * R included, where p = the length of the beta vector.</p>
     268     *
     269     * <p>Data for the model must have been successfully loaded using one of
     270     * the {@code newSampleData} methods before invoking this method; otherwise
     271     * a {@code NullPointerException} will be thrown.</p>
     272     *
     273     * @return The beta variance-covariance matrix
     274     * @throws org.apache.commons.math3.linear.SingularMatrixException if the design matrix is singular
     275     * @throws NullPointerException if the data for the model have not been loaded
     276     */
    @Override
    public RealMatrix calculateBetaVariance() {
        int p = getX().getColumnDimension();
        RealMatrix Raug = qr.getR().getSubMatrix(0, p - 1 , 0, p - 1);
        RealMatrix Rinv = new LUDecomposition(Raug).getSolver().getInverse();
        return Rinv.multiply(Rinv.transpose());
    }

}
