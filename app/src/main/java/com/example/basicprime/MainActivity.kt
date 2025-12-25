package com.example.basicprime

import android.os.Bundle
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basicprime.presentation.PrimeNumbersUiState
import com.example.basicprime.presentation.PrimeNumbersViewModel
import com.example.basicprime.prime.Resource
import com.example.basicprime.ui.theme.BasicPrimeTheme
import dagger.hilt.android.AndroidEntryPoint


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
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PrimeNumbersContainer(
        innerPadding = innerPadding,
        uiState = state,
        onLoadMore = { viewModel.loadMore() }
    )
}

@Composable
fun PrimeNumbersContainer(
    innerPadding: PaddingValues,
    uiState: PrimeNumbersUiState,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    val reachedBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.let {
                it.index != 0 && it.index >= listState.layoutInfo.totalItemsCount - 3
            } ?: false
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            onLoadMore()
        }
    }

    when (uiState.resource) {
        is Resource.Ready -> {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                state = listState
            ) {
                itemsIndexed(
                    items = uiState.resource.data,
                    key = { _, primeNumber -> primeNumber }
                ) { index, primeNumber ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = primeNumber.toString(),
                            fontWeight = FontWeight.Bold
                        )

                        if (index != uiState.resource.data.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }

                if (uiState.showLoader) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.resource.message.orEmpty())
            }
        }

        Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BasicPrimePreview() {
    BasicPrimeTheme {
        PrimeNumbersContainer(
            PaddingValues(0.dp),
            PrimeNumbersUiState(
                resource = Resource.Ready((0..10).toList())
            )
        )
    }
}
