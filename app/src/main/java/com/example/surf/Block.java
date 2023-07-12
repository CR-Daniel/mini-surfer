package com.example.surf;

import android.graphics.RectF;
import java.util.Random;

public class Block {
    public boolean isPassedPlayer = false;
    private RectF rect;
    private float speed;
    private float xPos, yPos; // X and Y Position of the block
    private int currentLane;

    public Block(int screenX, int screenY, int laneWidth, int numLanes, int lane) {
        // Use provided lane for the block to spawn in
        currentLane = lane;

        // Calculate the block's initial X position based on its lane
        xPos = laneWidth * currentLane + laneWidth / 2;
        yPos = 0;

        // Create a rect representing the block
        rect = new RectF(xPos, yPos, xPos + 100, yPos + 100); // Block with 100x100 pixels size
    }

    public RectF getRect() {
        return rect;
    }

    public int getLane() { return currentLane; }

    public void update(double deltaTime) {
        // Move the block down the screen
        speed = (float) (500 * deltaTime);
        yPos += speed;

        // Update the rect position according to block position
        rect.top = yPos;
        rect.bottom = yPos + 100;
    }
}
