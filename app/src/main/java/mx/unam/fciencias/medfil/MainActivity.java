package mx.unam.fciencias.medfil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}