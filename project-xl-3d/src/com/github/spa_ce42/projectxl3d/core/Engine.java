package com.github.spa_ce42.projectxl3d.core;

import com.github.spa_ce42.projectxl3d.event.WindowResizeCallback;
import com.github.spa_ce42.projectxl3d.render.Renderer;
import com.github.spa_ce42.projectxl3d.scene.Scene;

public class Engine {
    private final Window window;
    private final Renderer renderer;
    private final Scene scene;
    private final ApplicationLogic appLogic;
    private boolean running;
    private int targetFPS;
    private int targetUPS;

    public Engine(Configuration configuration, ApplicationLogic appLogic) {
        this.window = new Window(configuration);
        this.renderer = new Renderer(this.window);
        this.scene = new Scene(configuration);

        this.appLogic = appLogic;
        this.appLogic.initialize(this.window, this.scene, this.renderer);
        this.targetFPS = configuration.fps;
        this.targetUPS = configuration.ups;

        //noinspection Convert2Lambda
        this.window.addWindowResizeCallback(new WindowResizeCallback() {
            @Override
            public void onResize(Window window, int nWidth, int nHeight) {
                Engine.this.scene.resize(nWidth, nHeight);
            }
        });
    }

    private static long milliTime() {
        return System.nanoTime() / 1000000;
    }

    private void resize() {
        int width = window.getWidth();
        int height = window.getHeight();
        this.scene.resize(width, height);
    }

    public void run() {
        this.running = true;

        long initialTime = milliTime();
        float timeU = 1000.0f / this.targetUPS;
        float timeR = this.targetFPS > 0 ? 1000.0f / this.targetFPS : 0;
        float deltaUpdate = 0;
        float deltaFps = 0;
        long updateTime = initialTime;

        while(this.running && !this.window.shouldClose()) {
            this.window.pollEvents();

            long now = milliTime();
            deltaUpdate += (now - initialTime) / timeU;
            deltaFps += (now - initialTime) / timeR;

            if(this.targetFPS <= 0 || deltaFps >= 1) {
                this.appLogic.input(this.window, this.scene, now - initialTime);
            }

            if(deltaUpdate >= 1) {
                long diffTimeMillis = now - updateTime;
                this.appLogic.update(this.window, this.scene, diffTimeMillis);
                updateTime = now;
                deltaUpdate--;
            }

            if(this.targetFPS <= 0 || deltaFps >= 1) {
                this.renderer.render(this.window, this.scene);
                deltaFps--;
                this.window.swapBuffers();
            }
            initialTime = now;
        }

    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean flag) {
        this.running = flag;
    }

    public int getTargetFPS() {
        return this.targetFPS;
    }

    public void setTargetFPS(int nFPS) {
        this.targetFPS = nFPS;
    }

    public int getTargetUPS() {
        return this.targetUPS;
    }

    public void setTargetUPS(int nUPS) {
        this.targetUPS = nUPS;
    }

    public void clean() {
        this.scene.clean();
        this.renderer.clean();
        this.appLogic.clean();
        this.window.clean();
    }
}
