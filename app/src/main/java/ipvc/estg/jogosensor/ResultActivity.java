package ipvc.estg.jogosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreLabel = findViewById(R.id.scoreLabel);
        TextView highScoreLabel = findViewById(R.id.highScoreLabel);

        int score = getIntent().getIntExtra("SCORE", 0);
        scoreLabel.setText(score + "");

        //High Score
        SharedPreferences sharedPreferences = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore = sharedPreferences.getInt("HIGH_SCORE", 0);

        if(score > highScore){
            //Dá update no highscore
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.apply();

            highScoreLabel.setText("High Score : " + score);
        } else{
            highScoreLabel.setText("High Score : " + highScore);
        }
    }

    public void tryAgain(View view){
        startActivity(new Intent(getApplicationContext(), Jogo.class));
    }

    public void menu(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}