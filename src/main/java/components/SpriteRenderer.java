package components;

import jade.Component;

public class SpriteRenderer extends Component {
    private boolean startedUpdateCycle = false;

    @Override
    public void start() {
        System.out.println("I am starting!");
    }

    @Override
    public void update(float deltaTime) {
        if (!startedUpdateCycle) {
            System.out.println("I am updating!");
            startedUpdateCycle = true;
        }
    }
}
