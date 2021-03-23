package ipvc.estg.jogosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sair(View view){
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    public void jogar(View view) {
        startActivity(new Intent(getApplicationContext(), Jogo.class));
    }

    public void sobre(View view) {
        startActivity(new Intent(getApplicationContext(), Sobre.class));
    }
}