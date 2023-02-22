package com.github.spa_ce42.projectxl3d.core;

import com.github.spa_ce42.projectxl3d.event.MouseInput;
import com.github.spa_ce42.projectxl3d.event.WindowResizeCallback;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_COMPAT_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowContentScale;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Cleanable {
    private final long window;
    private final MouseInput mouseInput;
    private final List<WindowResizeCallback> windowResizeCallbacks;
    private int width;
    private int height;
    private int xPos;
    private int yPos;
    private int oldWidth;
    private int oldHeight;
    private int oldXPos;
    private int oldYPos;
    private String title;
    private boolean visible;
    private boolean fullscreen;

    public Window(Configuration configuration) {
        if(!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, configuration.glfwContextVersionMajor);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, configuration.glfwContextVersionMinor);

        if(configuration.compatibleProfile) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        }

        if(!configuration.compatibleProfile) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }

        if(configuration.windowWidth > 0 && configuration.windowHeight > 0) {
            this.width = configuration.windowWidth;
            this.height = configuration.windowHeight;
        } else {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if(vidMode != null) {
                this.width = vidMode.width();
                this.height = vidMode.height();
            }
        }


        this.title = configuration.windowTitle;

        this.window = glfwCreateWindow(this.width, this.height, configuration.windowTitle, NULL, NULL);

        if(this.window == NULL) {
            throw new IllegalStateException("Failed to create a GLFW window.");
        }

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        Objects.requireNonNull(videoMode);

        glfwSetWindowPos(
                this.window,
                (videoMode.width() - this.width) / 2,
                (videoMode.height() - this.height) / 2
        );


        glfwSetFramebufferSizeCallback(this.window, (window, width, height) -> Window.this.onResize(width, height));
        this.windowResizeCallbacks = new ArrayList<>();

        glfwSetErrorCallback((error, description) -> System.err.println(MemoryUtil.memUTF8(description)));

        glfwSetKeyCallback(this.window, (window, key, scancode, action, mods) -> {

        });

        glfwSetWindowPosCallback(this.window, (window, xPos, yPos) -> {
            Window.this.xPos = xPos;
            Window.this.yPos = yPos;
        });

        glfwMakeContextCurrent(this.window);
        glfwSwapInterval(configuration.fps > 0 ? 0 : 1);

        this.mouseInput = new MouseInput(this);
    }

    public void setFullscreen(boolean flag, long monitor) {
        if(this.fullscreen == flag) {
            return;
        }

        if(flag) {
            GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            Objects.requireNonNull(vidMode);
            int nWidth = vidMode.width();
            int nHeight = vidMode.height();
            this.oldWidth = this.width;
            this.oldHeight = this.height;
            this.oldXPos = this.xPos;
            this.oldYPos = this.yPos;
            this.width = nWidth;
            this.height = nHeight;
            glfwSetWindowMonitor(this.window, monitor, 0, 0, nWidth, nHeight, 0);
            this.onResize(nWidth, nHeight);
            this.fullscreen = true;
            return;
        }

        glfwSetWindowMonitor(this.window, NULL, 0, 0, this.oldWidth, this.oldHeight, 0);
        this.width = this.oldWidth;
        this.height = this.oldHeight;
        this.xPos = this.oldXPos;
        this.yPos = this.oldYPos;
        this.onResize(this.width, this.height);
        glfwSetWindowPos(this.window, this.xPos, this.yPos);
        this.fullscreen = false;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public MouseInput getMouseInput() {
        return this.mouseInput;
    }

    private void onResize(int nWidth, int nHeight) {
        this.width = nWidth;
        this.height = nHeight;
        glViewport(0, 0, this.width, this.height);

        for(WindowResizeCallback wrc : this.windowResizeCallbacks) {
            wrc.onResize(this, nWidth, nHeight);
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean flag) {
        if(this.visible == flag) {
            return;
        }

        this.visible = flag;

        if(this.visible) {
            glfwShowWindow(this.window);
            return;
        }

        glfwHideWindow(this.window);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(this.window, title);
        this.title = title;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(this.window, keyCode) == GLFW_PRESS;
    }

    public void pollEvents() {
        this.mouseInput.input();
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(this.window);
    }

    public void setWindowPosition(int x, int y) {
        glfwSetWindowPos(this.window, x, y);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(this.window);
    }

    public void setWindowShouldClose(boolean flag) {
        glfwSetWindowShouldClose(this.window, flag);
    }

    @Override
    public void clean() {
        glfwFreeCallbacks(this.window);
        glfwDestroyWindow(this.window);
    }

    public void setCursorPosCallback(GLFWCursorPosCallbackI cursorPosCallback) {
        glfwSetCursorPosCallback(this.window, cursorPosCallback);
    }

    public void setCursorEnterCallback(GLFWCursorEnterCallbackI cursorEnterCallback) {
        glfwSetCursorEnterCallback(this.window, cursorEnterCallback);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI mouseButtonCallback) {
        glfwSetMouseButtonCallback(this.window, mouseButtonCallback);
    }

    public long getNativeWindow() {
        return this.window;
    }

    public void addWindowResizeCallback(WindowResizeCallback windowResizeCallback) {
        this.windowResizeCallbacks.add(windowResizeCallback);
    }

    public void removeWindowResizeCallback(WindowResizeCallback windowResizeCallback) {
        this.windowResizeCallbacks.remove(windowResizeCallback);
    }

    public GLFWVidMode getGLFWVideoMode() {
        return glfwGetVideoMode(this.window);
    }

    public Vector2f getWindowContentScale() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xScale = stack.mallocFloat(1);
            FloatBuffer yScale = stack.mallocFloat(1);
            glfwGetWindowContentScale(this.window, xScale, yScale);
            return new Vector2f(xScale.get(0), yScale.get(0));
        }
    }
}
