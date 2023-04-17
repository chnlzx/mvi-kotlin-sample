package com.example.mvi_kotlin_simple

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mvi_kotlin_simple.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * @description 演示一个基本的MVI范式(模拟网络请求新闻列表、广告列表)
 * @date: 2023/4/16 11:18
 * @author: shawn
 * @step: 1. 创建一个viewmodel，并在activity引用
 *       2. 分析业务中有哪些UIIntent和UIState并创建(抓取新闻intent、抓取广告intent)
 *       3. 查看引用的库协程库、viewmodel（"androidx.activity:activity-ktx:1.6.0"）
 *       4. 创建一个网络请求，发起网络请求，并返回数据
 *       5. 遇到的问题汇总：① 定义UIState混乱 ② 对MutableStateFlow和Channel缺乏了解
 *                       ③ 多个intent同时并行发送，获取到data后，只获取到一个data反馈解决
 *                          方案：使用distinctUntilChanged，实现局部刷新数据
 *                       ④ 模拟多条接口网络延时发送数据，出现任务丢失的情况，方案：应当使用
 *                          多个异步任务去模拟网络请求，而不是在同一个协程job里，主要缺少对
 *                          协程的认识。
 *
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val model: MainViewModel by viewModels() //只读权限 "androidx.activity:activity-ktx:1.6.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initListeners()
    }


    private fun initData() {
        //这个一个livedata使用演示
        model.getUsers().observe(this) {
            println(it)
        }

        lifecycleScope.launch {
            model.uistateFlow.map {
                it.uiNewState
            }.distinctUntilChanged()
                .collectLatest { uiNewsState ->
                when (uiNewsState) {
                    is UINewsState.ERROR -> { println("news error") }
                    is UINewsState.INIT -> { println("news init") }
                    is UINewsState.SUCCESS -> {
                        val list = uiNewsState.list
                        println(list.toString())
                    }
                }
            }
        }


        lifecycleScope.launchWhenStarted {

//            if (model.uistateFlow is UIState.UIBannerState) {
//                model.uistateFlow.collect{
//                    println("更新广告")
//                }
//            }

            model.uistateFlow.map { it.uiBannerState }
                .distinctUntilChanged()
                .collectLatest { uIBannerState ->
                    when (uIBannerState) {
                        is UIBannerState.ERROR -> println("news error")
                        is UIBannerState.INIT -> println("banners init")
                        is UIBannerState.SUCCESS -> { println(uIBannerState.list) }
                    }
                }
        }


    }

    private fun initListeners() {
        binding.btn.setOnClickListener {
            model.getIntent(MainIntent.NewIntent)
            model.getIntent(MainIntent.BannerIntent)
        }

    }


}