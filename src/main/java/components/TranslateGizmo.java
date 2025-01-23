package components;

import editor.PropertiesWindow;
import jade.MouseListener;

public class TranslateGizmo extends Gizmo {
    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void update(float deltaTime) {
       if (activeGameObject != null && xAxisActive && !yAxisActive) activeGameObject.transform.position.x -= MouseListener.getWorldDx();
       if (activeGameObject != null && yAxisActive && !xAxisActive) activeGameObject.transform.position.y -= MouseListener.getWorldDy();

       super.update(deltaTime);
    }
}
