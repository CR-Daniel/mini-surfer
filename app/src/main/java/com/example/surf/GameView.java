package com.example.surf;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    double accumulatedTime = 0;
    private int highScore = 0;
    private int lastScore = 0;
    private SharedPreferences preferences;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private boolean isPlaying;
    private Thread gameThread;
    private int score = 0;
    private Paint scorePaint;
    private Player player; // Cube represents the player
    private ArrayList<Block> blocks; // This will hold your obstacles
    private Random random; // This is for randomizing obstacle positions
    private int screenX, screenY;
    private int laneWidth;
    private int numLanes = 3; // Define your desired number of lanes

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        preferences = context.getSharedPreferences("game", Context.MODE_PRIVATE);
        highScore = preferences.getInt("highScore", 0);
        lastScore = preferences.getInt("lastScore", 0);

        this.screenX = screenX;
        this.screenY = screenY;
        laneWidth = screenX / numLanes; // The width of a lane

        // Initialize your game objects here
        player = new Player(context, screenX, screenY, laneWidth, numLanes);
        blocks = new ArrayList<>();
        random = new Random();

        // Initialize the scorePaint
        scorePaint = new Paint();
        scorePaint.setColor(Color.GREEN);
        scorePaint.setTextSize(50);
    }

    public void run() {
        long lastTime = System.nanoTime();
        while (isPlaying) {
            long now = System.nanoTime();
            update((now - lastTime) / 1_000_000_000.0);
            draw();
            sleep();

            if (gameOver) {
                resetGame();
                gameOver = false;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            lastTime = now;
        }
    }


    private void update(double deltaTime) {
        accumulatedTime += deltaTime;

        if (!gameStarted) {
            return;
        }

        player.update(deltaTime);

        // End the game if the player goes off-screen
        if (player.getYPos() <= 0 || player.getYPos() + player.getHeight() >= screenY) {
            gameOver = true;
        }

        // Create a new obstacle every nth update
        if (accumulatedTime >= 0.75f) {
            int newBlockLane = random.nextInt(numLanes);
            blocks.add(new Block(screenX, screenY, laneWidth, numLanes, newBlockLane));
            accumulatedTime = 0;
        }

        Iterator<Block> blockIterator = blocks.iterator();
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            block.update(deltaTime);

            // Add a score point when the block passes the player
            if (!block.isPassedPlayer && block.getRect().top > player.getRect().bottom) {
                score++;
                block.isPassedPlayer = true;
            }

            // End the game if the player hits an obstacle
            if (player.getRect().intersect(block.getRect())) {
                gameOver = true;
            }

            // Remove the block if it has moved off the screen
            if (block.getRect().top > screenY) {
                blockIterator.remove();
            }
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            // clear the screen with black color
            canvas.drawColor(Color.BLACK);

            // Draw lines for each lane
            Paint linePaint = new Paint();
            linePaint.setColor(Color.WHITE);
            linePaint.setStrokeWidth(10);
            for (int i = 0; i < numLanes; i++) {
                float linePos = laneWidth * i + laneWidth / 2;
                canvas.drawLine(linePos, 0, linePos, screenY, linePaint);
            }

            // Define colors for the player and blocks
            Paint playerPaint = new Paint();
            playerPaint.setColor(Color.BLUE);
            Paint blockPaint = new Paint();
            blockPaint.setColor(Color.RED);

            // Define paint for the borders
            Paint borderPaint = new Paint();
            borderPaint.setColor(Color.WHITE);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(10);

            // Draw the player
            canvas.drawRect(player.getRect(), playerPaint);
            canvas.drawRect(player.getRect(), borderPaint);  // Draw player border

            // Draw the blocks
            for (Block block : blocks) {
                canvas.drawRect(block.getRect(), blockPaint);
                canvas.drawRect(block.getRect(), borderPaint);  // Draw block border
            }

            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.GRAY);

            // Draw the score
            if (gameStarted) {
                RectF scoreBackground = new RectF(0, 0, 300, 80);
                canvas.drawRect(scoreBackground, backgroundPaint);
                canvas.drawText("Score: " + score, 50, 50, scorePaint);
            } else {
                RectF scoreBackground = new RectF(0, 0, 400, 120);
                canvas.drawRect(scoreBackground, backgroundPaint);
                canvas.drawText("High Score: " + highScore, 50, 50, scorePaint);
                canvas.drawText("Last Score: " + lastScore, 50, 100, scorePaint);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resetGame() {
        lastScore = score;
        highScore = Math.max(score, highScore);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("highScore", highScore);
        editor.putInt("lastScore", lastScore);
        editor.apply();

        player = new Player(getContext(), screenX, screenY, laneWidth, numLanes);
        blocks.clear();
        gameStarted = false;
        score = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!gameStarted) {
                    gameStarted = true;
                    isPlaying = true;
                } else {
                    // Determine which half of the screen was touched
                    if (event.getX() < screenX / 2) {
                        // Move cube to left lane
                        player.moveLeft();
                    } else {
                        // Move cube to right lane
                        player.moveRight();
                    }
                    player.jump();
                }
                break;
        }
        return true;
    }
}
