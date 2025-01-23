package jade;

import com.google.gson.*;
import components.Component;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @java.lang.Override
    public GameObject deserialize(JsonElement jsonElement, java.lang.reflect.Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject gameObject = new GameObject(name);

        for (JsonElement component : components) {
            Component c = jsonDeserializationContext.deserialize(component, Component.class);
            gameObject.addComponent(c);
        }

        gameObject.transform = gameObject.getComponent(Transform.class);
        return gameObject;
    }
}
