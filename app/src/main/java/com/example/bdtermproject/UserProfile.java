package com.example.bdtermproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sdsmdg.tastytoast.TastyToast;


public class UserProfile extends Fragment {
    static boolean alreadyRated;
    private RelativeLayout mRelativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View mView = inflater.inflate(R.layout.user_profile, container, false);
        ListView lv = mView.findViewById(R.id.ratingList);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.textcenter, R.id.textItem, MatchedList.matchClickRatings);
        lv.setAdapter(adapter);

        TextView detailsText = mView.findViewById(R.id.detailUser);
        detailsText.setText(MatchedList.matchClickedDetails);
        TextView fullNameText = mView.findViewById(R.id.userFull);
        fullNameText.setText(MatchedList.matchedClickedFullName);

        alreadyRated = false;
        if (MatchedList.matchClickRatedUsers.contains(Login.loggedInUser))
            alreadyRated = true;



        Button addRating = mView.findViewById(R.id.addReview);
        Button sendEmail = mView.findViewById(R.id.send_email);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{MatchedList.matchClickedEmail});
                i.putExtra(Intent.EXTRA_SUBJECT, "Carpool app");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Trips.viewingSelfProfile) {
                    TastyToast.makeText(getContext(), "You can't leave yourself a review!", TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);
                    return;
                }

                if (alreadyRated) {
                    TastyToast.makeText(getContext(), "You've already left a review for this user!", TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);
                    return;
                }

                RateUser rateUser = new RateUser();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    transaction.add(R.id.userProfileView, rateUser);
                else
                    transaction.add(R.id.matchedUserList, rateUser);
                transaction.commit();
            }
            });


        return mView;
    }
}
