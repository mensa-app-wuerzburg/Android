package de.erikspall.mensaapp.ui.foodproviderlist.canteenlist

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.erikspall.mensaapp.R
import de.erikspall.mensaapp.domain.enums.Category
import de.erikspall.mensaapp.domain.enums.Location
import de.erikspall.mensaapp.domain.enums.StringResEnum
import de.erikspall.mensaapp.domain.usecases.foodproviders.FoodProviderUseCases
import de.erikspall.mensaapp.domain.usecases.openinghours.OpeningHourUseCases
import de.erikspall.mensaapp.domain.usecases.sharedpreferences.SharedPreferenceUseCases
import de.erikspall.mensaapp.ui.foodproviderlist.event.FoodProviderListEvent
import de.erikspall.mensaapp.ui.foodproviderlist.state.FoodProviderListState
import de.erikspall.mensaapp.ui.state.UiState
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CanteenListViewModel @Inject constructor(
    private val foodProviderUseCases: FoodProviderUseCases,
    private val openingHourUseCases: OpeningHourUseCases,
    private val sharedPreferences: SharedPreferenceUseCases,
) : ViewModel() {

    val state = FoodProviderListState()

    fun onEvent(event: FoodProviderListEvent) {
        when (event) {
            is FoodProviderListEvent.Init -> {
                val newLocationValue = sharedPreferences.getValueRes(
                    R.string.shared_pref_location,
                    R.string.location_wuerzburg
                )

                if (state.location.getValue() != newLocationValue ||
                    state.foodProviders.value == null ||
                    state.foodProviders.value!!.isEmpty()
                ) {
                    state.location = StringResEnum.locationFrom(newLocationValue)
                    state.uiState.value = UiState.LOADING
                    onEvent(FoodProviderListEvent.GetLatest)
                }
            }
            is FoodProviderListEvent.SetUiState -> { // TODO this should not exist
                state.uiState.postValue(event.uiState)
            }
            is FoodProviderListEvent.GetLatest -> {
                viewModelScope.launch {
                    Log.d("$TAG:fetchingProcess", "Starting to fetch ...")
                    val test = foodProviderUseCases.fetchAll(
                        location = state.location,
                        category = Category.CANTEEN
                    )
                    Log.d("$TAG:fetchingProcess", "Fetching ended ...")
                    if (test.isSuccess) {
                        Log.d("$TAG:fetchingProcess", "Setting ${test.getOrThrow().size} items ...")
                        state.foodProviders.value = test.getOrThrow()
                        state.uiState.postValue(UiState.NORMAL)
                    } else {
                        Log.d("$TAG:fetchingProcess", "Error!")
                        state.uiState.postValue(UiState.ERROR)
                        Log.d("$TAG:fetchingCanteens", test.exceptionOrNull()!!.toString())
                    }
                    // Notify Fragment that it should update its list
                    //state.receivedData.postValue(!state.receivedData.value!!)
                }

            }
            is FoodProviderListEvent.UpdateOpeningHours -> {
                state.foodProviders.value ?: return

                Log.d("$TAG:event-updating-hours", "Let's update!")

                // TODO: Only assign once
                state.foodProviders.value!!.forEach { oldCanteen ->
                    oldCanteen.openingHoursString = openingHourUseCases.formatToString(
                        oldCanteen.openingHours,
                        LocalDateTime.now(),
                        Locale.getDefault()
                    )
                }
                // Notify Fragment that it should update its list
                // state.receivedData.postValue(!state.receivedData.value!!)
            }
        }
    }

    companion object {
        const val TAG = "CanteenListViewModel"
    }
}