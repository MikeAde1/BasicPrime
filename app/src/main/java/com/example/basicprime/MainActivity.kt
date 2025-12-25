package com.example.basicprime

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.basicprime.presentation.PrimeNumbersViewModel
import com.example.basicprime.ui.theme.BasicPrimeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicPrimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PrimeNumbersScreen(innerPadding)
                }
            }
        }
    }
}

@Composable
fun PrimeNumbersScreen(
    innerPadding: PaddingValues,
    viewModel: PrimeNumbersViewModel = hiltViewModel<PrimeNumbersViewModel>()
) {
    val primesUiData = viewModel.primeNumbersList.collectAsLazyPagingItems()

    PrimeNumbersContainer(
        modifier = Modifier.padding(innerPadding),
        uiData = primesUiData
    )
}

/**
 * Stateful Container: Handles LazyPagingItems logic
 */
@Composable
fun PrimeNumbersContainer(
    modifier: Modifier,
    uiData: LazyPagingItems<Int>
) {
    val refreshState = uiData.loadState.refresh

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Handle Initial States (Full Screen)
        when (refreshState) {
            is LoadState.Loading -> LoadingContainer(Modifier.fillMaxSize())
            is LoadState.Error -> ErrorContainer(
                modifier = Modifier.fillMaxSize(),
                message = "An error occurred while fetching prime numbers.",
                actionText = "Refresh",
                retry = { uiData.refresh() }
            )

            is LoadState.NotLoading -> {
                if (uiData.itemCount == 0) {
                    EmptyContainer()
                }
            }
        }

        // 2. The Content List
        // We only show the list if we have items or we aren't in a hard error refresh state
        PrimeListContent(
            uiData = uiData,
            onRetryAppend = { uiData.retry() }
        )
    }
}

/**
 * Stateless Content: The actual list layout
 */
@Composable
private fun PrimeListContent(
    uiData: LazyPagingItems<Int>,
    onRetryAppend: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = uiData.itemCount,
            key = uiData.itemKey { it }
        ) { index ->
            val item = uiData[index]
            if (item != null) {
                PrimeItem(
                    number = item,
                    isLast = index == uiData.itemCount - 1
                )
            }
        }

        // Handle Pagination States (Bottom of list)
        item {
            when (val appendState = uiData.loadState.append) {
                is LoadState.Loading -> LoadingContainer(Modifier.fillMaxWidth())
                is LoadState.Error -> ErrorContainer(
                    message = "Failed to load more.",
                    actionText = "Try again",
                    retry = onRetryAppend
                )

                else -> {}
            }
        }
    }
}

@Composable
private fun PrimeItem(number: Int, isLast: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = number.toString(),
            fontWeight = FontWeight.Bold
        )
        if (!isLast) {
            HorizontalDivider()
        }
    }
}


@Composable
private fun ErrorContainer(
    modifier: Modifier = Modifier,
    message: String,
    actionText: String,
    retry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 5.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.padding(5.dp))
        Button(onClick = { retry.invoke() }) {
            Text(text = actionText)
        }
    }
}

@Composable
private fun LoadingContainer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContainer() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Empty data in initial request")
    }
}

@Preview(showBackground = true)
@Composable
fun BasicPrimePreview() {
    // To preview PagingData, we create a fake Flow
    val fakeData = PagingData.from(List(20) { 2 + it })
    val items = MutableStateFlow(fakeData).collectAsLazyPagingItems()

    BasicPrimeTheme {
        PrimeNumbersContainer(
            modifier = Modifier,
            uiData = items
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun LoadingPreview() {
    val items = MutableStateFlow(
        PagingData.empty<Int>(
            sourceLoadStates = LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()

    BasicPrimeTheme {
        PrimeNumbersContainer(Modifier, items)
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun ErrorPreview() {
    val items = MutableStateFlow(
        PagingData.empty<Int>(
            sourceLoadStates = LoadStates(
                refresh = LoadState.Error(Throwable()),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()

    BasicPrimeTheme {
        PrimeNumbersContainer(Modifier, items)
    }
}
