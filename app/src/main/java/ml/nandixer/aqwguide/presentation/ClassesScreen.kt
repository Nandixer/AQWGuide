package ml.nandixer.aqwguide.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ClassesScreen(viewModel: MainViewModel){
    val classes = viewModel.classes.value
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        items(classes){theClass ->
            ClassListItem(theClass, viewModel)
        }

    }

}
