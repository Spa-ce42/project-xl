package com.github.spa_ce42.projectxl3d.event;

import com.github.spa_ce42.projectxl3d.core.Window;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class MouseInput {
    private final Window window;
    private final Vector2f displVec;
    private volatile float posX;
    private volatile float posY;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private volatile float posXOld;
    private volatile float posYOld;
    private boolean rightButtonPressed;

    public MouseInput(Window window) {
        this.window = window;
        this.posXOld = this.posYOld = -1;
        this.posX = this.posY = 0;
        this.displVec = new Vector2f();
        this.leftButtonPressed = false;
        this.rightButtonPressed = false;
        this.inWindow = false;

        window.setCursorPosCallback((window13, xpos, ypos) -> {
            MouseInput.this.posX = (float)xpos;
            MouseInput.this.posY = (float)ypos;
        });

        window.setCursorEnterCallback((window12, entered) -> MouseInput.this.inWindow = entered);

        window.setMouseButtonCallback((window1, button, action, mods) -> {
            MouseInput.this.leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            MouseInput.this.rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public Vector2f getCurrentPos() {
        return new Vector2f(this.posX, this.posY);
    }

    public Vector2f getDisplVec() {
        return this.displVec;
    }

    public void input() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.displVec.zero();

            if(this.posXOld > 0 && this.posYOld > 0) {
                double deltaX = this.posX - this.posXOld;
                double deltaY = this.posY - this.posYOld;

                boolean rotateX = deltaX != 0;
                boolean rotateY = deltaY != 0;

                if(rotateX) {
                    this.displVec.x = (float)deltaX;
                }

                if(rotateY) {
                    this.displVec.y = (float)deltaY;
                }
            }

            DoubleBuffer xPosBuf = stack.mallocDouble(1);
            DoubleBuffer yPosBuf = stack.mallocDouble(1);
            glfwGetCursorPos(this.window.getNativeWindow(), xPosBuf, yPosBuf);
            this.posX = (float)xPosBuf.get(0);
            this.posY = (float)yPosBuf.get(0);
            this.posXOld = this.posX;
            this.posYOld = this.posY;
        }
    }

    public boolean leftButtonPressed() {
        return this.leftButtonPressed;
    }

    public boolean rightButtonPressed() {
        return this.rightButtonPressed;
    }

    public boolean inWindow() {
        return this.inWindow;
    }
}
