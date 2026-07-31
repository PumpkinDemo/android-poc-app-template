package ave.mujica.poc.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.ArrayList
import java.util.Collections
import java.io.IOException

import android.os.IBinder
import android.util.Log
import android.os.Parcel
import android.os.Bundle
import android.os.Parcelable


object BinderUtils {
    private const val TAG = "BinderUtils"
    private const val BINDER_EXTENSION_TRANSACTION = 0x5F455854

    fun getServiceBinder(name: String): IBinder? {
        try {
            val smClass = Class.forName("android.os.ServiceManager")
            val getService = smClass.getDeclaredMethod("getService", String::class.java)
            return getService.invoke(null, name) as IBinder
        } catch (e: Exception) {
            Log.d(TAG, "getServiceBinder(" + name + ") failed: " + e.message)
            return null
        }
    }
    
    @Throws(Exception::class)
    fun getExtensionBinder(baseBinder: IBinder?): IBinder? {
        if (baseBinder == null) {
            return null
        }
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            val ok = baseBinder.transact(BINDER_EXTENSION_TRANSACTION, data, reply, 0)
            if (!ok) {
                return null
            }
            return reply.readStrongBinder()
        } finally {
            reply.recycle()
            data.recycle()
        }
    }
    
    @Throws(Exception::class)
    fun transactForReply(binder: IBinder?, descriptor: String, code: Int, writer: ParcelWriter?): Parcel {
        if (binder == null) {
            throw IllegalStateException("binder is null")
        }
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            data.writeInterfaceToken(descriptor)
            if (writer != null) {
                writer.write(data)
            }
            val ok = binder.transact(code, data, reply, 0)
            if (!ok) {
                throw IllegalStateException("transact returned false for code $code")
            }
            reply.readException()
            return reply
        } finally {
            data.recycle()
        }
    }
    
    fun formatBundle(bundle: Bundle?): String {
        if (bundle == null) {
            return "null"
        }
        val keys = ArrayList(bundle.keySet()).apply { sort() }
        val sb = StringBuilder()
        sb.append("Bundle{")
        if (keys.isNotEmpty()) {
            sb.append('\n')
        }
        for (i in keys.indices) {
            val key = keys[i]
            val value: Any? = bundle.get(key)
            sb.append("  ").append(key).append(" = ").append(formatValue(value))
            if (i + 1 < keys.size) {
                sb.append('\n')
            }
        }
        if (keys.isNotEmpty()) {
            sb.append('\n')
        }
        sb.append('}')
        return sb.toString()
    }

    
    private fun formatValue(value: Any?): String {
        if (value == null) {
            return "null"
        }
        if (value is Bundle) {
            return formatBundle(value)
        }
        if (value is ArrayList<*>) {
            return value.toString()
        }
        if (value is Array<*> && value.isArrayOf<Parcelable>()) {
            val values = ArrayList<String>()
            for (item in value) {
                values.add(item.toString())
            }
            return values.toString()
        }
        if (value is Array<*> && value.isArrayOf<String>()) {
           return value.contentToString()
        }
        return value.toString()
    }
    
    interface ParcelWriter {
        @Throws(IOException::class)
        fun write(data: Parcel)
    }
}