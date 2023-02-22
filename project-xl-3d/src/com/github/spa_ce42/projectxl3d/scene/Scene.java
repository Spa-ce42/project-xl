package com.github.spa_ce42.projectxl3d.scene;

import com.github.spa_ce42.projectxl3d.core.Cleanable;
import com.github.spa_ce42.projectxl3d.model.Entity;
import com.github.spa_ce42.projectxl3d.model.Model;
import com.github.spa_ce42.projectxl3d.core.Configuration;
import com.github.spa_ce42.projectxl3d.texture.TextureCache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Scene implements Cleanable {
    private final Camera camera;
    private final Map<String, Model> models;
    private final TextureCache textureCache;

    public Scene(Configuration configuration) {
        this.camera = new Camera();
        this.models = new HashMap<>();
        this.resize(configuration.windowWidth, configuration.windowHeight);
        this.textureCache = new TextureCache(configuration.defaultTexturePath);
    }

    public TextureCache getTextureCache() {
        return this.textureCache;
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = models.get(modelId);

        if(model == null) {
            throw new IllegalStateException("Cannot find model with id: " + modelId);
        }

        model.getEntitiesList().add(entity);
    }

    public void removeEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = this.models.get(modelId);

        if(model == null) {
            throw new IllegalStateException("Cannot find model with id: " + modelId);
        }

        model.getEntitiesList().remove(entity);
    }

    public void removeModel(String modelId) {
        this.models.remove(modelId);
    }

    public void addModel(Model model) {
        this.models.put(model.getId(), model);
    }

    public Set<String> getIdSet() {
        return this.models.keySet();
    }

    public Collection<Model> getModels() {
        return this.models.values();
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void resize(int width, int height) {
        this.camera.resize(width, height);
    }

    @Override
    public void clean() {
        this.models.values().forEach(Model::clean);
    }
}
