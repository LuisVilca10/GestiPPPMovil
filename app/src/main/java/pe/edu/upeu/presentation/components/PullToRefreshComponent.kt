package pe.edu.upeu.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
@Composable
fun PullToRefreshComponent(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshTriggerDistance: Float = 80f,
    content: @Composable () -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
        refreshTriggerDistance = refreshTriggerDistance.dp,
        indicator = { state, refreshTrigger ->
            CustomRefreshIndicator(
                state = state,
                refreshTrigger = refreshTrigger
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun CustomRefreshIndicator(
    state: SwipeRefreshState,
    refreshTrigger: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isRefreshing -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
            state.indicatorOffset > 0f -> {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Pull to refresh",
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = state.indicatorOffset * 0.1f // Rotación suave
                        },
                )
            }
        }
    }
}