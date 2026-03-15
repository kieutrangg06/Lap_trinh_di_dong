package com.nikoniche.booki.architecture.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nikoniche.booki.R
import com.nikoniche.booki.architecture.navigation.Screen


@Composable
fun DropdownNavigationMenu(
    scaffoldViewModel: ScaffoldViewModel,
    navHostController: NavHostController,
) {
    Column(
        modifier= Modifier
            .padding(top = 63.dp)
            .wrapContentSize(Alignment.TopStart)
            .background(Color.White)
            .zIndex(999f),
    ) {
        NavigationItem(
            name ="Home",
            iconDrawable = R.drawable.ic_home,
        ) {
            scaffoldViewModel.menuOpened.value = false
            navHostController.navigate(Screen.HomeScreen.route)
        }
        NavigationItem(
            name ="My books",
            iconDrawable = R.drawable.ic_bullet_list,
        ) {
            scaffoldViewModel.menuOpened.value = false
            navHostController.navigate(Screen.PersonalBooksScreen.route)
        }
//        NavigationItem(
//            name ="Reading statistics",
//            iconDrawable = R.drawable.ic_graph_outlined
//        ) {
//            scaffoldViewModel.menuOpened.value = false
//        }
        NavigationItem(
            name ="Books created by me",
            iconDrawable = R.drawable.ic_library_add
        ) {
            scaffoldViewModel.menuOpened.value = false
            navHostController.navigate(Screen.BooksCreatedByMeScreen.route)
        }
//        NavigationItem(
//            name ="Settings",
//            iconDrawable = R.drawable.ic_settings
//        ) {
//            scaffoldViewModel.menuOpened.value = false
//        }
    }
}

@Composable
fun NavigationItem(
    name: String,
    iconDrawable: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, Color.Black)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter=painterResource(iconDrawable),
            contentDescription = name,
            modifier = Modifier
                .padding(8.dp)
                .size(25.dp)
            ,
        )
        Text(
            text=name,
            fontSize=18.sp,
            fontWeight= FontWeight.Normal,
            textAlign = TextAlign.Start,
            modifier=Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground=true)
@Composable
fun DropdownNavigationMenuPreview() {
    DropdownNavigationMenu(
        viewModel(), rememberNavController(),
    )
}