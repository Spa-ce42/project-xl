package com.github.spa_ce42.projectxl3d.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final String id;
    private final List<Entity> entitiesList;
    private final List<Material> materialList;

    public Model(String id, List<Material> materialList) {
        this.id = id;
        this.materialList = materialList;
        this.entitiesList = new ArrayList<>();
    }

    public void clean() {
        this.materialList.forEach(Material::clean);
    }

    public List<Entity> getEntitiesList() {
        return this.entitiesList;
    }

    public String getId() {
        return this.id;
    }

    public List<Material> getMaterialList() {
        return this.materialList;
    }
}
