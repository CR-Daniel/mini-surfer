package com.example.surf;

import android.graphics.RectF;
import java.util.Random;

public class Block {
    private RectF rect;
    private float speed;
    private float xPos, yPos; // X and Y Position of the block
    private int currentLane;

    public Block(int screenX, int screenY, int laneWidth, int numLanes) {
        // Randomly select a lane for the block to spawn in
        Random random = new Random();
        currentLane = random.nextInt(numLanes);

        // Calculate the block's initial X position based on its lane
        xPos = laneWidth * currentLane + laneWidth / 2;
        yPos = 0;

        // Speed of the block
        speed = 10;

        // Create a rect representing the block
        rect = new RectF(xPos, yPos, xPos + 100, yPos + 100); // Block with 100x100 pixels size
    }

    public RectF getRect() {
        return rect;
    }

    public void update() {
        // Move the block down the screen
        yPos += speed;

        // Update the rect position according to block position
        rect.top = yPos;
        rect.bottom = yPos + 100;
    }
}
