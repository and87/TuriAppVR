package com.claranet.turiappvr

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.claranet.turiappvr.models.ArrowVisibility
import com.claranet.turiappvr.ui.theme.TuriAppVRTheme
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.math.Position
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.claranet.turiappvr.fixtures.getTrailCrossroads
import com.claranet.turiappvr.utils.GPSUtils.detectNearestCrossroad

class TrailARGuideActivity : ComponentActivity() {

    private var hasLocationPermission by mutableStateOf(false)

    // Define the permission launcher in the Activity
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (!isGranted) {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check permission on start
        checkLocationPermission()

        setContent {
            TuriAppVRTheme {
                if (hasLocationPermission) {
                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                    TrailARGuideApp(locationManager)
                } else {
                    // Show waiting screen
                    WaitingPermissionScreen(checkLocationPermission())
                }
            }
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasLocationPermission = true
            }
            else -> {
                // Request permission - this will show system dialog, NOT go to settings
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}

@Composable
fun WaitingPermissionScreen(
    onCheckPermission: Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Location permission is required for trail navigation")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onCheckPermission }) {
                Text("Grant Permission")
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun TrailARGuideApp(
    locationManager: LocationManager,
) {

    var arrowVisibility by remember { mutableStateOf(ArrowVisibility()) }
    var leftArrowNode by remember { mutableStateOf<Node?>(null) }
    var rightArrowNode by remember { mutableStateOf<Node?>(null) }

    // Oggetti core SceneView
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val cameraStream = rememberARCameraStream(materialLoader)


    // Nodi della scena AR
    val childNodes = rememberNodes()

    LaunchedEffect(Unit) {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            5f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Log.i("update location", location.toString());
                    arrowVisibility = detectNearestCrossroad(
                        location.latitude,
                        location.longitude,
                        getTrailCrossroads()
                    )
                }
            }
        )
    }

    // Create and position the model node
    LaunchedEffect(Unit) {

        // First arrow (green, original direction)
        val parentNode1 = Node(engine = engine).apply {
            position = Position(x = -0.5f, y = 0f, z = -2f)
        }

        val modelNode1 = ModelNode(
            modelInstance = modelLoader.createModel("models/arrow.glb")?.instance!!,
            scaleToUnits = 1f
        ).apply {
            // Rotate 180 degrees on Y axis to point opposite direction
            rotation = Rotation(x = 0f, y = 20f, z = 0f)
        }

        parentNode1.addChildNode(modelNode1)
        leftArrowNode = parentNode1
        childNodes += parentNode1

        // Second arrow (rotated 180 degrees, opposite direction)
        val parentNode2 = Node(engine = engine).apply {
            position = Position(x = 0.5f, y = 0f, z = -2f)
        }

        val modelNode2 = ModelNode(
            modelInstance = modelLoader.createModel("models/arrow.glb")?.instance!!,
            scaleToUnits = 1f
        ).apply {
            // Rotate 180 degrees on Y axis to point opposite direction
            rotation = Rotation(x = 0f, y = 140f, z = 0f)
        }

        parentNode2.addChildNode(modelNode2)
        rightArrowNode = parentNode2
        childNodes += parentNode2

    }

    // Update visibility when arrowVisibility changes
    LaunchedEffect(arrowVisibility) {
        Log.i("update coordinates", arrowVisibility.toString());
        leftArrowNode?.isVisible = arrowVisibility.showLeftArrow
        rightArrowNode?.isVisible = arrowVisibility.showRightArrow
    }

    ARScene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        cameraStream = cameraStream,
        childNodes = childNodes,
        planeRenderer = false,
    )
}