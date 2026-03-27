package com.nikoniche.booki

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyHeadline(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text=text,
        fontSize=23.sp,
        fontWeight= FontWeight.Bold,
        modifier = modifier,
    )
}

@Composable
fun MyDivider(modifier: Modifier = Modifier) {
    Divider(color=Color.Black, modifier = modifier)
}

@Composable
fun MyAppColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        content = content
    )
}

@Composable
fun MyOutlinedButton(
    text: String,
    additionalContent: @Composable () -> Unit={},
    onClick: () -> Unit,
    modifier: Modifier=Modifier
) {
    Button(
        colors = ButtonDefaults
            .buttonColors(
                containerColor=Color.White,
                contentColor=Color.Black
            ),
        border = BorderStroke(2.5.dp, Color.Black),
        modifier = modifier.height(30.dp),
        shape = ButtonDefaults.filledTonalShape,
        contentPadding = PaddingValues(horizontal=10.dp, vertical=1.dp),
        onClick={
            onClick()
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth(),
        ) {

        }
        Text(text, color=Color.Black, fontSize=16.sp)
        additionalContent()
    }
}
