package com.example.permissioncameraandgalary

import android.app.Application

//B3: Tạo context cho toàn App sử dụng
class CustomAppication : Application(){
    companion object {
        lateinit var instance: CustomAppication
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}