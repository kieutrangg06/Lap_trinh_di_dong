package com.example.cupcake

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun OrderSummaryScreen(
    orderUiState: OrderUiState,
    onCancelButtonClicked: () -> Unit,
    onSendButtonClicked: () -> Unit,
    onNavigateUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Tính price ngay tại đây (giá mỗi cái = $2.00)
    val pricePerCupcake = 2.00
    val totalPrice = String.format("$%.2f", orderUiState.quantity * pricePerCupcake)

    Scaffold(
        topBar = {
            CupcakeTopAppBar(
                title = stringResource(R.string.choose_pickup_date),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        Text(
            text = stringResource(R.string.order_summary),
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Quantity: ${orderUiState.quantity} cupcake${if (orderUiState.quantity > 1) "s" else ""}")
        Text("Flavor: ${orderUiState.flavor}")
        Text("Pickup Date: ${orderUiState.date}")
        Text("Total: $totalPrice")   // ← dùng biến vừa tính

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancelButtonClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = {
                    shareOrder(context, orderUiState, totalPrice)  // truyền thêm totalPrice
                    onSendButtonClicked()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Send Order")
            }
        }}
    }
}

private fun shareOrder(context: Context, order: OrderUiState, totalPrice: String) {
    val shareText = """
        Quantity: ${order.quantity} cupcake${if (order.quantity > 1) "s" else ""}
        Flavor: ${order.flavor}
        Pickup date: ${order.date}
        Total: $totalPrice
        Thank you!
    """.trimIndent()

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}