package com.example.habithelper.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.habithelper.LoginActivity;
import com.example.habithelper.MainActivity;
import com.example.habithelper.OLSMultipleLinearRegression;
import com.example.habithelper.R;
import com.example.habithelper.TrackDay;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.commons.math3.linear.RealVector;
import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    Button btnLogout;
    TextView tvHello;
    TextView tvThreeOne;
    TextView tvThreeTwo;
    TextView tvThreeThree;
    double[] y;
    double[][] x;
    public int dimension_one;
    public int dimension_two;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        btnLogout=view.findViewById(R.id.btnLogout);
        tvHello=view.findViewById(R.id.tvHello);
        tvThreeOne=view.findViewById(R.id.tvThreeOne);
        tvThreeTwo=view.findViewById(R.id.tvThreeTwo);
        tvThreeThree=view.findViewById(R.id.tvThreeThree);

        ParseUser currentUser = ParseUser.getCurrentUser();
        String name = currentUser.getString("name");
        tvHello.setText("Nice to see you again, "+name);


        ParseQuery<TrackDay> query = ParseQuery.getQuery(TrackDay.class);
        query.include(TrackDay.KEY_PARENT_USER);
        query.include(TrackDay.KEY_DATE_NUMBER);
        query.include(TrackDay.KEY_TRACK_ARRAY);
        query.include(TrackDay.KEY_MOOD);
        query.whereEqualTo(TrackDay.KEY_PARENT_USER, currentUser);
        query.addDescendingOrder(TrackDay.KEY_DATE_NUMBER);
        query.findInBackground(new FindCallback<TrackDay>() {
            @Override
            public void done(List<TrackDay> daysTracked, ParseException e) {
                if (e != null) {
                    return;
                }
                dimension_one=daysTracked.size();
                if(dimension_one>=5){
                    dimension_two=daysTracked.get(0).getTrackArray().size();
                    y = new double[dimension_one];
                    x = new double[dimension_one][dimension_two]; //first is data, second is predictors
                    for (int i = 0; i<daysTracked.size(); i++){
                        List<Integer> trackDayArray = daysTracked.get(i).getTrackArray();
                        y[i]=daysTracked.get(i).getMood();
                        double[] myList = new double[trackDayArray.size()];
                        for (int k=0; k<trackDayArray.size(); k++) {
                            myList[k] = (double) trackDayArray.get(k);
                        }
                        x[i]=myList;
                    }
                    OLSMultipleLinearRegression myMLR = new OLSMultipleLinearRegression();
                    myMLR.newSampleData(y,x);
                    RealVector resultsVector = myMLR.calculateBeta();
                    double[] tempArray = resultsVector.toArray();
                    double[] resultsArray = new double[tempArray.length-1];
                    for (int i=1; i<tempArray.length; i++){
                        resultsArray[i-1]=tempArray[i];
                    }

                    System.out.println(currentUser.getJSONArray("habitsList"));
                    System.out.println(Arrays.toString(resultsArray));

                    //getting the three most impactful habits on a user's mood
                    try {
                        Object firstObject = currentUser.getJSONArray("habitsList").get(getIndexOfLargest(resultsArray));
                        String firstString = String.valueOf(firstObject);
                        tvThreeOne.setText("1. "+firstString);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        Object secondObject = currentUser.getJSONArray("habitsList").get(getIndexOfSecondLargest(resultsArray));
                        String secondString = String.valueOf(secondObject);
                        tvThreeTwo.setText("2. "+secondString);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        Object thirdObject = currentUser.getJSONArray("habitsList").get(getIndexOfThirdLargest(resultsArray));
                        String thirdString = String.valueOf(thirdObject);
                        tvThreeThree.setText("3. "+thirdString);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent i = new Intent (getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    public int getIndexOfLargest(double[] array){
        if ( array == null || array.length == 0 ){
            return -1; // null or empty
        }
        int largest = 0;
        for ( int i = 1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] ) largest = i;
        }
        return largest; // position of the first largest found
    }

    public int getIndexOfSecondLargest(double[] array){
        int largest;
        if ( array == null || array.length == 0 ){
            return -1; // null or empty
        }
        int max = getIndexOfLargest(array);
        if (max == 0){
            largest = 1;
        } else {
            largest = 0;
        }
        for ( int i = largest+1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] && array[i]<array[max]){
                largest = i;
            }
        }
        return largest; // position of the first largest found
    }

    public int getIndexOfThirdLargest(double[] array){
        int largest;
        if ( array == null || array.length == 0 ){
            return -1; // null or empty
        }
        int max = getIndexOfLargest(array);
        int secondMax = getIndexOfSecondLargest(array);
        if(max==0 || secondMax==0){
            if(max==1 || secondMax==1){
                largest=2;
            } else {
                largest=1;
            }
        } else {
            largest = 0;
        }

        for ( int i = largest+1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] && array[i]<array[secondMax]){
                largest = i;
            }
        }
        return largest; // position of the first largest found
    }

}