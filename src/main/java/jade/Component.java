package jade;

public abstract class Component {
    public transient GameObject gameObject;

    public void start() {}

    public void update(float deltaTime) {}

    public void imgui() {}
}
