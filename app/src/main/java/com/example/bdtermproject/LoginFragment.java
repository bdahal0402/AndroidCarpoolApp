package com.example.bdtermproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class LoginFragment extends Fragment implements View.OnClickListener {
    EditText username;
    EditText password;

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        View v = inflator.inflate(R.layout.login, container,false);
        Button singUpButton= (Button) v.findViewById(R.id.signUpButton);
        Button loginButton = (Button) v.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        singUpButton.setOnClickListener(this);
        username =  v.findViewById(R.id.loginUsername);
        password =  v.findViewById(R.id.loginPassword);
        return v;

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpButton:
                SignUpFragment signUpFragment = new SignUpFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.portraitFrame, signUpFragment)
                        .commit();
                break;
            case R.id.loginButton:

                try {

                    if (username.getText().toString() == "admin" && password.getText().toString() == "password") {
                        HomePage homePage = new HomePage();
                        FragmentManager homePageManager = getFragmentManager();
                        homePageManager.beginTransaction()
                                .replace(R.id.portraitFrame, homePage)
                                .commit();
                    }
                    else {
                        Toast toast = Toast.makeText(getContext(),
                                "Incorrect username or password!",
                                Toast.LENGTH_SHORT);

                        toast.show();
                        break;
                    }
                }
                catch(NullPointerException E) {
                    Toast toast = Toast.makeText(getContext(),
                            "You must type in both username and password!",
                            Toast.LENGTH_SHORT);

                    toast.show();
                    break;
                }
                break;


        }
    }


}
