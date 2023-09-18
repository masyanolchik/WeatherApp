import kotlinx.coroutines.CoroutineDispatcher

interface WeatherAppDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

expect val weatherAppDispatchers: WeatherAppDispatchers