package com.weatherapp.ui.locationselection.store

import com.arkivanov.mvikotlin.core.store.Store
import com.weatherapp.core.model.Location

interface LocationSelectionStore
    : Store<LocationSelectionStore.Intent, LocationSelectionStore.State, LocationSelectionStore.Label> {

    sealed class Intent {
        data class EnterSearchQueryIntent(val searchQuery: String): Intent()
        data class ChoseCurrentLocation(val location: Location): Intent()
    }

    sealed class State() {
        open val searchQuery: String = ""

        class Loading(override val searchQuery: String): State() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Loading

                if (searchQuery != other.searchQuery) return false

                return true
            }

            override fun hashCode(): Int {
                return searchQuery.hashCode()
            }
        }

        class Error(override val searchQuery: String, val errorMessage: String): State() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Error

                if (searchQuery != other.searchQuery) return false
                if (errorMessage != other.errorMessage) return false

                return true
            }

            override fun hashCode(): Int {
                var result = searchQuery.hashCode()
                result = 31 * result + errorMessage.hashCode()
                return result
            }
        }

        class FoundLocations(
            override val searchQuery: String,
            val locations: List<Location>
        ): State() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as FoundLocations

                if (searchQuery != other.searchQuery) return false
                if (locations != other.locations) return false

                return true
            }

            override fun hashCode(): Int {
                var result = searchQuery.hashCode()
                result = 31 * result + locations.hashCode()
                return result
            }
        }
    }

    sealed class Label {
        data class CurrentLocationChosen(val location: Location): Label()
        data class CurrentLocationChosingError(val location: Location, val errorMessage: String): Label()
    }
}