package com.softartdev.notedelight.navigation

import androidx.navigation.NavHostController
import com.softartdev.notedelight.shared.navigation.Router

class RouterImpl : Router {

    private var navController: NavHostController? = null

    override fun setController(navController: Any) {
        this.navController = navController as NavHostController
    }

    override fun releaseController() {
        navController = null
    }

    override fun navigate(route: String) = navController!!.navigate(route)

    override fun navigateClearingBackStack(route: String) {
        var popped = true
        while (popped) {
            popped = navController!!.popBackStack()
        }
        navController!!.navigate(route)
    }

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean): Boolean =
        navController!!.popBackStack(route, inclusive, saveState)

    override fun popBackStack() = navController!!.popBackStack()
}