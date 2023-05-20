package mx.unam.fciencias.medfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /** Barra menu */
    DrawerLayout drawerLayout;

    /** Icono menu hamburguesa */
    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelecFiltroFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_selec_filtro);
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Abriendo el fragmento correspondiente
        switch (item.getItemId()) {

            case R.id.nav_selec_filtro: // Pantalla de seleccion de filtro
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelecFiltroFragment()).commit();
                break;

            case R.id.nav_creditos: // Pantalla de creditos
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AcercaDeFragment()).commit();
                break;
        }

        // Cerrando el menu
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;

    }

    @Override
    public void onBackPressed() {

        // Si esta abierto el menu
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();

        }
    }
}