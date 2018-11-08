package top.rechinx.meow.ui.filter.paging

sealed class NetworkState {
    class Loading: NetworkState()
    class Loaded: NetworkState()
    class Empty(val query: String): NetworkState()
    class Error(val message: String?): NetworkState()
}