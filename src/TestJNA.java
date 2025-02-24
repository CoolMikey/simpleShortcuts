import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class TestJNA {
    public static void main(String[] args) {
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        char[] buffer = new char[1024];
        User32.INSTANCE.GetWindowText(hwnd, buffer, 1024);
        System.out.println("Active Window: " + Native.toString(buffer));
    }
}
