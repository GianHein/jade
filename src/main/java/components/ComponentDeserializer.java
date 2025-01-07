package components;

import com.google.gson.*;

public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @java.lang.Override
    public Component deserialize(JsonElement jsonElement, java.lang.reflect.Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String componentType = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return jsonDeserializationContext.deserialize(element, Class.forName(componentType));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + componentType, e);
        }
    }

    @java.lang.Override
    public JsonElement serialize(Component component, java.lang.reflect.Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("properties", jsonSerializationContext.serialize(component, component.getClass()));
        return result;
    }
}
