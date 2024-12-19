package jade;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene() {
        System.out.println("Inside LevelEditorScene");
    }

    @Override
    public void update(float deltaTime) {

        System.out.println("" + (1.0f / deltaTime) + " FPS");

        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) changingScene = true;

        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= deltaTime;
            Window.get().red -= deltaTime * 0.5f;
            Window.get().green -= deltaTime * 0.5f;
            Window.get().blue -= deltaTime * 0.5f;
        } else if (changingScene) {
            Window.changeScene(1);
        }
    }
}
