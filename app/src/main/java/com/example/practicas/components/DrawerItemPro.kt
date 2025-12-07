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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.practicas.viewmodel.MusicViewModel

// ==========================================================
// COMPONENTE PRINCIPAL DE ITEM DEL DRAWER
// ==========================================================

@Composable
fun DrawerItemPro(
    label: String,
    icon: ImageVector,
    selectedIcon: ImageVector,
    selected: Boolean,
    extraBadge: Int? = null,
    onClick: () -> Unit
) {
    val colorPrimary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = ""
    )

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
                tint = if (selected) colorPrimary else onSurface
            )
        },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    color = if (selected) colorPrimary else onSurface
                )

                if (extraBadge != null && extraBadge > 0) {
                    Spacer(modifier = Modifier.width(6.dp))

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    colorPrimary,
                                    CircleShape
                                )
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = extraBadge.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    )
}

// ==========================================================
// CONTENIDO DEL DRAWER (LISTA DE PLAYLISTS Y FAVORITOS)
// ==========================================================

@Composable
fun DrawerContent(
    navController: NavHostController,
    musicVM: MusicViewModel,
    currentRoute: String?,
    closeDrawer: () -> Unit
) {
    val onBackground = MaterialTheme.colorScheme.onBackground

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // ---------------- TÍTULO PLAYLISTS ----------------
        Text(
            text = "Playlists",
            style = MaterialTheme.typography.titleMedium,
            color = onBackground,
            modifier = Modifier.padding(16.dp)
        )

        // Cada playlist
        NavigationDrawerItem(
            label = { Text("IM BORED") },
            selected = false,
            onClick = {
                musicVM.loadImBored()
                navController.navigate("home") { launchSingleTop = true }
                closeDrawer()
            },
            icon = {
                Icon(
                    Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = onBackground
                )
            }
        )

        NavigationDrawerItem(
            label = { Text("Favorito (Spotify)") },
            selected = false,
            onClick = {
                musicVM.loadFavorito()
                navController.navigate("home") { launchSingleTop = true }
                closeDrawer()
            },
            icon = {
                Icon(
                    Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = onBackground
                )
            }
        )

        NavigationDrawerItem(
            label = { Text("GYM TRAINING") },
            selected = false,
            onClick = {
                musicVM.loadGymTraining()
                navController.navigate("home") { launchSingleTop = true }
                closeDrawer()
            },
            icon = {
                Icon(
                    Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = onBackground
                )
            }
        )

        NavigationDrawerItem(
            label = { Text("She Is Just A Girl") },
            selected = false,
            onClick = {
                musicVM.loadShesIsJustAGirl()
                navController.navigate("home") { launchSingleTop = true }
                closeDrawer()
            },
            icon = {
                Icon(
                    Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = onBackground
                )
            }
        )

        // ---------------- TÍTULO BIBLIOTECA PERSONAL ----------------
        Text(
            text = "Tu biblioteca",
            style = MaterialTheme.typography.titleMedium,
            color = onBackground,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
        )

        val liked by musicVM.likedTracks.collectAsState()

        DrawerItemPro(
            label = "Favoritos ❤️",
            icon = Icons.Default.FavoriteBorder,
            selectedIcon = Icons.Default.Favorite,
            selected = currentRoute == "liked",
            extraBadge = liked.size,
            onClick = {
                navController.navigate("liked") { launchSingleTop = true }
                closeDrawer()
            }
        )
    }
}
