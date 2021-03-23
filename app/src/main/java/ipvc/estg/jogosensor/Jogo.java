package ipvc.estg.jogosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Jogo extends AppCompatActivity implements SensorEventListener {
    //sensors
    private SensorManager sensorManager;
    private Sensor acelerometer;
    private float yAccel;

    // Elementos
    private TextView scoreLabel, startLabel;
    private ImageView box, orange, pink, black;

    //Size max
    private int screenHeight, screenWidth;
    private int frameHeight;
    private int boxSize;

    //score
    private int score;

    // Posição
    private float boxY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;
    private float blackX, blackY;

    //speed
    private int boxSpeed, orangeSpeed, pinkSpeed, blackSpeed;

    //Timer
    private Timer timer = new Timer();
    private Handler handler = new Handler();

    private long lastSensorUpdateTime = 0;
    private boolean start_flg = false;

    //sound player
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);

        soundPlayer = new SoundPlayer(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        black = findViewById(R.id.black);

        //Initial Positions
        orange.setX(-80.0f);
        orange.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);

        scoreLabel.setText("Score : " + score);

        //screen size
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        boxSpeed = Math.round(screenHeight / 60.0f);
        orangeSpeed = Math.round(screenWidth / 60.0f);
        pinkSpeed = Math.round(screenWidth / 36.0f);
        blackSpeed = Math.round(screenWidth / 45.0f);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!start_flg) {
            start_flg = true;
            startLabel.setVisibility(View.GONE);

            // FrameHeight
            FrameLayout frameLayout = findViewById(R.id.frame);
            frameHeight = frameLayout.getHeight();

            // Box
            boxY = box.getY();
            boxSize = box.getHeight();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    });
                }
            }, 0, 30);

        }
        return super.onTouchEvent(event);
    }

    public void update() {

        hitCheck();

        //orange
        orangeX -= orangeSpeed;
        if(orangeX < 0){
            orangeX = screenWidth + 20;
            orangeY = (float)Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //Black
        blackX -= blackSpeed;
        if(blackX < 0){
            blackX = screenWidth + 10;
            blackY = (float)Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        // Pink

        pinkX -= pinkSpeed;
        if(pinkX < 0){
            pinkX = screenWidth + 5000;
            pinkY = (float)Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);


        //Player movement
        if (yAccel > 0.35) {
            boxY += boxSpeed;
        } else if (yAccel < -0.35) {
            boxY -= boxSpeed;
        } else if (yAccel <= -0.35 && yAccel >= 0.35) {
            boxY = 0;
        }

        if (boxY < 0) boxY = 0;
        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY((boxY));

        scoreLabel.setText("Score : " + score);

    }

    public void hitCheck(){
        //orange
        float orangeCenterX = orangeX + orange.getWidth() / 2.0f;
        float orangeCenterY = orangeY + orange.getHeight() /2.0f;

        if(0 <= orangeCenterX && orangeCenterX <= boxSize && boxY <= orangeCenterY
                && orangeCenterY <= boxY + boxSize){
            orangeX = -100.0f;
            score += 10;
            soundPlayer.playHitSound();
        }

        //Pink
        float pinkCenterX = pinkX + pink.getWidth() / 2.0f;
        float pinkCenterY = pinkY + pink.getHeight() / 2.0f;

        if(0 <= pinkCenterX && pinkCenterX <= boxSize && boxY <= pinkCenterY
                && pinkCenterY <= boxY + boxSize){

            blackX = -100.0f;
            score += 30;
            soundPlayer.playHitSound();
        }

        //Black
        float blackCenterX = blackX + black.getWidth() / 2.0f;
        float blackCenterY = blackY + black.getHeight() / 2.0f;

        if(0 <= blackCenterX && blackCenterX <= boxSize && boxY <= blackCenterY
                && blackCenterY <= boxY + boxSize){

            soundPlayer.playOverSound();

            if(timer != null){
                timer.cancel();
                timer = null;
            }

            //result activity
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastSensorUpdateTime) > 60) {
                lastSensorUpdateTime = currentTime;
                yAccel = sensorEvent.values[1];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
