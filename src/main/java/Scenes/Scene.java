package Scenes;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import imgui.ImGui;
import jade.Camera;
import jade.GameObject;
import jade.GameObjectDeserializer;
import renderer.Renderer;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean loadedLevel = false;

    public Scene() {}

    public void init() {}

    public void start() {
        for (GameObject gameObject : gameObjects) {
            gameObject.start();
            this.renderer.add(gameObject);
        }

        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject) {
        if (!isRunning) {
            gameObjects.add(gameObject);
        }

        if (isRunning) {
            gameObjects.add(gameObject);
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == gameObjectId).findFirst();
        return result.orElse(null);
    }

    public abstract void update(float deltaTime);
    public abstract void render();

    public Camera getCamera() {
        return this.camera;
    }

    public void imgui() {

    }

    public void saveExit() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");

            List<GameObject> objsToSerialize = new ArrayList<>();

            for (GameObject object : this.gameObjects) {
                if (object.doSerialization()) objsToSerialize.add(object);
            }

            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            // e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGameObjectId = -1;
            int maxComponentId = -1;

            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i=0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for (Component component : objs[i].getAllComponents()) {
                    if (component.getUid() > maxComponentId) maxComponentId = component.getUid();
                }

                if (objs[i].getUid() > maxGameObjectId) maxGameObjectId = objs[i].getUid();
            }

            maxComponentId++;
            maxGameObjectId++;
            GameObject.init(maxGameObjectId);
            Component.init(maxComponentId);
            this.loadedLevel = true;
        }
    }
}