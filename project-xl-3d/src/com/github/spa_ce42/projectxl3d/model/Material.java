package com.github.spa_ce42.projectxl3d.model;

import com.github.spa_ce42.projectxl3d.core.Cleanable;
import com.github.spa_ce42.projectxl3d.scene.Mesh;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Material implements Cleanable {
    private final List<Mesh> meshList;
    private String texturePath;
    private Vector4f diffuseColor;

    public Material() {
        this.meshList = new ArrayList<>();
    }

    public List<Mesh> getMeshList() {
        return this.meshList;
    }

    public String getTexturePath() {
        return this.texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    @Override
    public void clean() {
        this.meshList.forEach(Mesh::clean);
    }

    public Vector4f getDiffuseColor() {
        return this.diffuseColor;
    }

    public void setDiffuseColor(Vector4f vector4f) {
        this.diffuseColor = vector4f;
    }

    public void setDefaultDiffuseColor() {
        this.diffuseColor = new Vector4f(0, 0, 0, 1);
    }
}
