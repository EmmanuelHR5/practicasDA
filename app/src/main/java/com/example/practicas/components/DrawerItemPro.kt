package com.example.practicas.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DrawerItemPro(
    label: String,
    icon: ImageVector,
    selectedIcon: ImageVector,
    selected: Boolean,
    extraBadge: Int? = null,
    onClick: () -> Unit
) {
    // Rebote ligero al estar seleccionado
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = ""
    )

    // Fade-in inicial
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(450),
        label = ""
    )

    NavigationDrawerItem(
        modifier = Modifier
            .graphicsLayer { this.alpha = alpha }
            .scale(scale),
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (selected) selectedIcon else icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary else Color.White
            )
        },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.White
                )

                if (extraBadge != null && extraBadge > 0) {
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = extraBadge.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    )
}
