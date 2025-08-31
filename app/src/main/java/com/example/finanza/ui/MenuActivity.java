package com.example.finanza.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.finanza.R;
public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Botão Home: volta para MainActivity (sem animação)
        ImageView navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, com.example.finanza.MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // REMOVE ANIMAÇÃO
            finish();
        });

        // Botão Menu: só destaca
        ImageView navMenu = findViewById(R.id.nav_menu);
        navMenu.setOnClickListener(v -> {
            // Já está na tela de menu
        });

        highlightBottomNav();
    }

    private void highlightBottomNav() {
        ImageView navMenu = findViewById(R.id.nav_menu);
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMovements = findViewById(R.id.nav_movements);
        ImageView navAccounts = findViewById(R.id.nav_accounts);

        navMenu.setColorFilter(ContextCompat.getColor(this, R.color.accentBlue));
        navHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navMovements != null)
            navMovements.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navAccounts != null)
            navAccounts.setColorFilter(ContextCompat.getColor(this, R.color.white));
    }
}