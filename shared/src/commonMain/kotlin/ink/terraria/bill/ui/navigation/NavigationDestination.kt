package ink.terraria.bill.ui.navigation

import org.jetbrains.compose.resources.StringResource

interface NavigationDestination {
    val route: String
    val titleRes: StringResource
}

