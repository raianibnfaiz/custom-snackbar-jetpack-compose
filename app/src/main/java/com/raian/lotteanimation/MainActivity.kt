package com.raian.lotteanimation

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raian.lotteanimation.ui.theme.LotteAnimationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                //TutorialLazyScreen()
                SnackBarScreen()
            }
        }
    }
}
@Composable
fun SnackBarScreen() {
//    BasicSnackbar()
//    StateHostSnackBar()
    CustomSnackbar()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicSnackbar() {
    var showSnackbar by remember { mutableStateOf(false) }
    Scaffold(snackbarHost = {
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.padding(10.dp),
                actionOnNewLine = true,
                action = {
                    Text(
                        text = "Action",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { showSnackbar = false }
                    )
                },
                dismissAction = {
                    Text(
                        text = "Dismiss",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { showSnackbar = false }
                    )
                }
            ) {
                Text(text = "Are you sure?", fontSize = 16.sp)
            }
        }
    }) {
        Button(onClick = { showSnackbar = true }) {
            Text(text = "Show SnackBar")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateHostSnackBar() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) {
        Button(onClick = {
            scope.launch {
                val actionLabelString: String? = "Action"
                val isActionLabelEmpty = actionLabelString.isNullOrEmpty()
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = "Are you sure?",
                    actionLabel = actionLabelString,
                    withDismissAction = !isActionLabelEmpty,
                    duration = if (isActionLabelEmpty)  {
                        SnackbarDuration.Short
                    } else SnackbarDuration.Indefinite
                )

                when(snackbarResult) {
                    SnackbarResult.ActionPerformed -> {
                        Toast.makeText(context, "Action", Toast.LENGTH_SHORT).show()
                    }
                    SnackbarResult.Dismissed -> {
                        Toast.makeText(context, "dismissed", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }) {
            Text(text = "Show SnackBar")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSnackbar() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(snackbarHost = {
        AnimatedVisibility(
            visible = snackbarHostState.currentSnackbarData != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            SnackbarHost( modifier = Modifier.padding(bottom = 16.dp),hostState = snackbarHostState) { snackBarData ->
                Row(Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
                    .padding(10.dp)
                ) {
                    Text(text = snackBarData.visuals.message)
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    if (!snackBarData.visuals.actionLabel.isNullOrEmpty()) {
                        Text(
                            text = snackBarData.visuals.actionLabel!!,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Action", Toast.LENGTH_SHORT).show()
                                snackBarData.performAction()
                            }
                        )
                    }
                    Text(
                        text = "Dismiss",
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Dismissed", Toast.LENGTH_SHORT).show()
                            snackBarData.dismiss()
                        }
                    )
                    IconButton(onClick = {
                        Toast.makeText(context, "Dismiss", Toast.LENGTH_SHORT).show()
                        snackBarData.dismiss()

                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            }
        }
    }) {
        Button(onClick = {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Are you sure?",
                    actionLabel = "Action"
                )
            }
        }) {
            Text(text = "Show SnackBar")
        }
    }
}



