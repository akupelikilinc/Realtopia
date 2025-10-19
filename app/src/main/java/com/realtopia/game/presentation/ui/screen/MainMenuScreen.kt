package com.realtopia.game.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.presentation.ui.theme.*
import kotlin.math.*

@Composable
fun MainMenuScreen(
    onNavigateToCareer: () -> Unit,
    onNavigateToTimeTrial: () -> Unit,
    onNavigateToEndless: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(true) }
    
    // Background animation
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "background"
    )
    
    // Floating animation for particles
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color(0xFF0f3460)
                    )
                )
            )
    ) {
        // Animated background
        AnimatedIslandBackground(
            offset = backgroundOffset,
            floatingOffset = floatingOffset
        )
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section - Title and stats
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                
                // Game Title
                GameTitle()
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Statistics
                StatisticsPanel()
            }
            
            // Bottom section - Game modes
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GameModeButton(
                    title = "Kariyer Modu",
                    subtitle = "Hikaye tabanlı ilerleme",
                    icon = Icons.Default.Work,
                    onClick = onNavigateToCareer,
                    color = CareerColor
                )
                
                GameModeButton(
                    title = "Zaman Denemesi",
                    subtitle = "Hızlı mülk yatırımı",
                    icon = Icons.Default.Timer,
                    onClick = onNavigateToTimeTrial,
                    color = TimeTrialColor
                )
                
                GameModeButton(
                    title = "Sonsuz Mod",
                    subtitle = "Sınırsız oyun deneyimi",
                    icon = Icons.Default.AllInclusive,
                    onClick = onNavigateToEndless,
                    color = EndlessColor
                )
            }
        }
    }
}

@Composable
private fun GameTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "title")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title"
    )
    
    Text(
        text = "REALTOPIA",
        style = MaterialTheme.typography.displayLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = Color.White,
        modifier = Modifier
            .scale(titleScale)
            .graphicsLayer {
                shadowElevation = 8f
            }
    )
    
    Text(
        text = "Emlak İmparatorluğu",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun StatisticsPanel() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatCard(
            title = "En Yüksek Bakiye",
            value = "₺2.5M",
            icon = Icons.Default.AccountBalanceWallet,
            color = GameSuccess
        )
        
        StatCard(
            title = "En Hızlı Süre",
            value = "3:45",
            icon = Icons.Default.Timer,
            color = GameWarning
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundSecondary.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GameModeButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isPressed = true },
                    onDragEnd = { isPressed = false },
                    onDrag = { _, _ -> }
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AnimatedIslandBackground(
    offset: Float,
    floatingOffset: Float
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height
        
        // Animated island
        val islandOffset = offset * 50f
        val islandY = height * 0.6f + sin(offset * 2 * PI).toFloat() * 20f
        
        // Draw island
        drawPath(
            path = createIslandPath(width, height, islandY),
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4a7c59),
                    Color(0xFF2d5016)
                )
            )
        )
        
        // Draw water
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF87CEEB),
                    Color(0xFF4682B4)
                )
            ),
            topLeft = Offset(0f, islandY),
            size = androidx.compose.ui.geometry.Size(width, height - islandY)
        )
        
        // Draw decorative elements - simplified
        // Buildings, trees and clouds can be added later
    }
}

private fun createIslandPath(width: Float, height: Float, islandY: Float): androidx.compose.ui.graphics.Path {
    val path = androidx.compose.ui.graphics.Path()
    path.moveTo(0f, islandY)
    path.quadraticBezierTo(width * 0.2f, islandY - 50f, width * 0.4f, islandY)
    path.quadraticBezierTo(width * 0.6f, islandY + 30f, width * 0.8f, islandY - 20f)
    path.quadraticBezierTo(width, islandY - 10f, width, islandY)
    path.lineTo(width, height)
    path.lineTo(0f, height)
    path.close()
    return path
}

// Canvas helper functions - simplified for now

// Color definitions
val CareerColor = Color(0xFF4CAF50)
val TimeTrialColor = Color(0xFFFF9800)
val EndlessColor = Color(0xFF2196F3)
