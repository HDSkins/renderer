package de.hdskins.skinrenderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class GLContext {

    public static final int CANVAS_WIDTH = 512;
    public static final int CANVAS_HEIGHT = 832;

    private static final GLContext instance = new GLContext();

    private final long mainWindow;

    public static GLContext getInstance() {
        return instance;
    }

    private GLContext() {
        if (!glfwInit()) {
            ErrorHandling.checkGLFWError();
            throw new RuntimeException("Failed to initialize GLFW");
        }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_FALSE);

        this.mainWindow = this.createWindow("Main");
    }

    public long createWindow(String name) {
        long window = glfwCreateWindow(CANVAS_WIDTH, CANVAS_HEIGHT, "Visage v" + Visage.VERSION + " [" + name + "]", NULL, this.mainWindow);
        if (window == NULL) {
            ErrorHandling.checkGLFWError();
            throw new RuntimeException("Failed to create window");
        }

        return window;
    }

}
