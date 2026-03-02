package com.example.postsapp.presentation.paginatedposts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.postsapp.domain.model.Post
import com.example.postsapp.presentation.posts.ErrorScreen
import com.example.postsapp.presentation.posts.LoadingScreen
import com.example.postsapp.presentation.posts.PostItem

/**
 * Why collectAsLazyPagingItems()?
 * Converts Flow<PagingData<Post>> into LazyPagingItems<Post>,
 * which integrates directly with LazyColumn. Paging 3 automatically
 * loads the next page as the user scrolls near the prefetch distance.
 *
 * Why check loadState.refresh vs loadState.append?
 * - refresh = initial load state (first page)
 * - append = loading more pages state (subsequent pages)
 * Each can be Loading, Error, or NotLoading independently.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginatedPostsScreen(
    onPostClick: (Post) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: PaginatedPostsViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.postsFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Paginated Posts") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    LoadingScreen()
                }
                is LoadState.Error -> {
                    val error = lazyPagingItems.loadState.refresh as LoadState.Error
                    ErrorScreen(
                        message = error.error.localizedMessage
                            ?: "Failed to load posts.",
                        onRetry = { lazyPagingItems.retry() }
                    )
                }
                is LoadState.NotLoading -> {
                    if (lazyPagingItems.itemCount == 0) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No posts available.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        PaginatedPostsList(
                            lazyPagingItems = lazyPagingItems,
                            onPostClick = onPostClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * Why items(count) instead of items(items)?
 * LazyPagingItems uses index-based access. Accessing
 * lazyPagingItems[index] triggers Paging to prefetch
 * the next page when nearing the prefetch distance.
 *
 * Why itemKey { it.id }?
 * Provides stable keys for LazyColumn item reuse,
 * improving performance during scrolling and recomposition.
 */
@Composable
fun PaginatedPostsList(
    lazyPagingItems: LazyPagingItems<Post>,
    onPostClick: (Post) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = { index -> lazyPagingItems.peek(index)?.id ?: index }
        ) { index ->
            val post = lazyPagingItems[index]
            if (post != null) {
                PostItem(
                    post = post,
                    onClick = { onPostClick(post) }
                )
            }
        }

        // Append loading indicator — shown while loading more pages
        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    val error = lazyPagingItems.loadState.append as LoadState.Error
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error.error.localizedMessage
                                ?: "Failed to load more posts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { lazyPagingItems.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is LoadState.NotLoading -> { /* No-op */ }
        }
    }
}
