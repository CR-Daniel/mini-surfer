package com.example.surf;

import android.content.Context;
import android.graphics.RectF;

public class Player {
    private RectF rect;
    private float speed;
    private float xPos, yPos; // X and Y Position of the cube
    private int currentLane;
    private int laneWidth;
    private int numLanes;

    public Player(Context context, int screenX, int screenY, int laneWidth, int numLanes) {
        this.laneWidth = laneWidth;
        this.numLanes = numLanes;

        // Start the player in the middle lane
        currentLane = numLanes / 2;

        // Calculate the player's initial X position based on its lane
        xPos = laneWidth * currentLane + laneWidth / 2;
        yPos = screenY / 2;

        // Speed of the player
        speed = 10;

        // Create a rect representing the player
        rect = new RectF(xPos, yPos, xPos + 100, yPos + 100); // Cube with 100x100 pixels size
    }

    public RectF getRect() {
        return rect;
    }

    public int getLane() { return currentLane; }

    public void update() {
        // Update the rect position according to player position
        rect.left = xPos;
        rect.top = yPos;
        rect.right = xPos + 100;
        rect.bottom = yPos + 100;
    }

    public void moveRight() {
        // Only move if not already in the rightmost lane
        if (currentLane < numLanes - 1) {
            currentLane++;
            xPos = laneWidth * currentLane + laneWidth / 2;
            update(); // Add this line
        }
    }

    public void moveLeft() {
        // Only move if not already in the leftmost lane
        if (currentLane > 0) {
            currentLane--;
            xPos = laneWidth * currentLane + laneWidth / 2;
            update(); // Add this line
        }
    }
}
