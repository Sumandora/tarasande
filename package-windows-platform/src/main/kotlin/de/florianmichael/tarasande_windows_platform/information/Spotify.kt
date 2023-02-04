package de.florianmichael.tarasande_windows_platform.information

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary

/*
 * Goal: Find the Spotify Instance
 * Read Display Title
 * Get Current Playing Title
 *
 * Developer from the future: This is bullshit, it doesn't work properly
 * It sometimes displays garbage data and sometimes just fucks up because it can
 */
object Spotify {
    private val PS_API = PSApi.instance
    private val KERNEL_32 = Kernel32.instance
    private val USER_32 = User32.instance

    fun getTrack(): String? {
        val processlist = IntArray(1024)
        PS_API.EnumProcesses(processlist, 1024, IntArray(1024))
        val names = ArrayList<String>()
        for (pid in processlist) {
            val ph = KERNEL_32.OpenProcess(0x0400 /*PROCESS_QUERY_INFORMATION*/ or 0x0010 /*PROCESS_VM_READ*/, false, pid)
            if (ph != null) {
                val filename = ByteArray(512)
                PS_API.GetModuleBaseNameW(ph, Pointer(0), filename, 512)
                val name = String(filename).replace("\u0000", "")
                if (name == "Spotify.exe") {
                    var hwnd: HWND? = null
                    do {
                        hwnd = USER_32.FindWindowExA(null, hwnd, null, null)
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
                KERNEL_32.CloseHandle(ph)
            }
        }
        return if (names.size >= 3) names[2] else null
    }

    @Suppress("FunctionName")
    interface PSApi : StdCallLibrary {
        fun EnumProcesses(lpidProcess: IntArray?, cb: Int, lpcbNeeded: IntArray?): Boolean
        fun GetModuleBaseNameW(hProcess: Pointer?, hModule: Pointer?, lpBaseName: ByteArray?, nSize: Int): DWORD?

        companion object {
            val instance: PSApi = Native.load("Psapi", PSApi::class.java)
        }
    }

    @Suppress("FunctionName")
    interface Kernel32 : StdCallLibrary {
        fun OpenProcess(dwDesiredAccess: Int, bInheritHandle: Boolean, dwProcessId: Int): Pointer?
        fun CloseHandle(hObject: Pointer?): Boolean

        companion object {
            val instance: Kernel32 = Native.load("Kernel32", Kernel32::class.java)
        }
    }

    @Suppress("FunctionName")
    interface User32 : StdCallLibrary {
        fun FindWindowExA(hwndParent: HWND?, childAfter: HWND?, className: String?, windowName: String?): HWND?

        companion object {
            val instance: User32 = Native.load("User32", User32::class.java)
        }
    }
}