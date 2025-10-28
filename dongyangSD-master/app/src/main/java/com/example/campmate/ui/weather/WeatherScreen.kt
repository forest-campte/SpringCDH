package com.example.campmate.ui.weather


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// OpenWeatherMap 아이콘 URL 생성
fun getIconUrl(iconId: String): String{
    return "https://openweathermap.org/img/wn/$iconId@2x.png"
}

private val iconColorFilter by lazy {
    ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(
            floatArrayOf(
                // R    G     B    A    Offset
                1.5f, 0f,   0f,   0f,  1000f,  // Red
                0f,   1.5f, 0f,   0f,  20f,  // Green
                0f,   0f,   1.0f, 0f,  0f,   // Blue
                0f,   0f,   0f,   1f,  0f    // Alpha
            )
        )
    )
}


@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val forecastState by viewModel.forecastState.collectAsState()

    // 1. 위치 권한 런처
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            // 2. 권한 승인 시 -> ViewModel에 예보 로드 요청
            viewModel.loadForecast()
        }
    }

    // 3. Composable 시작 시 권한 요청
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 4. 그라데이션 배경
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF6284FF), Color(0xFF8DA0FF), Color(0xFFB1C0FF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = forecastState) {
            is ForecastUiState.Loading -> {
                CircularProgressIndicator(color = Color.White)
                Text("날씨 예보 로딩 중...", color = Color.White, modifier = Modifier.padding(top = 16.dp))
            }
            is ForecastUiState.Error -> {
                Text("오류: ${state.message}", color = Color.White)
                // (재시도 버튼...)
            }
            is ForecastUiState.Success -> {
                // 5. '선택된 날짜' 상태 관리
                var selectedDay by remember { mutableStateOf(state.dailySummaries.firstOrNull()) }

                // 6. 메인 날씨 카드 (선택된 날짜 기준)
                selectedDay?.let {
                    WeatherDetailCard(
                        summary = it,
                        locationName = state.locationName
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. 5일 예보 선택 스크롤
                ForecastRow(
                    summaries = state.dailySummaries,
                    selectedDate = selectedDay?.date,
                    onDaySelected = { newSelectedDay ->
                        selectedDay = newSelectedDay // 날짜 선택 시 상태 업데이트
                    }
                )
            }
        }
    }
}

/**
 * 메인 날씨 정보 카드 (디자인 적용)
 */
@Composable
fun WeatherDetailCard(summary: DailyForecastSummary, locationName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = locationName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = summary.date.format(DateTimeFormatter.ofPattern("M월 d일")),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))


            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getIconUrl(summary.icon))
                    .crossfade(true)
                    .build(),
                contentDescription = summary.description,
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop,
                colorFilter =  iconColorFilter
            )

            Text(
                // (대표 온도를 표시. 여기서는 최고/최저의 평균을 사용)
                text = "${( (summary.tempMax + summary.tempMin) / 2 ).toInt()}°",
                fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = summary.description,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoChip(label = "최고", value = "${summary.tempMax.toInt()}°")
                WeatherInfoChip(label = "최저", value = "${summary.tempMin.toInt()}°")
            }
        }
    }
}

/**
 * 5일 예보 가로 스크롤 (날짜 선택)
 */
@Composable
fun ForecastRow(
    summaries: List<DailyForecastSummary>,
    selectedDate: java.time.LocalDate?,
    onDaySelected: (DailyForecastSummary) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(summaries) { daySummary ->
            ForecastDayItem(
                summary = daySummary,
                isSelected = daySummary.date == selectedDate,
                onClick = { onDaySelected(daySummary) }
            )
        }
    }
}

/**
 * 예보 스크롤의 개별 날짜 아이템
 */
@Composable
fun ForecastDayItem(
    summary: DailyForecastSummary,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color.White.copy(alpha = 0.5f)
    } else {
        Color.White.copy(alpha = 0.2f)
    }

    val isToday = summary.date == LocalDate.now()
    val itemColorFilter = if (isToday) {
        null
    } else {
        iconColorFilter
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isToday) "오늘" else summary.dayOfWeek,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        AsyncImage(
            model = getIconUrl(summary.icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            colorFilter = itemColorFilter
        )
        Text(
            text = "${summary.tempMax.toInt()}°",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Text(
            text = "${summary.tempMin.toInt()}°",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

/**
 * 최고/최저 기온 등을 표시하는 작은 칩
 */
@Composable
fun WeatherInfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}