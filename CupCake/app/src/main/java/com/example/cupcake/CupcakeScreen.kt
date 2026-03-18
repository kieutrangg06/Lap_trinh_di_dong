package com.example.cupcake

import androidx.annotation.StringRes
import com.example.cupcake.R

/**
 * Enum đại diện cho các màn hình trong ứng dụng Cupcake
 */
enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.choose_flavor),
    Pickup(title = R.string.choose_pickup_date),
    Summary(title = R.string.order_summary)
}