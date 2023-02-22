package tests;

import com.github.spa_ce42.projectxl3d.core.ApplicationLogic;
import com.github.spa_ce42.projectxl3d.core.Configuration;
import com.github.spa_ce42.projectxl3d.core.Engine;
import com.github.spa_ce42.projectxl3d.core.Window;
import com.github.spa_ce42.projectxl3d.event.MouseInput;
import com.github.spa_ce42.projectxl3d.model.Model;
import com.github.spa_ce42.projectxl3d.model.ModelLoader;
import com.github.spa_ce42.projectxl3d.render.Renderer;
import com.github.spa_ce42.projectxl3d.render.SceneRenderer;
import com.github.spa_ce42.projectxl3d.scene.Camera;
import com.github.spa_ce42.projectxl3d.model.Entity;
import com.github.spa_ce42.projectxl3d.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;

public class Tester implements ApplicationLogic {
    private Entity cubeEntity;

    @Override
    public void initialize(Window window, Scene scene, Renderer renderer) {
        window.setVisible(true);


        Camera camera = scene.getCamera();
        camera.setYaw((float)(3 * Math.PI / 2));
        camera.updateDirection();
        camera.updateView();

        Model cubeModel = ModelLoader.loadModel(
                "bp-model",
                "resources/backpack/backpack.obj",
                scene.getTextureCache()
        );

        scene.addModel(cubeModel);

        this.cubeEntity = new Entity("bp-entity", cubeModel.getId());
        this.cubeEntity.setPosition(0, 0, -2);
        this.cubeEntity.updateModelMatrix();
        scene.addEntity(this.cubeEntity);

        SceneRenderer sr = new SceneRenderer();
        renderer.addRenderable(sr);
    }
    
    private static final Vector3f UP = new Vector3f(0, 1, 0);
    private static final float CAMERA_SPEED = 0.25f;

    @Override
    public void input(Window window, Scene scene, float deltaMillis) {
        Camera camera = (Camera)scene.getCamera();
        Vector3f v = camera.getDirection();

        if(window.isKeyPressed(GLFW_KEY_W)) {
            camera.getPosition().add(new Vector3f(v).mul((float)(CAMERA_SPEED * deltaMillis)));
        }

        if(window.isKeyPressed(GLFW_KEY_S)) {
            camera.getPosition().sub(new Vector3f(v).mul((float)(CAMERA_SPEED * deltaMillis)));
        }

        if(window.isKeyPressed(GLFW_KEY_A)) {
            camera.getPosition().sub(new Vector3f(v).cross(UP).normalize().mul((float)(CAMERA_SPEED * deltaMillis)));
        }

        if(window.isKeyPressed(GLFW_KEY_D)) {
            camera.getPosition().add(new Vector3f(v).cross(UP).normalize().mul((float)(CAMERA_SPEED * deltaMillis)));
        }

        if(window.isKeyPressed(GLFW_KEY_F)) {
            window.setFullscreen(!window.isFullscreen(), glfwGetPrimaryMonitor());
        }

        float dYaw, dPitch, dRoll;
        dYaw = dPitch = dRoll = 0;

        if(window.isKeyPressed(GLFW_KEY_LEFT)) {
            dYaw = (float)(dYaw - 0.01745 * deltaMillis);
        }

        if(window.isKeyPressed(GLFW_KEY_RIGHT)) {
            dYaw = (float)(dYaw + 0.01745 * deltaMillis);
        }

        MouseInput mouseInput = window.getMouseInput();

        if(mouseInput.rightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            dYaw = dYaw + ((float)Math.toRadians(displVec.y * 0.1f));
            dPitch = dPitch + ((float)Math.toRadians(-displVec.x * 0.1f));
        }

        camera.addYaw(dYaw);
        camera.addPitch(dPitch);
        camera.addRoll(dRoll);
        camera.updateDirection();
        camera.updateView();
    }

    @Override
    public void update(Window window, Scene scene, float deltaMillis) {

    }

    @Override
    public void clean() {

    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        Tester logic = new Tester();
        Engine engine = new Engine(configuration, logic);
        engine.run();
        engine.clean();
    }
}
