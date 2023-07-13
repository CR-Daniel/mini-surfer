package com.example.surf;

import android.content.Context;
import android.graphics.RectF;

public class Player {
    private RectF rect;
    private float xPos, yPos; // X and Y Position of the cube
    private int currentLane;
    private int laneWidth;
    private int numLanes;
    private static final float GRAVITY = -200.0f;
    private static final float JUMP_STRENGTH = -200.0f;

    public Player(Context context, int screenX, int screenY, int laneWidth, int numLanes) {
        this.laneWidth = laneWidth;
        this.numLanes = numLanes;

        // Start the player in the middle lane
        currentLane = numLanes / 2;

        // Calculate the player's initial X position based on its lane
        xPos = laneWidth * currentLane + laneWidth / 2;
        yPos = screenY / 2;

        // Create a rect representing the player
        rect = new RectF(xPos, yPos, xPos + 100, yPos + 100); // Cube with 100x100 pixels size
    }

    public RectF getRect() {
        return rect;
    }

    public int getLane() { return currentLane; }

    public float getYPos() { return yPos; }

    public float getHeight() { return 100.0f; }

    public void update(double deltaTime) {
        // Always move down
        yPos -= GRAVITY * deltaTime;

        updatePosition();
    }

    public void updatePosition() {
        // Update the rect position according to player position
        rect.left = xPos;
        rect.top = yPos;
        rect.right = xPos + 100;
        rect.bottom = yPos + 100;
    }

    public void jump() {
        // Move up by a fixed amount
        yPos += JUMP_STRENGTH;
        update(0); // Update the player's position immediately
    }

    public void moveRight() {
        // Only move if not already in the rightmost lane
        if (currentLane < numLanes - 1) {
            currentLane++;
            xPos = laneWidth * currentLane + laneWidth / 2;
            updatePosition();
        }
    }

    public void moveLeft() {
        // Only move if not already in the leftmost lane
        if (currentLane > 0) {
            currentLane--;
            xPos = laneWidth * currentLane + laneWidth / 2;
            updatePosition();
        }
    }
}
