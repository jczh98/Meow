package top.rechinx.meow.support.log

import android.util.Log
import top.rechinx.meow.BuildConfig

object L {

    private var TAG = "ReLog"

    fun e(msg: String?) {
        if(BuildConfig.DEBUG) {

            if(msg != null && msg.isNotEmpty()) {

                val s = getMethodNames()

                if(msg.contains("\n")) {
                    Log.e(TAG, String.format(s, msg.replace("\n".toRegex(), "\\n║ ")))
                } else {
                    Log.e(TAG, String.format(s, msg))
                }
            }
        }
    }

    fun d(msg: String?) {
        if(BuildConfig.DEBUG) {

            if(msg != null && msg.isNotEmpty()) {

                val s = getMethodNames()

                if(msg.contains("\n")) {
                    Log.d(TAG, String.format(s, msg.replace("\n".toRegex(), "\\n║ ")))
                } else {
                    Log.d(TAG, String.format(s, msg))
                }
            }
        }
    }

    private fun getMethodNames(): String {
        val sElements = Thread.currentThread().stackTrace

        var stackOffset = LoggerPrinter.getStackOffset(sElements)

        stackOffset++
        val builder = StringBuilder()

        builder.append("  ").append(LoggerPrinter.BR).append(LoggerPrinter.TOP_BORDER).append(LoggerPrinter.BR)

        // current thread
        builder.append("║ " + "Thread: " + Thread.currentThread().name).append(LoggerPrinter.BR)
                .append(LoggerPrinter.MIDDLE_BORDER).append(LoggerPrinter.BR)
                .append("║ ")
                .append(sElements[stackOffset].className)
                .append(".")
                .append(sElements[stackOffset].methodName)
                .append(" ")
                .append(" (")
                .append(sElements[stackOffset].fileName)
                .append(":")
                .append(sElements[stackOffset].lineNumber)
                .append(")")
                .append(LoggerPrinter.BR)
                .append(LoggerPrinter.MIDDLE_BORDER).append(LoggerPrinter.BR)
                // log information
                .append("║ ").append("%s").append(LoggerPrinter.BR)
                .append(LoggerPrinter.BOTTOM_BORDER).append(LoggerPrinter.BR)

        return builder.toString()
    }
}