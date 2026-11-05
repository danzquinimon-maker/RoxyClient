
package com.roxyclient.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxyclient.model.MenuItem
import com.roxyclient.ui.components.MenuItemButton
import com.roxyclient.ui.components.StatusIndicator

@Composable
fun MainMenuScreen() {
    var currentMenu by remember { mutableStateOf<MenuItem?>(null) }
    var isConnected by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = currentMenu,
            label = "menu_transition",
            modifier = Modifier.fillMaxSize()
        ) { targetMenu ->
            if (targetMenu == null) {
                MainMenu(
                    onMenuItemClick = { currentMenu = it },
                    isConnected = isConnected
                )
            } else {
                CategoryScreen(
                    menuItem = targetMenu,
                    onBackClick = { currentMenu = null }
                )
            }
        }
    }
}

@Composable
fun MainMenu(
    onMenuItemClick: (MenuItem) -> Unit,
    isConnected: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎮",
                    fontSize = 48.sp
                )

                Text(
                    text = "ROXY CLIENT",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Minecraft Bedrock Utility Client",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = 0.85f
                    )
                )
            }
        }

        // Connection status
        StatusIndicator(
            isConnected = isConnected,
            modifier = Modifier.fillMaxWidth()
        )

        // Menu categories
        Text(
            text = "CLIENT FEATURES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = 4.dp,
                top = 8.dp,
                bottom = 4.dp
            )
        )

        MenuItem.all().forEach { item ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                MenuItemButton(
                    title = item.title,
                    icon = item.icon,
                    onClick = {
                        onMenuItemClick(item)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Footer
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "ROXY CLIENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Bedrock 26.60+",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Version 1.0.0",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CategoryScreen(
    menuItem: MenuItem,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back button and category title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onBackClick() }
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "←",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = menuItem.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = menuItem.icon,
                fontSize = 24.sp
            )
        }

        when (menuItem) {
            is MenuItem.Combat -> CombatCategoryScreen()
            is MenuItem.Movement -> MovementCategoryScreen()
            is MenuItem.Visuals -> VisualsCategoryScreen()
            is MenuItem.Player -> PlayerCategoryScreen()
            is MenuItem.World -> WorldCategoryScreen()
            is MenuItem.HUD -> HUDCategoryScreen()
            is MenuItem.Settings -> SettingsCategoryScreen()
        }
    }
}

@Composable
private fun FeaturePanel(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CombatCategoryScreen() {
    FeaturePanel(
        title = "⚔️ Interaction Information",
        description = """
            Player Information

            Health Status

            Session Information

            Online Player List
        """.trimIndent()
    )
}

@Composable
fun MovementCategoryScreen() {
    FeaturePanel(
        title = "🏃 Movement",
        description = """
            Movement Information

            Speed Display

            Coordinate Tracking

            Direction Indicator
        """.trimIndent()
    )
}

@Composable
fun VisualsCategoryScreen() {
    FeaturePanel(
        title = "👁️ Visual Utilities",
        description = """
            Entity Names

            Coordinates HUD

            Time Display

            Direction Indicator
        """.trimIndent()
    )
}

@Composable
fun PlayerCategoryScreen() {
    FeaturePanel(
        title = "👤 Player Information",
        description = """
            Coordinates

            Direction

            Current Dimension

            Health Status

            Player Information
        """.trimIndent()
    )
}

@Composable
fun WorldCategoryScreen() {
    FeaturePanel(
        title = "🌍 World Information",
        description = """
            Coordinates

            Biome Information

            Time Display

            Nearby Entities

            World Information
        """.trimIndent()
    )
}

@Composable
fun HUDCategoryScreen() {
    FeaturePanel(
        title = "📊 HUD Elements",
        description = """
            Coordinates

            Direction Indicator

            Player Status

            Custom Watermark
        """.trimIndent()
    )
}

@Composable
fun SettingsCategoryScreen() {
    FeaturePanel(
        title = "⚙️ Settings",
        description = """
            Menu Animation

            Menu Size

            Button Position

            Theme Options

            Audio Controls

            Reset to Default
        """.trimIndent()
    )
}
