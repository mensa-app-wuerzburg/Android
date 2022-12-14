package de.erikspall.mensaapp.domain.usecases.foodproviders

import de.erikspall.mensaapp.domain.interfaces.data.AppRepository
import de.erikspall.mensaapp.domain.model.FoodProvider

class FetchFoodProvider(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(
        foodProviderId: Int
    ): Result<FoodProvider> {
        return appRepository.fetchFoodProvider(foodProviderId)
    }
}