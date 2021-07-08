package com.viona.runtimepermission


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    private var PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private val STORAGE_PERMISSION_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRequest: Button = findViewById(R.id.button)
        buttonRequest.setOnClickListener {
            askForPermission()
        }
    }

    private fun askForPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, STORAGE_PERMISSION_CODE)
        } else {
            Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                if (allPermissionGranted()) {
                    Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()
                } else {
                    if (isNeverAskChecked) {
                        askForPermission()
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed because we need to save picture on your storage")
                            .setPositiveButton("ok"
                            ) { _, _ ->
                                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri: Uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                            .setNegativeButton("cancel"
                            ) { dialog, _ -> dialog.dismiss() }
                            .create().show()
                    }
                }
            }
        }
    }

    private val isNeverAskChecked: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (PERMISSIONS in PERMISSIONS) {
                    if (shouldShowRequestPermissionRationale(PERMISSIONS)) {
                        return true
                    }
                }
            }
            return false
        }

    private fun allPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (PERMISSION in PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(this, PERMISSION) == PackageManager.PERMISSION_DENIED) {
                    return false
                }
            }
        }
        return true
    }
}