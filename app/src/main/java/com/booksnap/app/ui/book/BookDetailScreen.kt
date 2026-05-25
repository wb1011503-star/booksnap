package com.booksnap.app.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.booksnap.app.data.model.Photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    onBack: () -> Unit,
    onAddPhoto: (Long) -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val book by viewModel.book.collectAsState()
    val photos by viewModel.photos.collectAsState()
    var photoToDelete by remember { mutableStateOf<Photo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "", fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddPhoto(viewModel.getBookId()) }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "사진 추가")
            }
        }
    ) { padding ->
        if (photos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outlineVariant)
                    Text("아직 저장된 사진이 없어요", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { onAddPhoto(viewModel.getBookId()) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("사진 추가")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    PhotoGridItem(
                        photo = photo,
                        onDeleteClick = { photoToDelete = photo }
                    )
                }
                item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    photoToDelete?.let { photo ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text("사진 삭제") },
            text = { Text("${photo.pageNumber}페이지 사진을 삭제할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePhoto(photo)
                    photoToDelete = null
                }) { Text("삭제", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) { Text("취소") }
            }
        )
    }
}

@Composable
private fun PhotoGridItem(photo: Photo, onDeleteClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            AsyncImage(
                model = photo.localPath,
                contentDescription = "${photo.pageNumber}페이지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(12.dp))
            )
            // 페이지 번호 배지
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "p.${photo.pageNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            // 삭제 버튼
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
