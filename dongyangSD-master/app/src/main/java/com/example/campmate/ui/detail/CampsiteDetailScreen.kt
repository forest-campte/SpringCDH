package com.example.campmate.ui.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campmate.R
import com.example.campmate.data.model.CampsiteSite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteDetailScreen(
    onNavigateUp: () -> Unit,
    viewModel: CampsiteDetailViewModel = hiltViewModel()
) {
    // --- 상태 변수 정의 ---
    val campsite by viewModel.campsite.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val context = LocalContext.current

    // ✅ [추가] 에러 방지를 위해 누락된 상태 변수들 정의
    // (이 변수들은 UI 어딘가에서 사용되고 있을 것입니다)
    var adultCount by remember { mutableStateOf(1) }
    var childCount by remember { mutableStateOf(0) }
    var selectedSite by remember { mutableStateOf<CampsiteSite?>(null) }

    // ✅ [추가] DateRangePicker 상태 (Material3)
    val datePickerState = rememberDateRangePickerState()

    Scaffold(
        topBar = {
            // (예시) TopBar - 본인의 TopBar Composable로 교체하세요
            TopAppBar(title = { Text(campsite?.name ?: "상세보기") })
        },
        bottomBar = {
            Button(
                onClick = {
                    // ✅ [수정] datePickerState에서 날짜 가져오기
                    val startDate = datePickerState.selectedStartDateMillis
                    val endDate = datePickerState.selectedEndDateMillis

                    // ✅ [수정] 실제 토큰 가져오기 (임시값)
                    // 이 부분은 SharedPreferences나 DataStore에서 가져와야 합니다.
                    val authToken = "Bearer YOUR_ACTUAL_TOKEN" // 👈 [수정필요]

                    if (startDate != null && endDate != null && selectedSite != null) {

                        // ✅ [수정] ViewModel 호출 시 authToken 포함
                        viewModel.makeReservation(
                            authToken,
                            adultCount,
                            childCount,
                            startDate,
                            endDate,
                            selectedSite!!
                        )

                        val message = context.getString(R.string.reservation_complete_message)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onNavigateUp()
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
        // ✅ [수정] paddingValues를 Column의 Modifier에 적용
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp) // (선택) 컨텐츠 영역의 추가 패딩
        ) {
            // --- 본문의 UI Composable ---
            // (예: CampsiteDetailContent, SiteItem, GuestCounter 등)
            // ...
            Text("캠핑장 상세 정보 UI가 여기에 표시됩니다.")
            // ...
        }
    }
}