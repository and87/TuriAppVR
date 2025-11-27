package com.example.turiappvr

import android.R
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.turiappvr.ui.theme.TuriAppVRTheme
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.model.Model
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes


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
        model?.let {
            // Create a parent node to anchor the position
            val parentNode = Node(engine = engine).apply {
                position = io.github.sceneview.math.Position(x = 0f, y = 0f, z = -2f)
            }

            // Create the model node as a child
            val modelNode = ModelNode(
                modelInstance = it.instance,
                scaleToUnits = 1f,
            )

            // Add model as child of parent node
            parentNode.addChildNode(modelNode)

            // Add parent node to the scene
            childNodes += parentNode
        }
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