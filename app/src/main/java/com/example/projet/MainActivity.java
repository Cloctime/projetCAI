package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    }
    public void openNiveau() {
        Intent intent = new Intent(this, niveau.class);
        startActivity(intent);
    }
    public void openComparaison() {
        Intent intent = new Intent(this, comparaison.class);
        startActivity(intent);
    }

}
