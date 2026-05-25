package com.booksnap.app.ui.photo

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhotoScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddPhotoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val book by viewModel.book.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var pageNumberText by remember { mutableStateOf("") }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // 저장 성공 → 뒤로
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) onSaved()
    }

    // 갤러리 picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { selectedUri = it } }

    // 카메라
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) selectedUri = cameraUri }

    fun launchCamera() {
        val photoFile = File.createTempFile("cam", ".jpg", context.cacheDir)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사진 추가", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 책 제목 표시
            book?.let {
                Text(
                    it.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 사진 미리보기 / 선택 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (selectedUri != null) {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "선택된 사진",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                        Text("사진을 선택하거나 촬영하세요", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            // 카메라 / 갤러리 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { launchCamera() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("카메라")
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("갤러리")
                }
            }

            // 페이지 번호 입력
            OutlinedTextField(
                value = pageNumberText,
                onValueChange = { v -> if (v.all { it.isDigit() } && v.length <= 5) pageNumberText = v },
                label = { Text("페이지 번호") },
                suffix = { Text("쪽") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.weight(1f))

            // 저장 버튼
            Button(
                onClick = {
                    val uri = selectedUri ?: return@Button
                    val page = pageNumberText.toIntOrNull() ?: return@Button
                    viewModel.savePhoto(uri, page)
                },
                enabled = selectedUri != null && pageNumberText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("저장", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
