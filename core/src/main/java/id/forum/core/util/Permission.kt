package id.forum.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

val requestCodePermission = 101

val maxNumberRequestPermissions = 2

val permissions =
    listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
var permissionRequestCount: Int = 0

/** Permission Checking  */
fun checkAllPermissions(context: Context): Boolean {
    var hasPermissions = true
    for (permission in permissions) {
        hasPermissions = hasPermissions and isPermissionGranted(permission, context)
    }
    return hasPermissions
}

fun isPermissionGranted(permission: String, context: Context) =
    ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED