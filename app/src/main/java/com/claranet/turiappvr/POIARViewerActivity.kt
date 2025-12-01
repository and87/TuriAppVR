package com.claranet.turiappvr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.model.Model
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import kotlin.collections.set

class POIARViewerActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TuriAppVR()
        }

    }
}

@Composable
fun TuriAppVR() {
    val context = LocalContext.current

    // Oggetti core SceneView
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val cameraStream = rememberARCameraStream(materialLoader)

    // Carica il modello
    val model = remember<Model?> {
        modelLoader.createModel("models/terme.glb")
    }

    // Nodi della scena AR
    val childNodes = rememberNodes()

    // Tracciamo quali immagini hanno già un anchor
    val imageNodes = remember { mutableStateMapOf<Int, AnchorNode>() }

    ARScene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        cameraStream = cameraStream,
        childNodes = childNodes,

        // Configurazione della sessione ARCore
        sessionConfiguration = { session, config ->
            // Database di augmented images
            config.augmentedImageDatabase = createAugmentedImageDatabase(context, session)

            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            config.focusMode = Config.FocusMode.AUTO
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        },

        // Non ci servono piani, possiamo disattivare la loro visualizzazione
        planeRenderer = false,

        // Callback ad ogni frame
        onSessionUpdated = { _, updatedFrame ->
            val updatedImages =
                updatedFrame.getUpdatedTrackables(AugmentedImage::class.java)

            updatedImages.forEach { image ->
                when (image.trackingState) {
                    TrackingState.TRACKING -> {
                        val existingNode = imageNodes[image.index]
                        if (existingNode == null) {
                            model?.let { loadedModel ->
                                val anchor = image.createAnchor(image.centerPose)
                                val anchorNode = AnchorNode(
                                    engine = engine,
                                    anchor = anchor
                                ).apply {
                                    modelLoader.createInstance(loadedModel)?.let {
                                        addChildNode(
                                            ModelNode(
                                                modelInstance = it
                                            ).apply {
                                                // Riduci/ingrandisci il modello se serve
                                                scale = Float3(0.15f)
                                            }
                                        )
                                    }
                                }
                                imageNodes[image.index] = anchorNode
                                childNodes += anchorNode
                            }
                        } else {
                            existingNode.isVisible = true
                        }
                    }

                    TrackingState.PAUSED -> {
                        imageNodes[image.index]?.isVisible = false
                    }

                    TrackingState.STOPPED -> {
                        val node = imageNodes.remove(image.index)
                        if (node != null) {
                            childNodes -= node
                            node.destroy()
                        }
                    }

                    else -> Unit
                }
            }
        }
    )
}