package com.booksnap.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.booksnap.app.data.model.Book
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddBook: () -> Unit,
    onBookClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val books by viewModel.books.collectAsState()
    var bookToDelete by remember { mutableStateOf<Book?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "BookSnap",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBook,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "항목 만들기")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (books.isEmpty()) {
                // 비어있을 때
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        "아직 등록된 책이 없어요",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onAddBook) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("항목 만들기")
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            onDeleteClick = { bookToDelete = book }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // 삭제 확인 다이얼로그
    bookToDelete?.let { book ->
        AlertDialog(
            onDismissRequest = { bookToDelete = null },
            title = { Text("책 삭제") },
            text = { Text("'${book.title}' 과 모든 사진을 삭제할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBook(book)
                    bookToDelete = null
                }) { Text("삭제", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { bookToDelete = null }) { Text("취소") }
            }
        )
    }
}

@Composable
private fun BookCard(book: Book, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 표지 이미지
            Box(
                modifier = Modifier
                    .size(60.dp, 84.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (book.coverUrl != null) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = "책 표지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // 책 정보
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "최근 저장: ${dateFormat.format(Date(book.lastPhotoAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
