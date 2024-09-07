package com.reysl.uroboros

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reysl.uroboros.data.db.tag_db.TagViewModel
import com.reysl.uroboros.pages.ItemCard

@Composable
fun TagHomeContent(tagViewModel: TagViewModel) {
    val tagList by tagViewModel.tagList.observeAsState(emptyList())
    if (tagList.isNotEmpty()) {
        var currentItem by remember {
            mutableStateOf(tagList[0])
        }
        var isFilled by remember {
            mutableStateOf(false)
        }
        IconButton(onClick = {
            isFilled = !isFilled
        }) {
            if (isFilled) {
                Image(
                    painter = painterResource(id = R.drawable.star_filled),
                    contentDescription = "Favourite",
                    modifier = Modifier
                        .size(30.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Favourite",
                    modifier = Modifier
                        .size(30.dp)
                )
            }

        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            items(tagList) { item ->
                ItemCard(item, currentItem) {
                    currentItem = item
                }
            }
        }
    } else {
//        Text(
//            text = "Нет доступных меток",
//            fontFamily = acherusFeral,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Light,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            textAlign = TextAlign.Center
//        )
    }

}