package com.example.myannotation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    NavController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Navigation.findNavController(MainActivity.this, R.id.fragment);
        controller.setGraph(R.navigation.nav_graph);
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt1:
                controller.navigate(R.id.fragmentA);
                break;
            case R.id.bt2:
                controller.navigate(R.id.fragmentB);
                break;
            case R.id.bt3:
                controller.navigate(R.id.fragmentC);
                break;
        }
    }
}