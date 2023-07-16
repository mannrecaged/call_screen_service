package com.kadvis.call_screen_service_example

import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {

    companion object {
        private const val REQUEST_PERMISSIONS = 1337
        private const val REQUEST_ROLE = 2333
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_PHONE_NUMBERS
            ), REQUEST_PERMISSIONS
        )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestRole() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        startActivityForResult(intent, REQUEST_ROLE)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_PERMISSIONS) {
            return
        }

        if (grantResults.size != permissions.size ||
            grantResults.any { it != PackageManager.PERMISSION_GRANTED }
        ) {
            Toast.makeText(
                this,
                "Please provide permission from system settings",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        requestRole()
    }


}
