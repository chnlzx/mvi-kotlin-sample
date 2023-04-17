package com.example.mvi_kotlin_simple

/**
 * <pre>
 *     author : shawn
 *     time   : 2023/04/16
 *     desc   : 错误的定义方式,对定义这个UIState类不清晰？？？
 * </pre>
 */

data class UIState(var uiNewState: UINewsState, var uiBannerState: UIBannerState)

sealed class UINewsState {

    object INIT : UINewsState()
    data class SUCCESS(var list: List<String>) : UINewsState()
    data class ERROR(var ex: String) : UINewsState()
}


sealed class UIBannerState {

    object INIT : UIBannerState()
    data class SUCCESS(var list: List<String>) : UIBannerState()
    data class ERROR(var ex: String) : UIBannerState()
}


//错误的定义方式
//interface  UIState{
//
//
//    sealed class UINewsState {
//
//
//        class loading(): UIState
//        class success(var list: List<String>): UIState
//        class error(var ex:String): UIState
//    }
//
//
//    sealed class UIBannerState {
//
//        class loading(): UIState
//        class success(var list: List<String>): UIState
//        class error(var ex:String): UIState
//    }
//
//
//}