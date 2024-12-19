package jade;

public class LevelScene extends Scene {
    LevelScene() {
        System.out.println("Inside LevelScene");
    }

    @Override
    public void update(float deltaTime) {
        Window.get().red = 1.0f;
        Window.get().green = 1.0f;
        Window.get().blue = 1.0f;
    }
}
