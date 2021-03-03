package com.example.solitaresolver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Mainmenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mainmenu extends Fragment implements View.OnClickListener {

    Button cameraButton, settingsButton, helpButton;


    public Mainmenu() {
        // Required empty public constructor
    }

    public static Mainmenu newInstance() {
        Mainmenu fragment = new Mainmenu();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mainmenu, container, false);

        cameraButton = v.findViewById(R.id.mainCamera);
        settingsButton =v.findViewById(R.id.mainSettings);
        helpButton = v.findViewById(R.id.mainHelp);


        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onClick(View v) {

        if (v == cameraButton){
            //todo start camera action


        }

        else if (v == settingsButton){

            System.out.println("klikkede på to settings");

            FragmentManager fragmentManager2 = getChildFragmentManager();
            FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();

            Settings settings = new Settings();
            fragmentTransaction2.add(R.id.content_from_buttons, settings); //content_from_buttons er det område som fragmentet skal laves i.

            fragmentTransaction2.commit();

        }else if (v == helpButton){
            System.out.println("klikkede på to settings");

            FragmentManager fragmentManager3 = getChildFragmentManager();
            FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();

//            HelpFragment helpFragment = new Settings(); //todo opret help fragment
//            fragmentTransaction3.add(R.id.mainActFillFragment, settings); //mainActFillFragment er det område som fragmentet skal laves i.

            fragmentTransaction3.commit();

        }




    }
}