package com.github.spa_ce42.projectxl3d.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Camera {
    private Matrix4f view;
    private Matrix4f projection;

    private Vector3f position;
    private float yaw;
    private float pitch;
    private float roll;
    private Vector3f direction;
    private Vector3f up;

    private float fov;
    private float aspectRatio;
    private float nearClip;
    private float farClip;

    public Camera(Vector3f position, Vector3f up, float fov, float aspectRatio, float nearClip, float farClip, float yaw, float pitch, float roll) {
        this.position = position;
        this.direction = new Vector3f();
        this.up = up;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearClip = nearClip;
        this.farClip = farClip;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.updateDirection();
        this.updateView();
        this.updateProjection();
    }

    public Camera() {
        this.position = new Vector3f();
        this.direction = new Vector3f();
        this.up = new Vector3f(0, 1, 0);
        this.fov = (float)Math.PI / 2;
        this.aspectRatio = 1280f / 720f;
        this.nearClip = 0.1f;
        this.farClip = 1000f;
        this.yaw = this.pitch = this.roll = 0;
        this.updateDirection();
        this.updateView();
        this.updateProjection();
    }

    public void updateView() {
        this.view = new Matrix4f().lookAt(
                this.position,
                new Vector3f(this.position).add(this.direction),
                this.up
        );
    }

    public void updateProjection() {
        this.projection = new Matrix4f().perspective(
                this.fov,
                this.aspectRatio,
                this.nearClip,
                this.farClip
        );
    }

    public Matrix4f getView() {
        return this.view;
    }

    public Matrix4f getProjection() {
        return this.projection;
    }

    public void addYaw(float yaw) {
        this.yaw = this.yaw + yaw;
    }

    public void addPitch(float pitch) {
        this.pitch = this.pitch + pitch;
    }

    public void addRoll(float row) {
        this.roll = this.roll + row;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return this.roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void updateDirection() {
        this.direction.x = (float)(cos(this.yaw) * cos(this.pitch));
        this.direction.y = (float)sin(this.pitch);
        this.direction.z = (float)(sin(this.yaw) * cos(this.pitch));
        this.direction.normalize();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getUp() {
        return up;
    }

    public void setUp(Vector3f up) {
        this.up = up;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getNearClip() {
        return nearClip;
    }

    public void setNearClip(float nearClip) {
        this.nearClip = nearClip;
    }

    public float getFarClip() {
        return farClip;
    }

    public void setFarClip(float farClip) {
        this.farClip = farClip;
    }

    public void resize(int width, int height) {
        this.setAspectRatio((float)width / (float)height);
        this.updateProjection();
    }

    public Matrix4f getViewMatrix() {
        return this.view;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projection;
    }
}
