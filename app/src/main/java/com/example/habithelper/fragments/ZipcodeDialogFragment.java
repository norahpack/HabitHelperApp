package com.example.habithelper.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.habithelper.R;
import com.example.habithelper.activities.MainActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import okhttp3.Headers;

public class ZipcodeDialogFragment extends DialogFragment {

    private EditText etNewZip;
    private Button btnSetNewZip;
    private String zipString;
    ParseUser currentUser;

    public ZipcodeDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static ZipcodeDialogFragment newInstance(String title) {
        ZipcodeDialogFragment frag = new ZipcodeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_zipcode, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNewZip = (EditText) view.findViewById(R.id.etNewZip);
        btnSetNewZip = view.findViewById(R.id.btnSetNewZip);
        currentUser = ParseUser.getCurrentUser();

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter New Zipcode");
        getDialog().setTitle(title);

        btnSetNewZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkZipcode();
            }
        });
    }

    private void checkZipcode() {
        zipString = etNewZip.getText().toString();
        if (zipString.isEmpty()) {
            Toast.makeText(getContext(), "zipcode cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        String api_request = TrackFragment.GET_WEATHER_URL + zipString;
        client.get(api_request, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                currentUser.put("zipCode", zipString);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            return;
                        }
                        //goes back to the profile tab and refreshes data
                        MainActivity.self.setTab(new ProfileFragment(), R.id.itemProfile);
                        dismiss();
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(getContext(), "Not a valid zipcode!", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
}
