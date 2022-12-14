package de.erikspall.mensaapp.ui.settings.mealcomponents.viewmodel

import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.erikspall.mensaapp.R
import de.erikspall.mensaapp.domain.enums.AdditiveType
import de.erikspall.mensaapp.domain.model.Additive
import de.erikspall.mensaapp.domain.usecases.additives.AdditiveUseCases
import de.erikspall.mensaapp.domain.usecases.sharedpreferences.SharedPreferenceUseCases
import de.erikspall.mensaapp.ui.settings.mealcomponents.event.AllergenicEvent
import de.erikspall.mensaapp.ui.settings.mealcomponents.viewmodel.state.MealComponentState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealComponentViewModel @Inject constructor(
        private val sharedPreferences: SharedPreferenceUseCases,
        private val additiveUseCases: AdditiveUseCases
) : ViewModel() {
    val state = MealComponentState(
            warningsActivated = MutableLiveData(sharedPreferences.getBoolean(R.string.setting_warnings_enabled, false)),
            allergens = additiveUseCases.getAdditives(AdditiveType.ALLERGEN),
            ingredients = additiveUseCases.getAdditives(AdditiveType.INGREDIENT)
    )
    fun onEvent(event: AllergenicEvent) {
        when (event) {
            is AllergenicEvent.Init -> {
                viewModelScope.launch {
                    additiveUseCases.fetchLatest()
                }
            }
            is AllergenicEvent.OnWarningsChanged -> {
                sharedPreferences.setBoolean(R.string.setting_warnings_enabled, event.warningsActivated)
                state.warningsActivated.postValue(event.warningsActivated) // TODO change to sharedPreferences listener
            }
            is AllergenicEvent.OnAdditiveChecked -> {
                viewModelScope.launch {
                    additiveUseCases.setAdditiveLikeStatus(event.name, event.type, event.checked)
                }
            }
        }
    }
}