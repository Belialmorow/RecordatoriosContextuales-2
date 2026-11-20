package com.example.recordatorios_contextuales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.recordatorios_contextuales.ui.Actividad2App
import com.example.recordatorios_contextuales.ui.theme.ResumenModulos1y2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResumenModulos1y2Theme {
                Actividad2App()
            }
        }
    }
}
