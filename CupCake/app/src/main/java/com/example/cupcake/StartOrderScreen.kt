package com.example.cupcake

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cupcake.R

@Composable
fun StartOrderScreen(
    onNextButtonClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ==================== THÊM ẢNH CUPCAKE Ở TRANG ĐẦU ====================
        Image(
            painter = painterResource(id = R.drawable.cupcake),  // ← Đặt tên file là cupcake.png hoặc cupcake.jpg
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onNextButtonClicked(1) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.one_cupcake))
            }
            Button(
                onClick = { onNextButtonClicked(6) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.six_cupcakes))
            }
            Button(
                onClick = { onNextButtonClicked(12) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.twelve_cupcakes))
            }
        }
    }
}