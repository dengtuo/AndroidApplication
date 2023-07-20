package com.wuba.android.app.utis

import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.wuba.android.app.AppEnvironment

object ToastAbility {
    private const val TAG: String = "ToastAbility"

    /**
     * 根据 R.String中的resId弹出Toast通知
     */
    @JvmStatic
    fun showToast(resId: Int) {
        AppEnvironment.application?.let {
            showToast(it.resources.getString(resId))
        }
        if (AppEnvironment.application == null) {
            Log.d(TAG, "showToast context is null")
        }
    }

    /**
     * 根据字符串弹Toast通知
     */
    @JvmStatic
    fun showToast(msg: String?) {
        showToast(msg, Gravity.CENTER)
    }

    @JvmStatic
    fun showToast(msg: String?, gravity: Int) {
        if (TextUtils.isEmpty(msg)) {
            return
        }
        innerShowToast(msg, gravity)
    }

    private fun innerShowToast(msg: String?, gravity: Int) {
        val context = AppEnvironment.application?.applicationContext
        context ?: return
        try {
            val toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            toast.setGravity(gravity, 0, 0)
            toast.show()
        } catch (e: Exception) {

        }
    }
}