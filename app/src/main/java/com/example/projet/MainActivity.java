package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button buttonNiveau;
    private Button buttonComparaison;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        buttonNiveau=(Button)findViewById(R.id.buttonNiveau);
        buttonNiveau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNiveau();
            }
        });
        buttonComparaison=(Button)findViewById(R.id.buttonComparaison);
        buttonComparaison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComparaison();
            }
        });
        Button buttonTestSensor = findViewById(R.id.buttonTestSensor);
        buttonTestSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Testsensor.class);
                startActivity(intent);
            }
        });
    }
    public void openNiveau() {
        Intent intent = new Intent(this, Level.class);
        startActivity(intent);
    }
    public void openComparaison() {
        Intent intent = new Intent(this, Connexion.class);
        startActivity(intent);
    }

}
