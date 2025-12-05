package com.example.practicas.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.practicas.components.DrawerItemPro
import com.example.practicas.viewmodel.MusicViewModel
import com.example.practicas.views.AccountView
import com.example.practicas.views.HomeScreen
import com.example.practicas.views.LikedSongsScreen
import com.example.practicas.views.SearchScreen
import com.example.practicas.views.TrackDetailScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    musicVM: MusicViewModel,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                musicVM = musicVM,
                currentRoute = currentRoute,
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Spotify Client") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (isSystemInDarkTheme())
                                    Icons.Default.LightMode
                                else
                                    Icons.Default.DarkMode,
                                contentDescription = "Cambiar tema"
                            )
                        }
                    }
                )
            },

            bottomBar = {
                NavigationBar {
                    val route = currentRoute

                    NavigationBarItem(
                        selected = route == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, "Inicio") }
                    )

                    NavigationBarItem(
                        selected = route == "search",
                        onClick = {
                            navController.navigate("search") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Search, "Buscar") }
                    )

                    NavigationBarItem(
                        selected = route == "account",
                        onClick = {
                            navController.navigate("account") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.AccountCircle, "Cuenta") }
                    )
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {

                composable("home") {
                    HomeScreen(
                        viewModel = musicVM,
                        navController = navController
                    )
                }

                composable("search") {
                    SearchScreen(
                        viewModel = musicVM,
                        navController = navController
                    )
                }

                composable("account") {
                    AccountView(
                        viewModel = musicVM,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                composable("trackDetail") {
                    val track by musicVM.selectedTrack.collectAsState()
                    val original by musicVM.lyricsOriginal.collectAsState()
                    val translated by musicVM.lyricsTranslated.collectAsState()

                    track?.let {
                        TrackDetailScreen(
                            track = it,
                            lyricsOriginal = original,
                            lyricsTranslated = translated,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

                // 🆕 Pantalla de favoritos (playlist "me gusta")
                composable("liked") {
                    LikedSongsScreen(
                        viewModel = musicVM,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    musicVM: MusicViewModel,
    currentRoute: String?,
    closeDrawer: () -> Unit
) {
    ModalDrawerSheet {

        Text(
            text = "Playlists",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        NavigationDrawerItem(
            label = { Text("IM BORED") },
            selected = false,
            onClick = {
                musicVM.loadImBored()
                navController.navigate("home")
                closeDrawer()
            },
            icon = { Icon(Icons.Default.PlaylistPlay, null) }
        )

        NavigationDrawerItem(
            label = { Text("Favorito (Spotify)") },
            selected = false,
            onClick = {
                musicVM.loadFavorito()
                navController.navigate("home")
                closeDrawer()
            },
            icon = { Icon(Icons.Default.PlaylistPlay, null) }
        )

        NavigationDrawerItem(
            label = { Text("GYM TRAINING") },
            selected = false,
            onClick = {
                musicVM.loadGymTraining()
                navController.navigate("home")
                closeDrawer()
            },
            icon = { Icon(Icons.Default.PlaylistPlay, null) }
        )

        NavigationDrawerItem(
            label = { Text("She Is Just A Girl") },
            selected = false,
            onClick = {
                musicVM.loadShesIsJustAGirl()
                navController.navigate("home")
                closeDrawer()
            },
            icon = { Icon(Icons.Default.PlaylistPlay, null) }
        )

        // 🆕 Sección de biblioteca personal
        Text(
            text = "Tu biblioteca",
            style = MaterialTheme.typography.titleMedium,
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
                navController.navigate("liked")
                closeDrawer()
            }
        )
    }
}
