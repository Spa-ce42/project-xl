package com.github.spa_ce42.projectxl3d.render;

import com.github.spa_ce42.projectxl3d.core.Window;
import com.github.spa_ce42.projectxl3d.model.Model;
import com.github.spa_ce42.projectxl3d.scene.Camera;
import com.github.spa_ce42.projectxl3d.model.Entity;
import com.github.spa_ce42.projectxl3d.model.Material;
import com.github.spa_ce42.projectxl3d.scene.Mesh;
import com.github.spa_ce42.projectxl3d.scene.Scene;
import com.github.spa_ce42.projectxl3d.texture.Texture;
import com.github.spa_ce42.projectxl3d.texture.TextureCache;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SceneRenderer implements Renderable {
    private final ShaderProgram shaderProgram;

    public SceneRenderer() {
        this.shaderProgram = new ShaderProgram(
                """
                        #version 460
                                        
                        layout(location = 0) in vec3 position;
                        layout(location = 1) in vec2 texCoord;
                                        
                        out vec2 outTextCoord;
                                        
                        uniform mat4 projectionMatrix;
                        uniform mat4 viewMatrix;
                        uniform mat4 modelMatrix;
                                        
                        void main()
                        {
                            gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
                            outTextCoord = texCoord;
                        }
                        """,
                """
                        #version 460
                                        
                        in vec2 outTextCoord;
                        out vec4 color;
                                                
                        struct Material {
                            vec4 diffuse;
                        };
                                        
                        uniform sampler2D textSampler;
                        uniform Material material;
                                        
                        void main() {
                            color = texture(textSampler, outTextCoord) + material.diffuse;
                        }
                                        
                        """
        );

        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("modelMatrix");
        this.shaderProgram.createUniform("textSampler");
        this.shaderProgram.createUniform("material.diffuse");
    }

    @Override
    public void render(Window window, Scene scene) {
        this.shaderProgram.bind();
        Camera camera = scene.getCamera();

        this.shaderProgram.uploadUniformMat4("viewMatrix", camera.getViewMatrix());
        this.shaderProgram.uploadUniformMat4("projectionMatrix", camera.getProjectionMatrix());
        this.shaderProgram.uploadUniformInt("textSampler", 0);

        Collection<Model> models = scene.getModels();
        TextureCache textureCache = scene.getTextureCache();

        for(Model model : models) {
            for(Material material : model.getMaterialList()) {
                this.shaderProgram.uploadUniformVec4("material.diffuse", material.getDiffuseColor());
                Texture texture = textureCache.getTexture(material.getTexturePath());

                if(texture != null) {
                    glActiveTexture(GL_TEXTURE0);
                    texture.bind();
                }

                for(Mesh mesh : material.getMeshList()) {
                    mesh.bind();

                    for(Entity entity : model.getEntitiesList()) {
                        this.shaderProgram.uploadUniformMat4("modelMatrix", entity.getModelMatrix());
                        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    @Override
    public void clean() {
        this.shaderProgram.clean();
    }
}
