package ink.terraria.bill.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.tag_education_name
import bill.shared.generated.resources.tag_entertainment_name
import bill.shared.generated.resources.tag_food_name
import bill.shared.generated.resources.tag_health_name
import bill.shared.generated.resources.tag_housing_name
import bill.shared.generated.resources.tag_other_name
import bill.shared.generated.resources.tag_salary_name
import bill.shared.generated.resources.tag_shopping_name
import bill.shared.generated.resources.tag_transport_name
import org.jetbrains.compose.resources.StringResource

data class BillTag(
    val id: Int,
    val nameRes: StringResource,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
) {
    fun icon(filled: Boolean): ImageVector = if (filled) filledIcon else outlinedIcon

    companion object {
        fun ofTagId(tagId: Int): BillTag {
            return billTags.getOrDefault(tagId, unknownTag)
        }
    }
}

val unknownTag = BillTag(
    id = 9999,
    nameRes = Res.string.tag_food_name,
    filledIcon = Icons.Filled.Restaurant,
    outlinedIcon = Icons.Outlined.Restaurant,
)

val billTags = listOf(
    BillTag(
        id = 1,
        nameRes = Res.string.tag_food_name,
        filledIcon = Icons.Filled.Restaurant,
        outlinedIcon = Icons.Outlined.Restaurant,
    ),
    BillTag(
        id = 2,
        nameRes = Res.string.tag_shopping_name,
        filledIcon = Icons.Filled.ShoppingCart,
        outlinedIcon = Icons.Outlined.ShoppingCart,
    ),
    BillTag(
        id = 3,
        nameRes = Res.string.tag_transport_name,
        filledIcon = Icons.Filled.DirectionsCar,
        outlinedIcon = Icons.Outlined.DirectionsCar,
    ),
    BillTag(
        id = 4,
        nameRes = Res.string.tag_entertainment_name,
        filledIcon = Icons.Filled.Movie,
        outlinedIcon = Icons.Outlined.Movie,
    ),
    BillTag(
        id = 5,
        nameRes = Res.string.tag_health_name,
        filledIcon = Icons.Filled.LocalHospital,
        outlinedIcon = Icons.Outlined.LocalHospital,
    ),
    BillTag(
        id = 6,
        nameRes = Res.string.tag_education_name,
        filledIcon = Icons.Filled.School,
        outlinedIcon = Icons.Outlined.School,
    ),
    BillTag(
        id = 7,
        nameRes = Res.string.tag_housing_name,
        filledIcon = Icons.Filled.Home,
        outlinedIcon = Icons.Outlined.Home,
    ),
    BillTag(
        id = 8,
        nameRes = Res.string.tag_salary_name,
        filledIcon = Icons.Filled.AttachMoney,
        outlinedIcon = Icons.Outlined.AttachMoney,
    ),
    BillTag(
        id = 9,
        nameRes = Res.string.tag_other_name,
        filledIcon = Icons.AutoMirrored.Filled.Label,
        outlinedIcon = Icons.AutoMirrored.Outlined.Label,
    ),
).associateBy { it.id }
