package com.github.spa_ce42.projectxl3d.render;

import com.github.spa_ce42.projectxl3d.core.Cleanable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderProgram implements Cleanable {
    private final Map<String, Integer> uniformsMap;
    private int program;

    public ShaderProgram(String vertexSource, String fragmentSource) {
        this.uniformsMap = new HashMap<>();
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);

        {
            glShaderSource(vertexShader, vertexSource);
            glCompileShader(vertexShader);

            int[] isCompiled = {0};
            glGetShaderiv(vertexShader, GL_COMPILE_STATUS, isCompiled);
            if(isCompiled[0] == GL_FALSE) {
                glDeleteShader(vertexShader);
                return;
            }
        }

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        {
            glShaderSource(fragmentShader, fragmentSource);
            glCompileShader(fragmentShader);

            int[] isCompiled = {0};
            glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, isCompiled);
            if(isCompiled[0] == GL_FALSE) {
                glDeleteShader(fragmentShader);
                return;
            }
        }

        this.program = glCreateProgram();
        glAttachShader(this.program, vertexShader);
        glAttachShader(this.program, fragmentShader);
        glLinkProgram(this.program);

        int[] isLinked = {0};
        glGetProgramiv(this.program, GL_LINK_STATUS, isLinked);
        if(isLinked[0] == GL_FALSE) {
            glDeleteProgram(this.program);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            return;
        }

        glDetachShader(this.program, vertexShader);
        glDetachShader(this.program, fragmentShader);
    }

    public static ShaderProgram createFromResourcesUnchecked(Class<?> resourceSource, String vertexShaderResourcePath, String fragmentShaderResourcePath) {
        ShaderProgram s;

        try(InputStream is = resourceSource.getResourceAsStream(vertexShaderResourcePath);
            InputStream it = resourceSource.getResourceAsStream(fragmentShaderResourcePath)) {

            String vs = new String(Objects.requireNonNull(is).readAllBytes());
            String fs = new String(Objects.requireNonNull(it).readAllBytes());

            s = new ShaderProgram(vs, fs);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }

        return s;
    }

    public static ShaderProgram createFromFilesUnchecked(String vsf, String fsf) {
        try {
            return new ShaderProgram(
                    Files.readString(Path.of(vsf)),
                    Files.readString(Path.of(fsf))
            );
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static float[] toFloatArray(Matrix4f matrix4f) {
        float[] f = new float[16];
        return matrix4f.get(f);
    }

    private static float[] toFloatArray(Matrix3f matrix3f) {
        float[] f = new float[12];
        return matrix3f.get(f);
    }

    public void createUniform(String name) {
        int location = glGetUniformLocation(this.program, name);
        this.uniformsMap.put(name, location);
    }

    public void bind() {
        glUseProgram(this.program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void uploadUniformMat4(final String name, final Matrix4f matrix4f) {
        int location = this.uniformsMap.get(name);
        glUniformMatrix4fv(location, false, toFloatArray(matrix4f));
    }

    public void uploadUniformVec4(String name, float x, float y, float z, float w) {
        int location = this.uniformsMap.get(name);
        glUniform4f(location, x, y, z, w);
    }

    public void uploadUniformVec4(String name, Vector4f vector4f) {
        int location = this.uniformsMap.get(name);
        glUniform4f(location, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    public void uploadUniformInt(String name, int i) {
        int location = this.uniformsMap.get(name);
        glUniform1i(location, i);
    }

    public void uploadUniformFloat(String name, float f) {
        int location = this.uniformsMap.get(name);
        glUniform1f(location, f);
    }

    public void uploadUniformVec2(String name, Vector2f vector2f) {
        int location = this.uniformsMap.get(name);
        glUniform2f(location, vector2f.x, vector2f.y);
    }

    public void uploadUniformVec3(String name, Vector3f vector3f) {
        int location = this.uniformsMap.get(name);
        glUniform3f(location, vector3f.x, vector3f.y, vector3f.z);
    }

    public void uploadUniformVec3(String name, float[] f) {
        int location = this.uniformsMap.get(name);
        glUniform3f(location, f[0], f[1], f[2]);
    }

    public void uploadUniformMat3(String name, Matrix3f matrix3f) {
        int location = this.uniformsMap.get(name);
        glUniformMatrix3fv(location, false, toFloatArray(matrix3f));
    }

    @Override
    public void clean() {
        glDeleteProgram(this.program);
    }
}
