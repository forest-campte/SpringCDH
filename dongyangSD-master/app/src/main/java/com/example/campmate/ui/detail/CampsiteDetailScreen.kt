package com.example.campmate.ui.detail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack // (참고) 중복 import지만 컴파일에 영향 없음
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // (추가)
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.campmate.R
import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.CampsiteSite
import com.example.campmate.data.model.Review
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import kotlinx.coroutines.flow.collectLatest // (추가)
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteDetailScreen(
    onNavigateUp: () -> Unit,
    viewModel: CampsiteDetailViewModel = hiltViewModel()
) {
    // --- 상태 변수 정의 ---
    val campsite by viewModel.campsite.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    var adultCount by remember { mutableStateOf(1) }
    var childCount by remember { mutableStateOf(0) }
    var selectedSite by remember { mutableStateOf<CampsiteSite?>(null) }

    val datePickerState = rememberDateRangePickerState()
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()) }

    // (추가) 1. ViewModel의 예약 결과를 구독(observe)
    LaunchedEffect(Unit) {
        viewModel.reservationResult.collectLatest { success ->
            if (success) {
                // 2. 예약 성공 시 토스트 + 화면 이동
                val message = context.getString(R.string.reservation_complete_message)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                onNavigateUp()
            } else {
                // 3. 예약 실패 시 (네트워크 오류, 서버 500 에러 등)
                val message = "예약에 실패했습니다. 다시 시도해주세요."
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(campsite?.name ?: "상세보기") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기") // (수정) AutoMirrored로 변경
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val startDate = datePickerState.selectedStartDateMillis
                    val endDate = datePickerState.selectedEndDateMillis

                    if (startDate != null && endDate != null && selectedSite != null) {

                        // (수정) 4. ViewModel 함수 호출만 실행 (토스트, onNavigateUp 모두 제거)
                        viewModel.makeReservation(
                            adultCount,
                            childCount,
                            startDate,
                            endDate,
                            selectedSite!!
                        )

                        // (삭제)
                        // val message = context.getString(R.string.reservation_complete_message)
                        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        // onNavigateUp()
                    } else {
                        val message = context.getString(R.string.please_select_date_and_site)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text(stringResource(R.string.reserve), fontSize = 18.sp)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Scaffold의 패딩 적용
        ) {
            when {
                isLoading -> {
                    // --- 로딩 중 ---
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    // --- 에러 발생 ---
                    Text(
                        text = error!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                campsite != null -> {
                    // --- 성공: 데이터 표시 ---
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // 1. 캠핑장 이미지
                        item {
                            Image(
                                painter = rememberAsyncImagePainter(campsite!!.imageUrl),
                                contentDescription = campsite!!.name,
                                modifier = Modifier.fillMaxWidth().height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // 2. 캠핑장 이름 및 설명
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(campsite!!.name ?: "", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(campsite!!.description ?: "", style = MaterialTheme.typography.bodyLarge)
                            }
                        }

                        item { Divider(modifier = Modifier.padding(horizontal = 16.dp)) }

                        // 3. 날짜 선택
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("날짜 선택", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))

                                // 선택된 날짜 표시
                                val startDateMillis = datePickerState.selectedStartDateMillis
                                val endDateMillis = datePickerState.selectedEndDateMillis
                                val selectedDateText = if (startDateMillis != null && endDateMillis != null) {
                                    "${dateFormatter.format(Date(startDateMillis))} - ${dateFormatter.format(Date(endDateMillis))}"
                                } else {
                                    "날짜를 선택해주세요"
                                }

                                Text(text = selectedDateText, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))

                                // 날짜 선택 다이얼로그 열기 버튼
                                Button(onClick = { showDatePickerDialog = true }) {
                                    Text("날짜 선택하기")
                                }
                            }
                        }
                        item { Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

                        // 4. 인원 수 선택
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("인원 선택", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                GuestCounter(
                                    label = "성인",
                                    count = adultCount,
                                    onCountChange = { adultCount = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                GuestCounter(
                                    label = "아동",
                                    count = childCount,
                                    onCountChange = { childCount = it }
                                )
                            }
                        }

                        item { Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

                        // 5. 사이트 선택
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text("사이트 선택", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        // (campsite.sites가 List<CampsiteSite>라고 가정)
                        items(campsite!!.sites) { site ->
                            SiteItem(
                                site = site,
                                isSelected = site == selectedSite,
                                onClick = { selectedSite = site },
                                modifier = Modifier.padding(horizontal = 16.dp) // 패딩 추가
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }


                        item { Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

                        // 6. 리뷰
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text("방문자 리뷰 (${reviews.size}개)", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        //  .forEach 대신 items 사용
                        items(reviews) { review ->
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text("${review.authorName ?: "익명"}: (평점: ${review.rating ?: "-"})")
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(review.content ?: "", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // 하단 버튼 공간 확보
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
    // DatePickerDialog 컴포저블
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false }, // 다이얼로그 밖 클릭 시 닫기
            confirmButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { // 확인 버튼 클릭 시 닫기
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { // 취소 버튼 (선택 사항)
                    Text("취소")
                }
            }
        ) {
            // 다이얼로그 내부에 DateRangePicker 배치
            DateRangePicker(state = datePickerState)
        }
    }

}




/** 1030cdh
 * 상세 정보 본문 UI
 */
@Composable
fun SiteItem(
    site: CampsiteSite,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Modifier 파라미터 추가
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(), // 전달받은 modifier 사용
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        val siteNameText = site.name ?: "이름 없음"
        val priceText = site.price?.let { "${it}원" } ?: "가격 정보 없음"
        Text("$siteNameText - $priceText")
    }
}

// (GuestCounter 함수는 기존과 동일)
@Composable
fun GuestCounter(label: String, count: Int, onCountChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 18.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (count > 0) onCountChange(count - 1) }) { Text("-") }
            Text(count.toString(), modifier = Modifier.padding(horizontal = 16.dp), fontSize = 18.sp)
            Button(onClick = { onCountChange(count + 1) }) { Text("+") }
        }
    }
}