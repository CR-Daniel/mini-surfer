package com.example.surf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    private boolean isPlaying;
    private Thread gameThread;
    private Paint paint;
    private Player player; // Cube represents the player
    private ArrayList<Block> blocks; // This will hold your obstacles
    private Random random; // This is for randomizing obstacle positions
    private int screenX, screenY;
    private int laneWidth;
    private int numLanes = 5; // Define your desired number of lanes

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.screenX = screenX;
        this.screenY = screenY;
        laneWidth = screenX / numLanes; // The width of a lane

        // Initialize your game objects here
        player = new Player(context, screenX, screenY, laneWidth, numLanes);
        blocks = new ArrayList<>();
        random = new Random();

        // For drawing
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        player.update();

        // Create a new obstacle every nth update
        if (new Random().nextInt(100) > 95) { // adjust as needed
            blocks.add(new Block(screenX, screenY, laneWidth, numLanes));
        }

        Iterator<Block> blockIterator = blocks.iterator();
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            block.update();

            // End the game if the player hits an obstacle
            if (player.getRect().intersect(block.getRect())) {
                isPlaying = false;
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

            // Define colors for the player and blocks
            Paint playerPaint = new Paint();
            playerPaint.setColor(Color.BLUE);
            Paint blockPaint = new Paint();
            blockPaint.setColor(Color.RED);

            // Define paint for the borders
            Paint borderPaint = new Paint();
            borderPaint.setColor(Color.WHITE);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(4);

            // Draw the player
            canvas.drawRect(player.getRect(), playerPaint);
            //canvas.drawRect(player.getRect(), borderPaint);  // Draw player border

            // Draw the blocks
            for (Block block : blocks) {
                canvas.drawRect(block.getRect(), blockPaint);
                //canvas.drawRect(block.getRect(), borderPaint);  // Draw block border
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Determine which half of the screen was touched
                if (event.getX() < screenX / 2) {
                    // Move cube to left lane
                    player.moveLeft();
                } else {
                    // Move cube to right lane
                    player.moveRight();
                }
                break;
        }
        return true;
    }
}
