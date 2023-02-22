package com.github.spa_ce42.projectxl3d.render;

import com.github.spa_ce42.projectxl3d.core.Cleanable;
import com.github.spa_ce42.projectxl3d.core.Window;
import com.github.spa_ce42.projectxl3d.scene.Scene;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;

public class Renderer implements Renderable {
    private final List<Renderable> renderables;

    public Renderer(Window window) {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0, 0, 0, 1);
        this.renderables = new ArrayList<>();
    }

    public void addRenderable(Renderable renderable) {
        this.renderables.add(renderable);
    }

    public void removeRenderable(Renderable renderable) {
        this.renderables.remove(renderable);
    }

    @Override
    public void render(Window window, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for(Renderable r : this.renderables) {
            r.render(window, scene);
        }
    }

    @Override
    public void clean() {
        this.renderables.forEach(Cleanable::clean);
    }
}
