package top.rechinx.meow.rikka.misc

/**
 * Create a resource model for view model, [status] for current resource, [data] for data,
 * [message] represents error message
 */
sealed class Resource<out T> constructor(val status: ResourceState, val data: T?, val message: String?) {

    class Success<T>(val value: T):
            Resource<T>(ResourceState.SUCCESS, value, null)

    class Error<T>(val error: String?):
            Resource<T>(ResourceState.ERROR, null, error)

    class Loading<T>:
            Resource<T>(ResourceState.LOADING, null, null)

}