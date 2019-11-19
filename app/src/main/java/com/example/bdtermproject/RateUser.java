package com.example.bdtermproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RateUser extends Fragment {

    ImageButton star1;
    ImageButton star2;
    ImageButton star3;
    ImageButton star4;
    ImageButton star5;

    EditText ratingText;
    int starValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.add_rating, container, false);

        ImageButton closeBtn = mView.findViewById(R.id.ib_close);
        ratingText = mView.findViewById(R.id.reviewMsg);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateUser rateUser = RateUser.this;
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.remove(rateUser);
                transaction.commit();
            }
        });

        star1 = mView.findViewById(R.id.star1);
        star2 = mView.findViewById(R.id.star2);
        star3 = mView.findViewById(R.id.star3);
        star4 = mView.findViewById(R.id.star4);
        star5 = mView.findViewById(R.id.star5);
        starValue = 0;

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.empty_star);
                star3.setImageResource(R.drawable.empty_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                starValue = 1;
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.empty_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                starValue = 2;
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.filled_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                starValue = 3;
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.filled_star);
                star4.setImageResource(R.drawable.filled_star);
                star5.setImageResource(R.drawable.empty_star);
                starValue = 4;
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.filled_star);
                star2.setImageResource(R.drawable.filled_star);
                star3.setImageResource(R.drawable.filled_star);
                star4.setImageResource(R.drawable.filled_star);
                star5.setImageResource(R.drawable.filled_star);
                starValue = 5;
            }
        });

        Button submit = mView.findViewById(R.id.submitRating);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ratingMessage = ratingText.getText().toString().trim();


                if(starValue == 0){
                    TastyToast.makeText(getContext(), "Please select the number of stars you want to rate this user.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    return;
                }

                if (ratingMessage.length() < 15){
                    ratingText.setError("The review message has to be at least 15 characters!");
                    ratingText.requestFocus();
                    return;
                }
                double totalRatingValue = starValue;
                if (MatchedList.allRatingValues.size() != 0) {
                    for (int i=0; i<MatchedList.allRatingValues.size(); i++){
                        totalRatingValue += MatchedList.allRatingValues.get(i);
                    }
                }

                double divideValue = 1;
                if (MatchedList.allRatingValues.size() > 0)
                    divideValue = MatchedList.allRatingValues.size()+1;
                String averageRating = String.valueOf((totalRatingValue/divideValue));
                JSONObject request = new JSONObject();
                try {
                    request.put("username", Login.loggedInUser);
                    request.put("user", MatchedList.matchClickedUsername);
                    request.put("stars", starValue);
                    request.put("message", ratingMessage);
                    request.put("averageRating", averageRating);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                        (Request.Method.POST, "http://136.32.51.159/carpool/sendReview.php", request, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject e) {

                                try {
                                    if (e.getString("message").contains("success")){
                                        TastyToast.makeText(getContext(), e.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                        return;
                                    }
                                    else{
                                        TastyToast.makeText(getContext(), e.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                        return;
                                    }


                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.print("error");
                            }
                        });

                RequestCall.getInstance(getContext()).addToRequestQueue(jsArrayRequest);

            }
        });

        return mView;
    }
}
