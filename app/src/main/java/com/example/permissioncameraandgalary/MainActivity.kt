package com.example.permissioncameraandgalary

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.permissioncameraandgalary.ui.theme.PermissionCameraAndGalaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //B7: Khởi tạo URI để biết đươc ảnh lưu ở đâu.
            var captureImageUri by remember { mutableStateOf<Uri?>(null) }

            //Galary
            val launchGalary =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->  // Tra ve 1 Uri
                    captureImageUri = uri
                }

            //B6: Gọi ứng dụng thứ 3 camera
            val launchPermissionCamera =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                    captureImageUri = CameraService.takeImageFromCameraDevice(this)
                }

            //B4:Khởi tạo Chạy quyền thông qua Launch
            val lauchPermission =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { // Cứ nhớ đây là một callback
                    if (it == true) { // it đại diện cho true ngược lại là false
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    } else {

                    }
                }

            //B5: Khởi tạo giao diện Demo Run Test
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable {
//                        launchGalary.launch("image/*")
                        requestPermissionCamera(
                            onGranted = {
                                //B9: Triển khai Camera
                                captureImageUri = CameraService.takeImageFromCameraDevice(this)
                                //B8: Triển khai Uri
                                captureImageUri?.let { launchPermissionCamera.launch(it) } //it chính là captureImageUri
                            }, onExplainDenied = {

                            }, onRequest = {
                                lauchPermission.launch(Manifest.permission.CAMERA)
                            }
                        )
                    }) {
                AsyncImage(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.Center), contentDescription = "", model = captureImageUri
                )
            }
        }
    }

    //B2: Tạo hàm Request Permission
    fun requestPermissionCamera(
        onGranted: () -> Unit,
        onExplainDenied: () -> Unit,
        onRequest: () -> Unit,
    ) {
        //B3: Tạo biến cho CustomApplication
        val context = CustomAppication.instance
        // when ở đây mới chỉ show ra Dialog
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                // Ham nay chay khi nguoi dung da cap quyen roi
                onGranted()
            }

            PackageManager.PERMISSION_DENIED -> {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    // Khi nguoi dung tu choi tu 2 lan se chay ham nay va hien thi popup giai thich
                    onExplainDenied()
                } else {
                    // Lan dau vao app chua cap quyen gi se chay ham nay
                    onRequest()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PermissionCameraAndGalaryTheme {
        Greeting("Android")
    }
}