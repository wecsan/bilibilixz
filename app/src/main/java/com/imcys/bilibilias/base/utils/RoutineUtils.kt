package com.imcys.bilibilias.base.utils

import android.content.Context
import android.util.Log



//顶层方法
fun asLogD(tag: String, content: String) = Log.d(tag, content)

fun asLogE(tag: String, content: String) = Log.e(tag, content)

fun asLogV(tag: String, content: String) = Log.v(tag, content)

fun asLoW(tag: String, content: String) = Log.w(tag, content)

fun asLogD(context: Context, content: String) = Log.d(context::class.java.simpleName, content)

fun asLogE(context: Context, content: String) = Log.e(context::class.java.simpleName, content)

fun asLogV(context: Context, content: String) = Log.v(context::class.java.simpleName, content)

fun asLoW(context: Context, content: String) = Log.w(context::class.java.simpleName, content)