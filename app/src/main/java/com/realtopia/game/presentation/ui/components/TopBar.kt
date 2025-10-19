package com.realtopia.game.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realtopia.game.presentation.ui.theme.*

@Composable
fun TopBar(
    balance: Double,
    portfolioValue: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Balance Panel
        InfoPanel(
            icon = Icons.Default.AccountBalanceWallet,
            title = "Bakiye",
            value = formatMoney(balance),
            color = TextOnDark,
            backgroundColor = BackgroundSecondary.copy(alpha = 0.2f)
        )
        
        // Portfolio Panel
        InfoPanel(
            icon = Icons.Default.TrendingUp,
            title = "Portföy",
            value = formatMoney(portfolioValue),
            color = TextOnDark,
            backgroundColor = BackgroundSecondary.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun InfoPanel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = color.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color,
                fontSize = 18.sp
            )
        }
    }
}

private fun formatMoney(amount: Double): String {
    return when {
        amount >= 1000000 -> "₺${(amount / 1000000).toInt()}M"
        amount >= 1000 -> "₺${(amount / 1000).toInt()}K"
        else -> "₺${amount.toInt()}"
    }
}
