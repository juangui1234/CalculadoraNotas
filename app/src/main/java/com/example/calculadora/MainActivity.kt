package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.calculadora.ui.theme.CalculadoraTheme
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CalculadoraTheme {
                PromedioScreen()
            }
        }
    }
}


// =====================================================
// PANTALLA PRINCIPAL
// Maneja los estados y realiza el calculo
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromedioScreen() {

    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var materia by remember { mutableStateOf("") }
    var nota1 by remember { mutableStateOf("") }
    var nota2 by remember { mutableStateOf("") }
    var nota3 by remember { mutableStateOf("") }

    // Estados del resultado
    var promedio by remember { mutableStateOf<Double?>(null) }
    var estado by remember { mutableStateOf("") }
    var resultadoVisible by remember { mutableStateOf(false) }

    // Snackbar
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()


    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text("Calculadora de Promedios")
                }
            )
        },

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },

        floatingActionButton = {

            BotonCalcularPromedio(
                onClick = {

                    // Convertimos las notas a numeros
                    val n1 = nota1
                        .replace(",", ".")
                        .trim()
                        .toDoubleOrNull()

                    val n2 = nota2
                        .replace(",", ".")
                        .trim()
                        .toDoubleOrNull()

                    val n3 = nota3
                        .replace(",", ".")
                        .trim()
                        .toDoubleOrNull()


                    // Validamos estudiante y materia
                    if (nombre.isBlank() || materia.isBlank()) {

                        resultadoVisible = false

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Ingrese el nombre del estudiante y la materia"
                            )
                        }

                    } else if (n1 == null || n2 == null || n3 == null) {

                        // Validamos que las notas sean numeros
                        resultadoVisible = false

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Ingrese correctamente las tres notas"
                            )
                        }

                    } else if (
                        n1 !in 0.0..5.0 ||
                        n2 !in 0.0..5.0 ||
                        n3 !in 0.0..5.0
                    ) {

                        // Validamos que las notas esten entre 0 y 5
                        resultadoVisible = false

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Las notas deben estar entre 0.0 y 5.0"
                            )
                        }

                    } else {

                        // Calculo del promedio
                        val prom = (n1 + n2 + n3) / 3.0

                        promedio = prom

                        // Determinamos si gana o pierde
                        estado = if (prom >= 3.0) {
                            "Gana"
                        } else {
                            "Pierde"
                        }

                        // Mostramos el resultado
                        resultadoVisible = true

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Promedio calculado correctamente"
                            )
                        }
                    }
                }
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // Formulario
            FormularioPromedio(
                nombre = nombre,
                materia = materia,
                nota1 = nota1,
                nota2 = nota2,
                nota3 = nota3,

                onNombreChange = {
                    nombre = it
                    resultadoVisible = false
                },

                onMateriaChange = {
                    materia = it
                    resultadoVisible = false
                },

                onNota1Change = {
                    nota1 = it
                    resultadoVisible = false
                },

                onNota2Change = {
                    nota2 = it
                    resultadoVisible = false
                },

                onNota3Change = {
                    nota3 = it
                    resultadoVisible = false
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Mostrar resultado
            if (resultadoVisible) {

                ResultadoPromedio(
                    nombre = nombre,
                    materia = materia,
                    promedio = promedio,
                    estado = estado
                )
            }
        }
    }
}


// =====================================================
// FORMULARIO
// Recibe los datos
// Aqui NO se realiza ningun calculo
// =====================================================

@Composable
fun FormularioPromedio(
    nombre: String,
    materia: String,
    nota1: String,
    nota2: String,
    nota3: String,
    onNombreChange: (String) -> Unit,
    onMateriaChange: (String) -> Unit,
    onNota1Change: (String) -> Unit,
    onNota2Change: (String) -> Unit,
    onNota3Change: (String) -> Unit
) {

    Column {

        // Campo estudiante
        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = {
                Text("Estudiante")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Campo materia
        OutlinedTextField(
            value = materia,
            onValueChange = onMateriaChange,
            label = {
                Text("Materia")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Campo Nota 1
        OutlinedTextField(
            value = nota1,
            onValueChange = onNota1Change,
            label = {
                Text("Nota 1")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Campo Nota 2
        OutlinedTextField(
            value = nota2,
            onValueChange = onNota2Change,
            label = {
                Text("Nota 2")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Campo Nota 3
        OutlinedTextField(
            value = nota3,
            onValueChange = onNota3Change,
            label = {
                Text("Nota 3")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}


// =====================================================
// RESULTADO
// Solamente muestra los datos calculados
// Aqui NO se realiza el calculo
// =====================================================

@Composable
fun ResultadoPromedio(
    nombre: String,
    materia: String,
    promedio: Double?,
    estado: String
) {

    val colorEstado = if (estado == "Gana") {
        Color(0xFF4CAF50)
    } else {
        Color(0xFFF44336)
    }


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDE7F6)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Resultado",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Estudiante: $nombre"
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Materia: $materia"
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            val promedioFormateado = promedio?.let {
                String.format(
                    Locale.getDefault(),
                    "%.2f",
                    it
                )
            } ?: ""

            Text(
                text = "Promedio: $promedioFormateado"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = if (estado == "Gana") {
                    "Gana la materia"
                } else {
                    "Pierde la materia"
                },
                color = colorEstado,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// =====================================================
// BOTON CALCULAR
// Recibe la accion mediante onClick
// =====================================================

@Composable
fun BotonCalcularPromedio(
    onClick: () -> Unit
) {

    FloatingActionButton(
        onClick = onClick
    ) {

        Text(
            text = "Calcular",
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        )
    }
}