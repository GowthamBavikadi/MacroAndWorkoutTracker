package com.gowtham.gym.ui.workout

import android.os.SystemClock
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.gym.R
import com.gowtham.gym.*
import kotlinx.coroutines.delay

enum class AnatomyViewAngle(val title: String) {
    FRONT("Anterior"),
    BACK("Posterior")
}


object AnatomyCoordinates {
    val front: Map<MuscleGroup, List<List<Offset>>> = mapOf(
        MuscleGroup.TRAPS to listOf(
            listOf(
                Offset(0.422f, 0.205f), Offset(0.452f, 0.212f),
                Offset(0.476f, 0.172f), Offset(0.500f, 0.168f),
                Offset(0.524f, 0.172f), Offset(0.548f, 0.212f),
                Offset(0.578f, 0.205f), Offset(0.556f, 0.226f),
                Offset(0.500f, 0.220f), Offset(0.444f, 0.226f)
            )
        ),
        MuscleGroup.CHEST to listOf(

            listOf(
                Offset(0.422f, 0.218f), Offset(0.496f, 0.220f),
                Offset(0.496f, 0.282f), Offset(0.435f, 0.280f),
                Offset(0.412f, 0.252f)
            ),

            listOf(
                Offset(0.504f, 0.220f), Offset(0.578f, 0.218f),
                Offset(0.588f, 0.252f), Offset(0.565f, 0.280f),
                Offset(0.504f, 0.282f)
            )
        ),
        MuscleGroup.SHOULDERS to listOf(

            listOf(
                Offset(0.368f, 0.220f), Offset(0.410f, 0.204f),
                Offset(0.418f, 0.220f), Offset(0.415f, 0.264f),
                Offset(0.378f, 0.270f), Offset(0.362f, 0.245f)
            ),

            listOf(
                Offset(0.582f, 0.220f), Offset(0.590f, 0.204f),
                Offset(0.632f, 0.220f), Offset(0.638f, 0.245f),
                Offset(0.622f, 0.270f), Offset(0.585f, 0.264f)
            )
        ),
        MuscleGroup.BICEPS to listOf(

            listOf(
                Offset(0.365f, 0.268f), Offset(0.412f, 0.272f),
                Offset(0.414f, 0.336f), Offset(0.392f, 0.350f),
                Offset(0.360f, 0.344f), Offset(0.354f, 0.298f)
            ),

            listOf(
                Offset(0.588f, 0.272f), Offset(0.635f, 0.268f),
                Offset(0.646f, 0.298f), Offset(0.640f, 0.344f),
                Offset(0.608f, 0.350f), Offset(0.586f, 0.336f)
            )
        ),
        MuscleGroup.ABS to listOf(
            listOf(
                Offset(0.442f, 0.286f), Offset(0.500f, 0.284f),
                Offset(0.558f, 0.286f), Offset(0.556f, 0.365f),
                Offset(0.546f, 0.438f), Offset(0.500f, 0.462f),
                Offset(0.454f, 0.438f), Offset(0.444f, 0.365f)
            )
        ),
        MuscleGroup.FOREARMS to listOf(

            listOf(
                Offset(0.355f, 0.342f), Offset(0.398f, 0.350f),
                Offset(0.388f, 0.402f), Offset(0.342f, 0.442f),
                Offset(0.292f, 0.464f), Offset(0.282f, 0.432f),
                Offset(0.322f, 0.370f)
            ),

            listOf(
                Offset(0.602f, 0.350f), Offset(0.645f, 0.342f),
                Offset(0.678f, 0.370f), Offset(0.718f, 0.432f),
                Offset(0.708f, 0.464f), Offset(0.658f, 0.442f),
                Offset(0.612f, 0.402f)
            )
        ),
        MuscleGroup.QUADS to listOf(

            listOf(
                Offset(0.432f, 0.438f), Offset(0.495f, 0.456f),
                Offset(0.492f, 0.565f), Offset(0.482f, 0.658f),
                Offset(0.434f, 0.662f), Offset(0.412f, 0.565f)
            ),

            listOf(
                Offset(0.505f, 0.456f), Offset(0.568f, 0.438f),
                Offset(0.588f, 0.565f), Offset(0.566f, 0.662f),
                Offset(0.518f, 0.658f), Offset(0.508f, 0.565f)
            )
        ),
        MuscleGroup.CALVES to listOf(

            listOf(
                Offset(0.426f, 0.678f), Offset(0.484f, 0.674f),
                Offset(0.482f, 0.760f), Offset(0.464f, 0.852f),
                Offset(0.438f, 0.850f), Offset(0.414f, 0.742f)
            ),

            listOf(
                Offset(0.516f, 0.674f), Offset(0.574f, 0.678f),
                Offset(0.586f, 0.742f), Offset(0.562f, 0.850f),
                Offset(0.536f, 0.852f), Offset(0.518f, 0.760f)
            )
        )
    )

    val back: Map<MuscleGroup, List<List<Offset>>> = mapOf(
        MuscleGroup.TRAPS to listOf(
            listOf(
                Offset(0.468f, 0.155f), Offset(0.532f, 0.155f),
                Offset(0.575f, 0.216f), Offset(0.548f, 0.270f),
                Offset(0.500f, 0.320f), Offset(0.452f, 0.270f),
                Offset(0.425f, 0.216f)
            )
        ),
        MuscleGroup.SHOULDERS to listOf(

            listOf(
                Offset(0.422f, 0.216f), Offset(0.385f, 0.216f),
                Offset(0.368f, 0.245f), Offset(0.372f, 0.274f),
                Offset(0.415f, 0.266f)
            ),

            listOf(
                Offset(0.578f, 0.216f), Offset(0.615f, 0.216f),
                Offset(0.632f, 0.245f), Offset(0.628f, 0.274f),
                Offset(0.585f, 0.266f)
            )
        ),
        MuscleGroup.LATS to listOf(

            listOf(
                Offset(0.415f, 0.268f), Offset(0.460f, 0.282f),
                Offset(0.485f, 0.330f), Offset(0.460f, 0.388f),
                Offset(0.428f, 0.354f)
            ),

            listOf(
                Offset(0.585f, 0.268f), Offset(0.572f, 0.354f),
                Offset(0.540f, 0.388f), Offset(0.515f, 0.330f),
                Offset(0.540f, 0.282f)
            )
        ),
        MuscleGroup.TRICEPS to listOf(

            listOf(
                Offset(0.414f, 0.270f), Offset(0.374f, 0.274f),
                Offset(0.358f, 0.342f), Offset(0.404f, 0.346f)
            ),

            listOf(
                Offset(0.586f, 0.270f), Offset(0.596f, 0.346f),
                Offset(0.642f, 0.342f), Offset(0.626f, 0.274f)
            )
        ),
        MuscleGroup.FOREARMS to listOf(

            listOf(
                Offset(0.392f, 0.344f), Offset(0.345f, 0.356f),
                Offset(0.285f, 0.442f), Offset(0.258f, 0.478f),
                Offset(0.302f, 0.480f), Offset(0.384f, 0.402f)
            ),

            listOf(
                Offset(0.608f, 0.344f), Offset(0.616f, 0.402f),
                Offset(0.698f, 0.480f), Offset(0.742f, 0.478f),
                Offset(0.715f, 0.442f), Offset(0.655f, 0.356f)
            )
        ),
        MuscleGroup.LOWER_BACK to listOf(
            listOf(
                Offset(0.464f, 0.356f), Offset(0.500f, 0.336f),
                Offset(0.536f, 0.356f), Offset(0.530f, 0.420f),
                Offset(0.500f, 0.442f), Offset(0.470f, 0.420f)
            )
        ),
        MuscleGroup.GLUTES to listOf(

            listOf(
                Offset(0.446f, 0.418f), Offset(0.422f, 0.465f),
                Offset(0.430f, 0.518f), Offset(0.496f, 0.504f),
                Offset(0.498f, 0.446f)
            ),

            listOf(
                Offset(0.502f, 0.446f), Offset(0.504f, 0.504f),
                Offset(0.570f, 0.518f), Offset(0.578f, 0.465f),
                Offset(0.554f, 0.418f)
            )
        ),
        MuscleGroup.HAMSTRINGS to listOf(

            listOf(
                Offset(0.422f, 0.518f), Offset(0.494f, 0.516f),
                Offset(0.484f, 0.664f), Offset(0.432f, 0.662f)
            ),

            listOf(
                Offset(0.506f, 0.516f), Offset(0.578f, 0.518f),
                Offset(0.568f, 0.662f), Offset(0.516f, 0.664f)
            )
        ),
        MuscleGroup.CALVES to listOf(

            listOf(
                Offset(0.436f, 0.668f), Offset(0.488f, 0.674f),
                Offset(0.484f, 0.775f), Offset(0.462f, 0.875f),
                Offset(0.436f, 0.870f), Offset(0.422f, 0.750f)
            ),

            listOf(
                Offset(0.512f, 0.674f), Offset(0.564f, 0.668f),
                Offset(0.578f, 0.750f), Offset(0.564f, 0.870f),
                Offset(0.538f, 0.875f), Offset(0.516f, 0.775f)
            )
        )
    )
}


fun isPointInPolygon(point: Offset, vertices: List<Offset>): Boolean {
    var intersectCount = 0
    val n = vertices.size
    for (i in 0 until n) {
        val v1 = vertices[i]
        val v2 = vertices[(i + 1) % n]
        if (((v1.y > point.y) != (v2.y > point.y)) &&
            (point.x < (v2.x - v1.x) * (point.y - v1.y) / (v2.y - v1.y) + v1.x)
        ) {
            intersectCount++
        }
    }
    return intersectCount % 2 == 1
}


@Composable
fun BodyMapScreen(
    viewModel: MainViewModel
) {
    var selectedMuscle by remember { mutableStateOf<MuscleGroup?>(null) }
    var currentAngle by remember { mutableStateOf(AnatomyViewAngle.FRONT) }
    var searchText by remember { mutableStateOf("") }
    var showStopSessionDialog by remember { mutableStateOf(false) }
    var showPRsDialog by remember { mutableStateOf(false) }
    var showWorkoutSettingsDialog by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(false) }

    val trainedToday by viewModel.trainedMusclesToday.collectAsState()
    val isSessionActive by viewModel.isSessionActive.collectAsState()
    val allPRs by viewModel.allPersonalRecords.collectAsState()


    if (showStopSessionDialog) {
        AlertDialog(
            onDismissRequest = { showStopSessionDialog = false },
            title = { Text("End Workout Session?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    "You have an active workout session running. Save or discard this session.",
                    color = ThemeColors.textSecondary
                )
            },
            containerColor = ThemeColors.card,
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.stopSession()
                        showStopSessionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRingColors.protein)
                ) {
                    Text("Stop & Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelSession()
                        showStopSessionDialog = false
                    }
                ) {
                    Text("Discard", color = HealthRingColors.calories)
                }
            }
        )
    }


    if (showPRsDialog) {
        AlertDialog(
            onDismissRequest = { showPRsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = HealthRingColors.fat)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Personal Records", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                if (allPRs.isEmpty()) {
                    Text("No personal records logged yet. Log your sets to automatically track PRs!", color = ThemeColors.textSecondary)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                        items(allPRs) { pr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ThemeColors.cardAlt)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(pr.exerciseName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Est. 1RM: ${String.format("%.1f", pr.estimated1RM)} kg", color = ThemeColors.textSecondary, fontSize = 11.sp)
                                }
                                Text("${pr.bestWeight.toInt()} kg  ${pr.reps}", color = HealthRingColors.fat, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            },
            containerColor = ThemeColors.card,
            confirmButton = {
                TextButton(onClick = { showPRsDialog = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }


    if (showWorkoutSettingsDialog) {
        WorkoutSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showWorkoutSettingsDialog = false }
        )
    }


    if (selectedMuscle != null) {
        MuscleExerciseDetailView(
            viewModel = viewModel,
            initialMuscle = selectedMuscle!!,
            trainedToday = trainedToday,
            onBack = { selectedMuscle = null },
            onSwitchMuscle = { selectedMuscle = it }
        )
        return
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "WORKOUT",
                        color = HealthRingColors.protein,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        "Muscle Map",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ThemeColors.cardAlt)
                            .border(1.dp, ThemeColors.border, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.Accessibility else Icons.Default.GridView,
                            contentDescription = "Toggle View",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }


                    IconButton(
                        onClick = { showPRsDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ThemeColors.cardAlt)
                            .border(1.dp, ThemeColors.border, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = "Personal Records",
                            tint = HealthRingColors.fat,
                            modifier = Modifier.size(16.dp)
                        )
                    }


                    IconButton(
                        onClick = { showWorkoutSettingsDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ThemeColors.cardAlt)
                            .border(1.dp, ThemeColors.border, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Workout Settings",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }


                    SessionTimerPill(
                        isActive = isSessionActive,
                        formattedTimeProvider = { viewModel.formattedElapsedTime() },
                        onStart = { viewModel.startSession() },
                        onStopRequest = { showStopSessionDialog = true }
                    )
                }
            }


            CapsuleSearchBar(
                text = searchText,
                onTextChange = { searchText = it },
                placeholder = "Search any exercise or muscle",
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            if (searchText.isNotBlank()) {

                QuickSearchResults(
                    viewModel = viewModel,
                    query = searchText,
                    onSelectMuscle = { selectedMuscle = it }
                )
            } else if (isGridView) {

                Spacer(modifier = Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp)
                ) {
                    items(MuscleGroup.values()) { muscle ->
                        MuscleGroupCardTile(
                            muscle = muscle,
                            isTrained = trainedToday.contains(muscle),
                            onClick = { selectedMuscle = muscle }
                        )
                    }
                }
            } else {

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(ThemeColors.cardAlt)
                            .padding(3.dp)
                    ) {
                        AnatomyViewAngle.values().forEach { angle ->
                            val isSelected = currentAngle == angle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isSelected) ThemeColors.pill else Color.Transparent)
                                    .clickable { currentAngle = angle }
                                    .padding(horizontal = 18.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    angle.title,
                                    color = if (isSelected) Color.White else ThemeColors.textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }


                val visibleMuscles = remember(currentAngle) {
                    val polygons = if (currentAngle == AnatomyViewAngle.FRONT) AnatomyCoordinates.front else AnatomyCoordinates.back
                    MuscleGroup.values().filter { polygons.containsKey(it) }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visibleMuscles) { muscle ->
                        val isTrained = trainedToday.contains(muscle)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(ThemeColors.cardAlt)
                                .border(0.8.dp, ThemeColors.border, RoundedCornerShape(50))
                                .clickable { selectedMuscle = muscle }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isTrained) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(muscle.primaryColor)
                                )
                            }
                            Text(
                                muscle.title,
                                color = ThemeColors.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }


                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnatomicalFigureCanvas(
                        angle = currentAngle,
                        selectedMuscle = null,
                        trainedToday = trainedToday,
                        isMini = false,
                        onSelectMuscle = { selectedMuscle = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@Composable
fun SessionTimerPill(
    isActive: Boolean,
    formattedTimeProvider: () -> String,
    onStart: () -> Unit,
    onStopRequest: () -> Unit
) {
    var tick by remember { mutableStateOf(0L) }

    LaunchedEffect(isActive) {
        while (isActive) {
            delay(1000)
            tick = SystemClock.uptimeMillis()
        }
    }

    if (isActive) {
        val elapsedText = remember(tick, isActive) { formattedTimeProvider() }
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 0.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF1B2B1B))
                .border(1.dp, Color(0xFF30D158).copy(alpha = 0.5f), RoundedCornerShape(50))
                .clickable(onClick = onStopRequest)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF30D158).copy(alpha = alpha))
            )
            Text(
                elapsedText,
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Default.Stop,
                contentDescription = "Stop",
                tint = Color(0xFFFA114F),
                modifier = Modifier.size(13.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(ThemeColors.cardAlt)
                .border(1.dp, ThemeColors.border, RoundedCornerShape(50))
                .clickable(onClick = onStart)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Start Session",
                tint = HealthRingColors.protein,
                modifier = Modifier.size(14.dp)
            )
            Text(
                "Start Session",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun AnatomicalFigureCanvas(
    angle: AnatomyViewAngle,
    selectedMuscle: MuscleGroup?,
    trainedToday: Set<MuscleGroup>,
    isMini: Boolean = false,
    onSelectMuscle: (MuscleGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = if (angle == AnatomyViewAngle.FRONT) R.drawable.body_anterior else R.drawable.body_posterior
    val polygons = if (angle == AnatomyViewAngle.FRONT) AnatomyCoordinates.front else AnatomyCoordinates.back

    Box(
        modifier = modifier
            .pointerInput(angle) {
                detectTapGestures { tapOffset ->
                    val normX = tapOffset.x / size.width
                    val normY = tapOffset.y / size.height
                    val normPoint = Offset(normX, normY)


                    for ((muscle, regionList) in polygons) {
                        for (polygon in regionList) {
                            if (isPointInPolygon(normPoint, polygon)) {
                                onSelectMuscle(muscle)
                                return@detectTapGestures
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Body Anatomy",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
            alpha = if (isMini) 0.65f else 0.85f,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                setToSaturation(0f)
            })
        )


        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height


            trainedToday.forEach { trained ->
                if (polygons.containsKey(trained) && trained != selectedMuscle) {
                    val regions = polygons[trained] ?: return@forEach
                    val brush = Brush.linearGradient(
                        colors = trained.tileGradient.map { it.copy(alpha = if (isMini) 0.45f else 0.55f) }
                    )
                    regions.forEach { regionPoints ->
                        val path = createSmoothPath(regionPoints, canvasW, canvasH)
                        drawPath(path, brush = brush, style = Fill)
                        drawPath(
                            path,
                            color = trained.primaryColor.copy(alpha = 0.6f),
                            style = Stroke(width = if (isMini) 1f else 1.5f)
                        )
                    }
                }
            }


            if (selectedMuscle != null && polygons.containsKey(selectedMuscle)) {
                val regions = polygons[selectedMuscle] ?: return@Canvas
                val fillBrush = Brush.linearGradient(
                    colors = selectedMuscle.tileGradient.map { it.copy(alpha = 0.88f) }
                )
                regions.forEach { regionPoints ->
                    val path = createSmoothPath(regionPoints, canvasW, canvasH)

                    drawPath(path, brush = fillBrush, style = Fill)

                    drawPath(
                        path,
                        color = Color.White.copy(alpha = 0.90f),
                        style = Stroke(width = if (isMini) 1.2f else 2.2f)
                    )
                }
            }
        }
    }
}


private fun createSmoothPath(points: List<Offset>, width: Float, height: Float): Path {
    val path = Path()
    if (points.size < 3) return path

    val scaled = points.map { Offset(it.x * width, it.y * height) }
    val count = scaled.size

    val firstMid = Offset(
        (scaled[count - 1].x + scaled[0].x) * 0.5f,
        (scaled[count - 1].y + scaled[0].y) * 0.5f
    )
    path.moveTo(firstMid.x, firstMid.y)

    for (i in 0 until count) {
        val current = scaled[i]
        val next = scaled[(i + 1) % count]
        val mid = Offset((current.x + next.x) * 0.5f, (current.y + next.y) * 0.5f)
        path.quadraticBezierTo(current.x, current.y, mid.x, mid.y)
    }
    path.close()
    return path
}


@Composable
fun MuscleGroupCardTile(
    muscle: MuscleGroup,
    isTrained: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(muscle.tileGradient))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                if (isTrained) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Trained", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                muscle.title,
                color = Color.Black.copy(alpha = 0.90f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun MuscleExerciseDetailView(
    viewModel: MainViewModel,
    initialMuscle: MuscleGroup,
    trainedToday: Set<MuscleGroup>,
    onBack: () -> Unit,
    onSwitchMuscle: (MuscleGroup) -> Unit
) {
    var activeMuscle by remember { mutableStateOf(initialMuscle) }
    var query by remember { mutableStateOf("") }
    var isAddingInline by remember { mutableStateOf(false) }
    var newExerciseName by remember { mutableStateOf("") }
    var selectedExerciseForLogging by remember { mutableStateOf<ExerciseDefinitionEntity?>(null) }

    val exercises = remember(activeMuscle, query) {
        viewModel.exercisesForMuscle(activeMuscle, query)
    }

    val workoutLogs by viewModel.allWorkoutLogs.collectAsState()
    val allPRs by viewModel.allPersonalRecords.collectAsState()


    var dragAccumulator by remember { mutableStateOf(0f) }


    if (selectedExerciseForLogging != null) {
        WorkoutSetLoggerDialog(
            viewModel = viewModel,
            exercise = selectedExerciseForLogging!!,
            muscle = activeMuscle,
            onDismiss = { selectedExerciseForLogging = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColors.background)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragAccumulator > 100f) {
                            onBack()
                        }
                        dragAccumulator = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 0) {
                            dragAccumulator += dragAmount
                        }
                    }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                activeMuscle.primaryColor.copy(alpha = 0.50f),
                                activeMuscle.secondaryColor.copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            "${exercises.size} exercises",
                            color = ThemeColors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        activeMuscle.title,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                    )
                }
            }


            CapsuleSearchBar(
                text = query,
                onTextChange = { query = it },
                placeholder = "Search ${activeMuscle.title} exercises",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )


            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(exercises) { ex ->
                    val logsForEx = workoutLogs.filter { it.exerciseName.equals(ex.name, ignoreCase = true) }
                    val pr = allPRs.firstOrNull { it.exerciseName.equals(ex.name, ignoreCase = true) }

                    ExerciseRowCard(
                        exercise = ex,
                        sessionCount = logsForEx.size,
                        bestPR = pr,
                        accentColor = activeMuscle.primaryColor,
                        onClick = { selectedExerciseForLogging = ex }
                    )
                }

                item {

                    if (isAddingInline) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(ThemeColors.cardAlt)
                                .border(1.dp, activeMuscle.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BasicTextField(
                                value = newExerciseName,
                                onValueChange = { newExerciseName = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (newExerciseName.isNotBlank()) {
                                        viewModel.addCustomExercise(newExerciseName.trim(), activeMuscle.title)
                                        newExerciseName = ""
                                        isAddingInline = false
                                    }
                                }),
                                decorationBox = { inner ->
                                    if (newExerciseName.isEmpty()) {
                                        Text("New ${activeMuscle.title} exercise name...", color = ThemeColors.textTertiary, fontSize = 14.sp)
                                    }
                                    inner()
                                }
                            )

                            TextButton(onClick = { isAddingInline = false }) {
                                Text("Cancel", color = ThemeColors.textSecondary, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    if (newExerciseName.isNotBlank()) {
                                        viewModel.addCustomExercise(newExerciseName.trim(), activeMuscle.title)
                                        newExerciseName = ""
                                        isAddingInline = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = activeMuscle.primaryColor)
                            ) {
                                Text("Add", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(ThemeColors.card)
                                .border(1.dp, activeMuscle.primaryColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .clickable { isAddingInline = true }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = activeMuscle.primaryColor)
                            Text("Add ${activeMuscle.title} Variation Inline", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }


        MiniBodySwitcher(
            selectedMuscle = activeMuscle,
            trainedToday = trainedToday,
            onSelectMuscle = {
                activeMuscle = it
                onSwitchMuscle(it)
            },
            onExpandToFullBody = onBack,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 96.dp)
        )
    }
}


@Composable
fun ExerciseRowCard(
    exercise: ExerciseDefinitionEntity,
    sessionCount: Int,
    bestPR: PersonalRecordEntity?,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ThemeColors.card)
            .border(1.dp, ThemeColors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(exercise.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            if (bestPR != null) {
                Text("PR: ${bestPR.bestWeight.toInt()} kg  ${bestPR.reps}  $sessionCount sessions", color = ThemeColors.textSecondary, fontSize = 12.sp)
            } else if (sessionCount > 0) {
                Text("$sessionCount sessions logged", color = ThemeColors.textSecondary, fontSize = 12.sp)
            } else {
                Text("Tap to log sets", color = ThemeColors.textTertiary, fontSize = 12.sp)
            }
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ThemeColors.textTertiary)
    }
}


@Composable
fun MiniBodySwitcher(
    selectedMuscle: MuscleGroup,
    trainedToday: Set<MuscleGroup>,
    onSelectMuscle: (MuscleGroup) -> Unit,
    onExpandToFullBody: () -> Unit,
    modifier: Modifier = Modifier
) {
    var angle by remember { mutableStateOf(AnatomyViewAngle.FRONT) }

    Column(
        modifier = modifier
            .width(104.dp)
            .height(168.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xE61C1C1E))
            .border(1.dp, ThemeColors.borderStrong, RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ThemeColors.pill)
                    .clickable { angle = if (angle == AnatomyViewAngle.FRONT) AnatomyViewAngle.BACK else AnatomyViewAngle.FRONT }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(if (angle == AnatomyViewAngle.FRONT) "Ant" else "Post", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }


            IconButton(onClick = onExpandToFullBody, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Default.Fullscreen, contentDescription = "Expand", tint = ThemeColors.textSecondary, modifier = Modifier.size(14.dp))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnatomicalFigureCanvas(
                angle = angle,
                selectedMuscle = selectedMuscle,
                trainedToday = trainedToday,
                isMini = true,
                onSelectMuscle = onSelectMuscle,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
fun WorkoutSetLoggerDialog(
    viewModel: MainViewModel,
    exercise: ExerciseDefinitionEntity,
    muscle: MuscleGroup,
    onDismiss: () -> Unit
) {
    val rpeEnabled by viewModel.rpeEnabled.collectAsState()
    var sets by remember {
        mutableStateOf(
            listOf(
                SetEntry(weight = 60.0, reps = 10),
                SetEntry(weight = 60.0, reps = 10),
                SetEntry(weight = 60.0, reps = 8)
            )
        )
    }
    var rpe by remember { mutableStateOf(7.5) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(muscle.title, color = muscle.primaryColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text("Sets (${sets.size})", color = ThemeColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                sets.forEachIndexed { index, setEntry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Set ${index + 1}", color = ThemeColors.textSecondary, fontSize = 13.sp, modifier = Modifier.width(42.dp))


                        OutlinedTextField(
                            value = setEntry.weight.toInt().toString(),
                            onValueChange = { str ->
                                val w = str.toDoubleOrNull() ?: 0.0
                                sets = sets.toMutableList().also { it[index] = setEntry.copy(weight = w) }
                            },
                            label = { Text("kg", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = muscle.primaryColor,
                                unfocusedBorderColor = ThemeColors.border
                            )
                        )


                        OutlinedTextField(
                            value = setEntry.reps.toString(),
                            onValueChange = { str ->
                                val r = str.toIntOrNull() ?: 0
                                sets = sets.toMutableList().also { it[index] = setEntry.copy(reps = r) }
                            },
                            label = { Text("reps", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = muscle.primaryColor,
                                unfocusedBorderColor = ThemeColors.border
                            )
                        )


                        if (sets.size > 1) {
                            IconButton(
                                onClick = { sets = sets.toMutableList().also { it.removeAt(index) } },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Delete", tint = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                OutlinedButton(
                    onClick = {
                        val last = sets.lastOrNull() ?: SetEntry(weight = 50.0, reps = 10)
                        sets = sets + SetEntry(weight = last.weight, reps = last.reps)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = muscle.primaryColor)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Set", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))


                if (rpeEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "INTENSITY (RPE)",
                            color = ThemeColors.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            String.format(java.util.Locale.US, "%.1f", rpe),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = rpe.toFloat(),
                        onValueChange = { rpe = (Math.round(it * 2) / 2.0).coerceIn(1.0, 10.0) },
                        valueRange = 1f..10f,
                        steps = 17,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }


                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Workout notes (optional)...", fontSize = 12.sp, color = ThemeColors.textTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = muscle.primaryColor,
                        unfocusedBorderColor = ThemeColors.border
                    )
                )
            }
        },
        containerColor = ThemeColors.card,
        confirmButton = {
            Button(
                onClick = {
                    viewModel.logWorkoutSession(
                        exerciseName = exercise.name,
                        bodyPart = muscle.title,
                        sets = sets,
                        rpe = if (rpeEnabled) rpe else null,
                        notes = notes
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = muscle.primaryColor)
            ) {
                Text("Log Workout", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ThemeColors.textSecondary)
            }
        }
    )
}


@Composable
fun WorkoutSettingsDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val rpeEnabled by viewModel.rpeEnabled.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = HealthRingColors.protein)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Workout Settings", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeColors.cardAlt)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text("Track RPE", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("110 rate of perceived exertion scale", color = ThemeColors.textTertiary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = rpeEnabled,
                        onCheckedChange = { viewModel.setRpeEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HealthRingColors.protein,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = ThemeColors.card
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeColors.cardAlt.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    Text(
                        "RPE (Rate of Perceived Exertion) is a 110 scale for how hard a set felt, separate from the weight and reps. It helps you judge whether you had more in the tank or were near your limit  useful for deciding whether to push harder next session. Turn it off if you'd rather log sets without it.",
                        color = ThemeColors.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        containerColor = ThemeColors.card,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}


@Composable
fun CapsuleSearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String = "Search",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(ThemeColors.cardAlt)
            .border(1.dp, ThemeColors.border, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = ThemeColors.textSecondary, modifier = Modifier.size(18.dp))
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (text.isEmpty()) {
                    Text(placeholder, color = ThemeColors.textTertiary, fontSize = 15.sp)
                }
                inner()
            }
        )
        if (text.isNotEmpty()) {
            IconButton(onClick = { onTextChange("") }, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = ThemeColors.textTertiary, modifier = Modifier.size(16.dp))
            }
        }
    }
}


@Composable
fun QuickSearchResults(
    viewModel: MainViewModel,
    query: String,
    onSelectMuscle: (MuscleGroup) -> Unit
) {
    val allCatalog by viewModel.exerciseCatalog.collectAsState()
    val catalog = allCatalog.ifEmpty { MainViewModel.seedExercises() }
    val filtered = catalog.filter { it.name.contains(query, ignoreCase = true) || it.bodyPart.contains(query, ignoreCase = true) }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (filtered.isEmpty()) {
            item {
                Text("No exercises matching \"$query\"", color = ThemeColors.textSecondary, modifier = Modifier.padding(top = 20.dp))
            }
        } else {
            items(filtered) { ex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ThemeColors.card)
                        .clickable { onSelectMuscle(ex.primaryMuscle) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(ex.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(ex.primaryMuscle.title, color = ex.primaryMuscle.primaryColor, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ThemeColors.textTertiary)
                }
            }
        }
    }
}
