package com.example.habithelper.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.habithelper.R;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.habithelper.activities.ChooseIconActivity;
import com.example.habithelper.activities.MainActivity;
import com.example.habithelper.models.Habit;
import com.example.habithelper.views.DrawIconView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class CreateIconFragment extends DialogFragment {

    public static final String TAG = "MainActivity";
    public static final int BITMAP_QUALITY = 40;

    DrawIconView myDrawView;
    ParseUser currentUser;
    Button btnSetIcon;
    String currentCustomIconName = "Custom Habit";
    String secondIconName = "Custom Habit";
    Boolean hasSecondCustom = false;

    public CreateIconFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static CreateIconFragment newInstance(String title, String secondIconName, Boolean hasSecondCustom) {
        CreateIconFragment frag = new CreateIconFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("secondIconName", secondIconName);
        args.putBoolean("hasSecondCustom", hasSecondCustom);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_icon, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myDrawView = view.findViewById(R.id.myDrawView);
        btnSetIcon = view.findViewById(R.id.btnSetIcon);

        currentUser = ParseUser.getCurrentUser();

        // Fetch arguments from bundle and set title
        currentCustomIconName = getArguments().getString("title", "Custom Habit");
        secondIconName = getArguments().getString("secondIconName", "Custom Habit");
        hasSecondCustom = getArguments().getBoolean("hasSecondCustom");
        getDialog().setTitle(currentCustomIconName);

        btnSetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap canvasImage = myDrawView.getBitmap();
                setImage(canvasImage);
            }
        });
    }

    public void setImage(Bitmap image) {
        // Configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        image.setHasAlpha(true);
        image.compress(Bitmap.CompressFormat.PNG, BITMAP_QUALITY, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        File resizedFile = getPhotoFileUri("photo.png");
        putIconToParse(resizedFile);
        try {
            resizedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resizedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Write the bytes of the bitmap to file
        try {
            fos.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Get the photoFileUri for the file to be saved in
     * @param fileName the file to save
     * @return the file target for the photo
     */
    public File getPhotoFileUri(String fileName){
        // get safe storage directory for photos
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() +File.separator+fileName);
    }

    /**
     * Saves the file the user drew as the habit's image icon
     * @param file the file containing what the user drew
     */
    private void putIconToParse(File file) {
        ParseQuery<Habit> query = ParseQuery.getQuery(Habit.class);
        query.include(Habit.KEY_HABIT_NAME);
        query.include(Habit.KEY_HABIT_CREATOR);
        query.whereEqualTo(Habit.KEY_HABIT_CREATOR, currentUser);
        query.whereEqualTo(Habit.KEY_HABIT_NAME, currentCustomIconName);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Habit>() {
            @Override
            public void done(List<Habit> habits, ParseException e) {
                if (e != null) {
                    return;
                }
                if(habits.size() == 1){
                    Habit currentHabit = habits.get(0);
                    currentHabit.setHabitImageKey("customIcon");
                    currentHabit.setHabitCustomIcon(new ParseFile(file));
                    currentHabit.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                            }
                            startNextActivity();
                            return;
                        }
                    });
                }
            }
        });
    }

    /**
     * either goes to the secondIconActivity or to MainActivity if the user doesn't have a second activity
     */
    private void startNextActivity() {
        if (hasSecondCustom){
            Intent i = new Intent (getContext(), ChooseIconActivity.class);
            i.putExtra("currentCustomIconName", secondIconName);
            i.putExtra("hasSecondCustom", false);
            getContext().startActivity(i);
        } else {
            // goes back to the MainActivity class with the user logged in
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
        }
    }
}
