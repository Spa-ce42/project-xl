package com.github.spa_ce42.projectxl3d.texture;

import com.github.spa_ce42.projectxl3d.core.Cleanable;

import java.util.HashMap;
import java.util.Map;

public class TextureCache implements Cleanable {
    private final Map<String, Texture> textureMap;
    private String defaultTexture;

    public TextureCache(String defaultTexture) {
        this.textureMap = new HashMap<>();

        if(defaultTexture != null) {
            this.textureMap.put(defaultTexture, new Texture(defaultTexture));
            this.defaultTexture = defaultTexture;
        }
    }

    @Override
    public void clean() {
        this.textureMap.values().forEach(Texture::clean);
    }

    public Texture createTexture(String texturePath) {
        return this.textureMap.computeIfAbsent(texturePath, Texture::new);
    }

    public Texture getTexture(String texturePath) {
        Texture texture = null;

        if(texturePath != null) {
            texture = this.textureMap.get(texturePath);
        }

        if(texture == null && this.defaultTexture != null) {
            texture = textureMap.get(this.defaultTexture);
        }

        return texture;
    }
}
