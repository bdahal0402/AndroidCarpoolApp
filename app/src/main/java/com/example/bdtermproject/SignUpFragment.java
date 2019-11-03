package com.example.bdtermproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        View v = inflator.inflate(R.layout.signup, container,false);
        Button b = (Button) v.findViewById(R.id.backToLogin);
        b.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLogin:
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.portraitFrame, loginFragment)
                        .commit();
                break;

        }
    }

}
