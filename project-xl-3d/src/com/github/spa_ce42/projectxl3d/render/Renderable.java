package com.github.spa_ce42.projectxl3d.render;

import com.github.spa_ce42.projectxl3d.core.Cleanable;
import com.github.spa_ce42.projectxl3d.core.Window;
import com.github.spa_ce42.projectxl3d.scene.Scene;

public interface Renderable extends Cleanable {
    void render(Window window, Scene scene);
}
