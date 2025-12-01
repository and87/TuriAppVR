package com.claranet.turiappvr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.claranet.turiappvr.ui.theme.TuriAppVRTheme
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.math.Rotation
import io.github.sceneview.model.Model
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.math.Position


class TrailARGuideActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuriAppVRTheme {
                TrailARGuideApp()
            }
        }
    }
}


@Composable
fun TrailARGuideApp() {
    val context = LocalContext.current

    // Oggetti core SceneView
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val cameraStream = rememberARCameraStream(materialLoader)

    // Carica il modello
    val model = remember<Model?> {
        modelLoader.createModel("models/arrow.glb")
    }

    // Nodi della scena AR
    val childNodes = rememberNodes()

    // Create and position the model node
    LaunchedEffect(model) {

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
            //materialInstances.forEach { materialInstances -> materialInstances.forEach { materialInstance -> materialInstance.setColor(Color.Green)  } }
        }

        parentNode1.addChildNode(modelNode1)
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
        childNodes += parentNode2

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