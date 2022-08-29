package de.erikspall.mensaapp.domain.model.interfaces

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import de.erikspall.mensaapp.domain.model.Canteen
import de.erikspall.mensaapp.domain.model.enums.FoodProviderType
import de.erikspall.mensaapp.domain.model.enums.Location
import java.util.*

interface FoodProvider {
    @DrawableRes
    fun getImageResourceId(): Int
    fun getType(): FoodProviderType
    fun getName(): String
    fun getLocation(): Location
    fun getTitleInfo(): String
    fun getOpeningHours(): List<OpeningInfo>
    fun getAdditionalInfo(): String
    fun getMenus(): List<Menu>
    fun getMenuOfDay(): Optional<Menu>

    companion object {
        fun createCanteen(
            name: String,
            location: Location,
            titleInfo: String,
            openingHours: List<OpeningInfo>,
            additionalInfo: String,
            menus: List<Menu>,
            @DrawableRes imageResourceId: Int,
            type: FoodProviderType
        ): FoodProvider {
            return Canteen(
                name,
                location,
                titleInfo,
                openingHours,
                additionalInfo,
                menus,
                imageResourceId,
                type
            )
        }
    }
}