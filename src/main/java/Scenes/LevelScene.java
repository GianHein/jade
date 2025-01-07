package Scenes;

import jade.Window;

public class LevelScene extends Scene {
    public LevelScene() {
        System.out.println("Inside LevelScene");
    }

    @Override
    public void update(float deltaTime) {
        Window.get().red = 1.0f;
        Window.get().green = 1.0f;
        Window.get().blue = 1.0f;
    }
}
