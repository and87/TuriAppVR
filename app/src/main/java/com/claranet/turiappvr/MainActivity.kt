package com.claranet.turiappvr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.claranet.turiappvr.ui.theme.TuriAppVRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuriAppVRTheme {
                ARModeSelector()
            }
        }
    }
}

@Composable
fun ARModeSelector(){
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier.padding(all = 5.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {
                context.startActivity(Intent(context, POIARViewerActivity::class.java))
            }) {
                Text("POI Viewer")
            }

            Button(onClick = {
                context.startActivity(Intent(context, TrailARGuideActivity::class.java))
            }) {
                Text("Trail Guide")
            }
        }
    }


}

@Preview
@Composable
fun ARModeSelectorPreview(){
    ARModeSelector()
}


