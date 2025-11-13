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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.campmate.ui.weather.DailyForecastSummary // ❗️ ViewModel의 상태 클래스 임포트
import com.example.campmate.ui.weather.SelectableLocation // ❗️ ViewModel의 상태 클래스 임포트
import com.example.campmate.ui.weather.WeatherViewModel // ❗️ ViewModel 임포트
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// --- 요청하신 테마 색상 정의 ---
val theme_light_primary = Color(0xFF226C2E)
val theme_light_secondary = Color(0xFF53634B)
val theme_light_background = Color(0xFFF9FAEF)
val theme_light_surfaceVariant = Color(0xFFDEE5D9) // Card/Tile Borders
val theme_light_secondaryContainer = Color(0xFFD6E8CA) // Tiled Backgrounds
val theme_light_onPrimary = Color.White
val theme_light_onSecondaryContainer = Color(0xFF111F0D) // secondaryContainer 위의 텍스트

// --- 채도 필터 ---
private val sunnyColorFilter by lazy {
    val saturationMatrix = ColorMatrix()
    saturationMatrix.setSaturation(1.5f)
    ColorFilter.colorMatrix(saturationMatrix)
}

// OpenWeatherMap 아이콘 URL 생성
fun getIconUrl(iconId: String) = "https://openweathermap.org/img/wn/$iconId@4x.png"

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
            // 권한 승인 (ViewModel의 init에서 이미 로드를 시도했음)
        }
    }

    // 2. Composable 시작 시 권한 요청
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 3. 새 테마 배경 적용
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme_light_background) // ⬅️ 새 배경색
            .verticalScroll(rememberScrollState()) // 스크롤 가능하도록
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 4. 위치 선택기 ---
        LocationSelector(
            availableLocations = forecastState.availableLocations,
            selectedLocationId = forecastState.selectedLocationId,
            onLocationSelected = { locationId ->
                viewModel.onLocationSelected(locationId)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 5. 날씨 정보 표시 ---
        if (forecastState.isLoading && forecastState.dailySummaries.isEmpty()) {
            // 초기 로딩
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 64.dp),
                color = theme_light_primary
            )
            Text(
                "날씨 정보 로딩 중...",
                color = theme_light_secondary,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else if (forecastState.errorMessage != null) {
            // 에러
            Text("오류: ${forecastState.errorMessage}", color = Color.Red)
        } else if (forecastState.dailySummaries.isNotEmpty()) {
            // --- 6. 데이터가 있을 때 ---

            var selectedDay by remember {
                mutableStateOf(forecastState.dailySummaries.first())
            }

            LaunchedEffect(forecastState.dailySummaries) {
                selectedDay = forecastState.dailySummaries.first()
            }

            // 6-1. 메인 날씨 카드
            WeatherDetailCard(
                summary = selectedDay,
                locationName = forecastState.locationDisplayName,
                isLoading = forecastState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6-2. 5일 예보 선택 스크롤
            ForecastRow(
                summaries = forecastState.dailySummaries,
                selectedDate = selectedDay.date,
                onDaySelected = { newSelectedDay ->
                    selectedDay = newSelectedDay
                }
            )
        }
    }
}

/**
 * 1. (새 Composable) 위치 선택기 (상단 칩 그룹)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelector(
    availableLocations: List<SelectableLocation>,
    selectedLocationId: String,
    onLocationSelected: (String) -> Unit
) {
    if (availableLocations.size > 1) { // 선택지가 2개 이상일 때만 표시
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableLocations, key = { it.id }) { location ->
                val isSelected = location.id == selectedLocationId

                FilterChip(
                    selected = isSelected,
                    onClick = { onLocationSelected(location.id) },
                    label = { Text(location.name) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = theme_light_secondaryContainer,
                        labelColor = theme_light_onSecondaryContainer,
                        selectedContainerColor = theme_light_primary, // ⬅️ 선택됨 (Primary)
                        selectedLabelColor = theme_light_onPrimary // ⬅️ 선택됨 (White)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = theme_light_surfaceVariant,
                        selectedBorderColor = theme_light_primary
                    )
                )
            }
        }
    }
}


/**
 * 2. 메인 날씨 정보 카드 (색상 수정)
 */
@Composable
fun WeatherDetailCard(
    summary: DailyForecastSummary,
    locationName: String,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme_light_surfaceVariant, RoundedCornerShape(20.dp)), // ⬅️ 테두리
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // ⬅️ 배경색 (F9FAEF) 위에 흰색 카드
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                color = theme_light_primary // ⬅️ Primary
            )
            Text(
                text = summary.date.format(DateTimeFormatter.ofPattern("M월 d일")),
                fontSize = 16.sp,
                color = theme_light_secondary // ⬅️ Secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(getIconUrl(summary.icon))
                        .crossfade(true)
                        .build(),
                    contentDescription = summary.description,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // 칩을 눌러 위치 변경 시 로딩 스피너 표시
                if (isLoading) {
                    CircularProgressIndicator(color = theme_light_primary)
                }
            }

            Text(
                text = "${( (summary.tempMax + summary.tempMin) / 2 ).toInt()}°",
                fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold,
                color = theme_light_primary // ⬅️ Primary
            )
            Text(
                text = summary.description,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = theme_light_secondary // ⬅️ Secondary
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
 * 3. 5일 예보 가로 스크롤 (색상 수정)
 */
@Composable
fun ForecastRow(
    summaries: List<DailyForecastSummary>,
    selectedDate: LocalDate?,
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
 * 4. 예보 스크롤 개별 날짜 아이템 (색상 수정)
 */
@Composable
fun ForecastDayItem(
    summary: DailyForecastSummary,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        theme_light_primary
    } else {
        theme_light_secondaryContainer
    }
    val textColor = if (isSelected) {
        theme_light_onPrimary
    } else {
        theme_light_onSecondaryContainer
    }

    val isToday = summary.date == LocalDate.now()
    val itemColorFilter = if (isToday) null else sunnyColorFilter

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
            color = textColor
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
            color = textColor
        )
        Text(
            text = "${summary.tempMin.toInt()}°",
            fontSize = 14.sp,
            color = textColor.copy(alpha = 0.7f)
        )
    }
}

/**
 * 5. 최고/최저 기온 칩 (색상 수정)
 */
@Composable
fun WeatherInfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = theme_light_secondary.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme_light_secondary
        )
    }
}