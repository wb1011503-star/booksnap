package com.booksnap.app.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.booksnap.app.data.db.OpenLibraryBook
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onBack: () -> Unit,
    onBookAdded: (Long) -> Unit,
    viewModel: AddBookViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    // 직접 입력 다이얼로그 상태
    var showManualDialog by remember { mutableStateOf(false) }
    var manualTitle by remember { mutableStateOf("") }
    var manualAuthor by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("책 추가", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // 검색창
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("책 제목 또는 저자 검색") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(8.dp))

            // 직접 입력 버튼
            TextButton(
                onClick = { showManualDialog = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("검색 결과가 없으면? 직접 입력")
            }

            // 검색 결과 목록
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(results, key = { it.key }) { book ->
                    SearchResultItem(book = book, onClick = {
                        scope.launch {
                            val id = viewModel.addBook(book)
                            onBookAdded(id)
                        }
                    })
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }

    // 직접 입력 다이얼로그
    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("직접 입력") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = manualTitle,
                        onValueChange = { manualTitle = it },
                        label = { Text("책 제목 *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = manualAuthor,
                        onValueChange = { manualAuthor = it },
                        label = { Text("저자") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (manualTitle.isNotBlank()) {
                            scope.launch {
                                val id = viewModel.addManualBook(manualTitle.trim(), manualAuthor.trim())
                                showManualDialog = false
                                onBookAdded(id)
                            }
                        }
                    },
                    enabled = manualTitle.isNotBlank()
                ) { Text("추가") }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) { Text("취소") }
            }
        )
    }
}

@Composable
private fun SearchResultItem(book: OpenLibraryBook, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp, 62.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val url = book.coverUrl()
                if (url != null) {
                    AsyncImage(
                        model = url,
                        contentDescription = "표지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(book.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(book.authorDisplay(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                book.firstPublishYear?.let {
                    Text("$it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
