import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyboardHook implements NativeKeyListener {

    private boolean shortcutOpened = false;
    private final ShortcutListener shortcutListener = new ShortcutListener();

    public void startListening() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0 &&
                (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0 &&
                e.getKeyCode() == NativeKeyEvent.VC_H) {

            System.out.println("CTRL+ALT+H detected!");
            String appName = shortcutListener.getActiveWindowTitle();
            shortcutListener.displayShortcuts(appName);
            shortcutOpened = true;
        }

        if (shortcutOpened && e.getKeyCode() == NativeKeyEvent.VC_E) {
            System.out.println("CTRL+E detected - Opening editor");
            shortcutListener.handleEditRequest();
            shortcutOpened = false;
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}
