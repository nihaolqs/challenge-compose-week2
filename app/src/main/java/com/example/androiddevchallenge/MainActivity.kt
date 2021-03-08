/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        CountdownScreen(60000, Modifier)
    }
}

val handel by lazy { Handler(Looper.getMainLooper()) }

@Composable
fun CountdownScreen(time: Int, modifier: Modifier) {
    val leftTime = remember { mutableStateOf(time) }
    val color = when (leftTime.value * 100 / time) {
        in 0..33 -> Color(0xFFE30425)
        in 10..66 -> Color(0xFFFFBA53)
        else -> Color(0xFF4CAF50)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black)
    ) {
        Surface(modifier = modifier.height(400.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black)
                    .padding(10.dp),
                onDraw = {

                    drawArc(
                        size = Size(200.dp.toPx(), 200.dp.toPx()),
                        color = Color(0xFF494949),
                        startAngle = -90F,
                        sweepAngle = 360F,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(
                            size.width / 2 - 100.dp.toPx(),
                            size.height / 2 - 100.dp.toPx()
                        )
                    )

                    drawArc(
                        size = Size(200.dp.toPx(), 200.dp.toPx()),
                        color = color,
                        startAngle = -90F,
                        sweepAngle = leftTime.value * 1f / time * 360,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(
                            size.width / 2 - 100.dp.toPx(),
                            size.height / 2 - 100.dp.toPx()
                        )
                    )
                }
            )

            Text(
                text = formartTime(leftTime.value),
                color = color,
                style = typography.h5,
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val y =
                            constraints.maxHeight / 2 - placeable[LastBaseline] / 2
                        val x = (constraints.maxWidth - placeable.width) / 2
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(x, y)
                        }
                    }
            )
        }

        Row() {
            Button(
                onClick = {
                    handel.removeCallbacksAndMessages(null)
                    leftTime.value = time
                },
                modifier =
                Modifier
                    .weight(1f)
                    .padding(30.dp)
            ) {
                Text(text = "Reset")
            }
            val isStart = remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (!isStart.value) {
                        val run = object : Runnable {
                            override fun run() {
                                leftTime.value -= 50
                                if (leftTime.value >= 50) {
                                    handel.postDelayed(this, 50)
                                }
                            }
                        }
                        handel.postDelayed(run, 50)
                    } else {
                        handel.removeCallbacksAndMessages(null)
                    }
                    isStart.value = !isStart.value
                },
                modifier =
                Modifier
                    .weight(1f)
                    .padding(30.dp)
            ) {
                Text(text = if (isStart.value) "Pause" else "Start")
            }
        }
    }
}

fun formartTime(time: Int): String {
    val sb = StringBuffer()
    var t = time / 1000
    val s = t % 60
    sb.insert(0, s)
    if (s < 10) {
        sb.insert(0, 0)
    }
    t /= 60
    val m = t % 60
    sb.insert(0, "$m:")
    if (m < 10) {
        sb.insert(0, 0)
    }
    t /= 60
    sb.insert(0, "$t:")
    if (t < 10) {
        sb.insert(0, 0)
    }
    return sb.toString()
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
