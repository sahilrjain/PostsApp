# PostsApp — Pagination with Paging 3

## Context

The posts list originally fetches all 100 posts from JSONPlaceholder in a single API call. This branch adds a **new paginated screen** using Jetpack Paging 3 while keeping the original screen intact, so both approaches coexist in the same project for learning and comparison.

## Architecture

```
com.example.postsapp/
├── data/
│   ├── model/          PostDto (Moshi DTO)
│   ├── paging/         PostsPagingSource (NEW — Paging 3 data source)
│   ├── remote/         PostsApiService (Retrofit — added getPagedPosts)
│   └── repository/     PostsRepositoryImpl (added getPagedPosts using Pager)
├── domain/
│   ├── model/          Post (domain model)
│   └── repository/     PostsRepository (added getPagedPosts interface)
├── presentation/
│   ├── navigation/     PostsNavHost (updated — HomeScreen with two options)
│   ├── posts/          PostsScreen + PostsViewModel (UNCHANGED — loads all posts)
│   ├── paginatedposts/ PaginatedPostsScreen + PaginatedPostsViewModel (NEW)
│   └── postdetail/     PostDetailScreen (UNCHANGED)
└── di/                 Hilt modules (UNCHANGED)
```

## Changes Overview

| Action | File | Layer |
|--------|------|-------|
| MODIFY | `app/build.gradle.kts` | Build — added `paging-runtime` and `paging-compose` |
| MODIFY | `PostsApiService.kt` | Data — added `getPagedPosts()` with `_page` and `_limit` params |
| CREATE | `PostsPagingSource.kt` | Data — `PagingSource<Int, Post>` loading pages from API |
| MODIFY | `PostsRepository.kt` | Domain — added `getPagedPosts(): Flow<PagingData<Post>>` |
| MODIFY | `PostsRepositoryImpl.kt` | Data — implemented `getPagedPosts()` with `Pager` and `PagingConfig` |
| CREATE | `PaginatedPostsViewModel.kt` | Presentation — exposes `Flow<PagingData<Post>>` with `cachedIn` |
| CREATE | `PaginatedPostsScreen.kt` | Presentation — uses `LazyPagingItems` with load state handling |
| MODIFY | `PostsNavHost.kt` | Navigation — added `HomeScreen` with both screen options |

**Unchanged:** `PostsScreen.kt`, `PostsViewModel.kt`, `PostsContract.kt`, `Post.kt`, `PostDto.kt`

## How Paging 3 Works in This Project

### Data Flow
```
API (page/limit) → PostsPagingSource.load() → Pager → Flow<PagingData<Post>>
    → cachedIn(viewModelScope) → collectAsLazyPagingItems() → LazyColumn
```

### Key Components

1. **`PostsPagingSource`** — Defines how to load each page. Called automatically by Paging 3 as the user scrolls. Returns `LoadResult.Page` with data and prev/next keys, or `LoadResult.Error` on failure.

2. **`Pager` + `PagingConfig`** — Created in the repository. Configures page size (10), prefetch distance (2), and creates new `PostsPagingSource` instances for each refresh.

3. **`cachedIn(viewModelScope)`** — Caches PagingData so configuration changes (rotation) don't re-trigger network calls.

4. **`collectAsLazyPagingItems()`** — Compose bridge that converts `Flow<PagingData>` into `LazyPagingItems`, integrating directly with `LazyColumn`.

### Load States
- **`loadState.refresh`** — Initial load: shows full-screen spinner or error
- **`loadState.append`** — Loading more pages: shows bottom spinner or inline error with retry

## Verification
- Build and run the app
- Home screen shows two buttons: "All Posts" and "Paginated Posts"
- **All Posts** — loads all 100 posts at once (original behavior)
- **Paginated Posts** — loads 10 at a time with infinite scroll
- Scroll to bottom in paginated view to see loading indicator and next page loading
- After all 100 posts, scrolling stops (no more pages)
- Both screens navigate to the same post detail page
