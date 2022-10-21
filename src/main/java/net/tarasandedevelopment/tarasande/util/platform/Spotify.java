package net.tarasandedevelopment.tarasande.util.platform;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.util.ArrayList;

/*
 * Goal: Find the Spotify Instance
 * Read Display Title
 * Get Current Playing Title
 *
 * Developer from the future: This is bullshit, it doesn't work properly
 * It sometimes displays garbage data and sometimes just fucks up because it can
 */
public class Spotify {

    private static final PSApi PS_API = PSApi.INSTANCE;
    private static final Kernel32 KERNEL_32 = Kernel32.INSTANCE;
    private static final User32 USER_32 = User32.INSTANCE;

    public static String getTrack() {
        int[] processlist = new int[1024];
        PS_API.EnumProcesses(processlist, 1024, new int[1024]);
        ArrayList<String> names = new ArrayList<>();

        for (int pid : processlist) {
            Pointer ph = KERNEL_32.OpenProcess(0x0400 /*PROCESS_QUERY_INFORMATION*/ | 0x0010 /*PROCESS_VM_READ*/, false, pid);
            if (ph != null) {
                byte[] filename = new byte[512];
                PS_API.GetModuleBaseNameW(ph, new Pointer(0), filename, 512);
                String name = new String(filename).replace("\0", "");

                if (name.equals("Spotify.exe")) {
                    HWND hwnd = null;
                    do {
                        hwnd = USER_32.FindWindowExA(null, hwnd, null, null);
                        IntByReference currProcID = new IntByReference();
                        com.sun.jna.platform.win32.User32.INSTANCE.GetWindowThreadProcessId(hwnd, currProcID);
                        if (currProcID.getValue() == pid) {
                            final int requiredLength = com.sun.jna.platform.win32.User32.INSTANCE.GetWindowTextLength(hwnd) + 1;
                            final char[] title2 = new char[requiredLength];
                            final int length = com.sun.jna.platform.win32.User32.INSTANCE.GetWindowText(hwnd, title2, title2.length);
                            String jString = new String(title2).replace("\0", "").substring(0, length);
                            if (!jString.trim().isEmpty()) {
                                names.add(jString);
                            }
                        }
                    } while (hwnd != null);
                }

                KERNEL_32.CloseHandle(ph);
            }
        }
        return names.size() >= 3 ? names.get(2) : null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public interface PSApi extends StdCallLibrary {
        PSApi INSTANCE = Native.load("Psapi", PSApi.class);

        boolean EnumProcesses(int[] ProcessIDsOut, int size, int[] BytesReturned);

        WinDef.DWORD GetModuleBaseNameW(Pointer hProcess, Pointer hModule, byte[] lpBaseName, int nSize);
    }

    @SuppressWarnings("UnusedReturnValue")
    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("Kernel32", Kernel32.class);

        Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

        boolean CloseHandle(Pointer hObject);
    }

    @SuppressWarnings("UnusedReturnValue")
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("User32", User32.class);

        HWND FindWindowExA(HWND hwndParent, HWND childAfter, String className, String windowName);
    }
}
