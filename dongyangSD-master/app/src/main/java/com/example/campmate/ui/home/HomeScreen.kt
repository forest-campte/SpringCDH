package com.example.campmate.ui.home

import android.R.attr.fontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.campmate.data.model.Campsite


import com.example.campmate.data.model.AdminZoneGroup


@Composable
fun HomeScreen(
    onCampsiteClick: (Int) -> Unit, // (Int) -> Unit 은 숫자(ID) 하나를 받는 클릭 동작이라는 의미입니다.
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is HomeUiState.Success -> {

//                CampsiteList(
//                    campsites = state.campsites,
//                    onCampsiteClick = onCampsiteClick
//                )
                AdminGroupList(
                    adminGroups = state.adminGroups, // adminGroups 전달
                    onCampsiteClick = onCampsiteClick
                )
            }
            is HomeUiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

//1030cdh
//@Composable
//fun CampsiteList(campsites: List<Campsite>, onCampsiteClick: (Int) -> Unit) {
//    LazyColumn(
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(campsites) { campsite ->
//            CampsiteCard(
//                campsite = campsite,
//                onClick = { onCampsiteClick(campsite.id) }
//            )
//        }
//    }
//}
@Composable
fun AdminGroupList(adminGroups: List<AdminZoneGroup>, onCampsiteClick: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(adminGroups) { group ->
            AdminGroupItem(group = group, onCampsiteClick = onCampsiteClick)
        }
    }
}
@Composable
fun AdminGroupItem(group: AdminZoneGroup, onCampsiteClick: (Int) -> Unit) {
    Column {
        // 1. 캠핑장 이름 (그룹 제목)
        Text(
            text = group.name, // 관리자 이름 표시
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 2. 캠핑존 카드 (가로 스크롤)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(group.sites) { campsite ->

                CampsiteCard(
                    campsite = campsite,
                    onClick = { onCampsiteClick(campsite.id) }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteCard(campsite: Campsite, onClick: () -> Unit) {
    Card(
        onClick = onClick,
//        modifier = Modifier.fillMaxWidth(),
        //1030cdh
        modifier = Modifier.width(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(campsite.imageUrl),
                contentDescription = campsite.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    //1030cdh
                    text = campsite.name ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    //1030cdh
                    text = campsite.description?:"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2 // 여러 줄 방지
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        //1030cdh
                        text = String.format("%.1f", campsite.rating),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}