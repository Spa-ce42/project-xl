package com.github.spa_ce42.projectxl3d.core;

import com.github.spa_ce42.projectxl3d.render.Renderer;
import com.github.spa_ce42.projectxl3d.scene.Scene;

public interface ApplicationLogic extends Cleanable {
    void initialize(Window window, Scene scene, Renderer renderer);

    void input(Window window, Scene scene, float deltaMillis);

    void update(Window window, Scene scene, float deltaMillis);

    void clean();
}
