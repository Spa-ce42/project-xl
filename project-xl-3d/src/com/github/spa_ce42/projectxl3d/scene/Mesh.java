package com.github.spa_ce42.projectxl3d.scene;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    private final int vertexCount;
    private final int vertexArrayId;
    private final List<Integer> vertexBufferIds;

    public Mesh(float[] positions, float[] textCoords, int[] indices) {
        this.vertexCount = indices.length;
        this.vertexBufferIds = new ArrayList<>();
        this.vertexArrayId = glGenVertexArrays();
        glBindVertexArray(this.vertexArrayId);

        //Positions
        int vboId = this.createVertexBuffer();
        FloatBuffer positionsBuffer = BufferUtils.createFloatBuffer(positions.length);
        positionsBuffer.put(0, positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //Texture coordinates
        vboId = glGenBuffers();
        vertexBufferIds.add(vboId);
        FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
        textCoordsBuffer.put(0, textCoords);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        //Indices
        vboId = glGenBuffers();
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(0, indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private int createVertexBuffer() {
        int i = glGenBuffers();
        this.vertexBufferIds.add(i);
        return i;
    }

    public int getMode() {
        return GL_TRIANGLES;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public void bind() {
        glBindVertexArray(this.vertexArrayId);
    }

    public void clean() {
        for(int i : this.vertexBufferIds) {
            glDeleteBuffers(i);
        }

        glDeleteVertexArrays(this.vertexArrayId);
    }
}
