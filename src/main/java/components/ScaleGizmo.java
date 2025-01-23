package components;

import editor.PropertiesWindow;
import jade.MouseListener;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void update(float deltaTime) {
        if (activeGameObject != null && xAxisActive && !yAxisActive) activeGameObject.transform.scale.x -= MouseListener.getWorldDx();
        if (activeGameObject != null && yAxisActive && !xAxisActive) activeGameObject.transform.scale.y -= MouseListener.getWorldDy();

        super.update(deltaTime);
    }
}
