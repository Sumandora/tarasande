package su.mandora.tarasande.util.spotify

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import java.util.function.Consumer
import java.util.regex.Pattern

object Spotify {
    private val psApi = PSApi.INSTANCE
    private val kernel32 = Kernel32.INSTANCE
    private val user32 = User32.INSTANCE

    private val pattern = Pattern.compile("([\\s\\S]*-[\\s\\S]*)")

    private val callbacks = ArrayList<Consumer<String>>()

    private var currentTrack: String? = null
    private var lookupThread: Thread? = null

    fun addCallback(spotifyCallback: Consumer<String>) {
        if (lookupThread == null) {
            setTrack()
            lookupThread = Thread {
                while (true) {
                    setTrack()
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            lookupThread?.start()
        }

        callbacks.add(spotifyCallback)
    }

    private fun setTrack() {
        val processlist = IntArray(1024)
        psApi.EnumProcesses(processlist, 1024, IntArray(1024))
        val names = ArrayList<String>()
        for (pid in processlist) {
            val ph: Pointer? = kernel32.OpenProcess(0x0400 /*PROCESS_QUERY_INFORMATION*/ or 0x0010 /*PROCESS_VM_READ*/, false, pid)
            if (ph != null) {
                val filename = ByteArray(512)
                psApi.GetModuleBaseNameW(ph, Pointer(0), filename, 512)
                val name = String(filename).replace("\u0000", "")
                if (name == "Spotify.exe") {
                    var hwnd: HWND? = null
                    do {
                        hwnd = user32.FindWindowExA(null, hwnd, null, null)
                        val currProcID = IntByReference()
                        com.sun.jna.platform.win32.User32.INSTANCE.GetWindowThreadProcessId(hwnd, currProcID)
                        if (currProcID.value == pid) {
                            val requiredLength = com.sun.jna.platform.win32.User32.INSTANCE.GetWindowTextLength(hwnd) + 1
                            val title2 = CharArray(requiredLength)
                            val length = com.sun.jna.platform.win32.User32.INSTANCE.GetWindowText(hwnd, title2, title2.size)
                            val jString = String(title2).replace("\u0000", "").substring(0, length)
                            if (jString.trim { it <= ' ' }.isNotEmpty()) {
                                names.add(jString)
                            }
                        }
                    } while (hwnd != null)
                }
                kernel32.CloseHandle(ph)
            }
        }

        var newTrack: String? = null
        for (name in names) {
            if (pattern.asPredicate().test(name)) {
                newTrack = name
                break
            }
        }

        if (newTrack != null && newTrack != currentTrack) {
            callbacks.forEach { it.accept(newTrack) }
            currentTrack = newTrack
        }
    }
}

private interface PSApi : StdCallLibrary {
    fun EnumProcesses(ProcessIDsOut: IntArray?, size: Int, BytesReturned: IntArray?): Boolean
    fun GetModuleBaseNameW(hProcess: Pointer?, hModule: Pointer?, lpBaseName: ByteArray?, nSize: Int): DWORD?

    companion object {
        val INSTANCE = Native.load("Psapi", PSApi::class.java) as PSApi
    }
}

private interface Kernel32 : StdCallLibrary {
    fun OpenProcess(dwDesiredAccess: Int, bInheritHandle: Boolean, dwProcessId: Int): Pointer?
    fun CloseHandle(hObject: Pointer?): Boolean

    companion object {
        val INSTANCE = Native.load("Kernel32", Kernel32::class.java) as Kernel32
    }
}

private interface User32 : StdCallLibrary {
    fun FindWindowExA(hwndParent: HWND?, childAfter: HWND?, className: String?, windowName: String?): HWND?

    companion object {
        val INSTANCE = Native.load("User32", User32::class.java) as User32
    }
}