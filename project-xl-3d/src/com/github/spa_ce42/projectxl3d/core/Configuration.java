package com.github.spa_ce42.projectxl3d.core;

public class Configuration {
    public int windowWidth;
    public int windowHeight;
    public String windowTitle;
    public int fps;
    public int ups;
    public boolean compatibleProfile;
    public String defaultTexturePath;
    public int glfwContextVersionMajor;
    public int glfwContextVersionMinor;

    public Configuration() {
        this.windowWidth = 1280;
        this.windowHeight = 720;
        this.windowTitle = "Program";
        this.fps = 60;
        this.ups = 20;
        this.compatibleProfile = false;
        this.defaultTexturePath = null;
        this.glfwContextVersionMajor = 4;
        this.glfwContextVersionMinor = 6;
    }
}
