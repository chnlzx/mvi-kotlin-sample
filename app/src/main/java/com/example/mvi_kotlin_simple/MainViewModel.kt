package com.example.mvi_kotlin_simple

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * <pre>
 *     author : shawn
 *     time   : 2023/04/16
 *     desc   :
 * </pre>
 */
class MainViewModel() : ViewModel() {


    var _channel: Channel<MainIntent> = Channel<MainIntent>()

    var channelFLow: Flow<MainIntent> = _channel.receiveAsFlow()


    private var _uistateFlow = MutableStateFlow(UIState(UINewsState.INIT, UIBannerState.INIT))//

    val uistateFlow: StateFlow<UIState> = _uistateFlow.asStateFlow()//对外暴露一个只读的stateflow


    init {
        handlerIntent()
    }

    private val users: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>().also {
            loadUsers()
        }
    }

    fun getUsers(): LiveData<List<String>> {
        return users
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }

    /**
     * @description 获取多个意图 ,多态
     * @date: 2023/4/16 12:15
     * @author: shawn
     * @param
     * @return
     */
    fun getIntent(newIntent: MainIntent) {

        viewModelScope.launch {
            _channel.send(newIntent)
        }
    }

    /**
     * @description 处理多个意图
     * @date: 2023/4/16 12:21
     * @author: shawn
     * @param
     * @return
     */
    private fun handlerIntent() {

        viewModelScope.launch(Dispatchers.IO) {
            channelFLow.collectLatest {
                //如果没有异步任务，两个方法同时调用delay(2000)后会出现任务丢失的情况
                when (it) {
                    is MainIntent.NewIntent -> async { requestGetNews() }
                    is MainIntent.BannerIntent -> async { requestGetBanners() }
                }
            }
        }



    }

    private suspend fun requestGetBanners() {
        delay(5*1000)
        //假设这已经获取到了网络数据list
        val list =
            mutableListOf("${System.currentTimeMillis()}====广告01", "广告02", "广告03", "广告04", "广告05")
        _uistateFlow.update {
            it.copy(uiBannerState = UIBannerState.SUCCESS(list))
//            it.copy(uiBannerState = UIBannerState.ERROR("出错"))
        }

    }


    private suspend fun requestGetNews() {
        delay(2000)
        //假设这已经获取到了网络数据list
        val list =
            mutableListOf("${System.currentTimeMillis()}====新闻01", "新闻02", "新闻03", "新闻04", "新闻05")

        _uistateFlow.value = uistateFlow.value.copy(uiNewState = UINewsState.SUCCESS(list))
    }


    private fun sentUIState(uistate: UIState) {


    }


}