package com.example.mvi_kotlin_simple

/**
 * <pre>
 *     author : shawn
 *     time   : 2023/04/16
 *     desc   :
 * </pre>
 */
sealed class MainIntent{

    object NewIntent:MainIntent()
    object BannerIntent:MainIntent()
}

//open class getBanners:MainIntent() {
//
//}
//
//open class getNews:MainIntent() {
//
//}
