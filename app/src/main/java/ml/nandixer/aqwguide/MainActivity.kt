package ml.nandixer.aqwguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.presentation.MainViewModel
import ml.nandixer.aqwguide.ui.theme.AQWGuideTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AQWGuideTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel = hiltViewModel()
                    MainScreenList(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreenList(viewModel: MainViewModel){
    val classes = viewModel.classes.value
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        items(classes){theClass ->
            ClassListItem(theClass)
        }

    }

}

@Composable
fun ClassListItem(theClass: CombatClass){
    val ratings = theClass.ratings
    val altNames = theClass.names.drop(1).joinToString (" | ")
    ElevatedCard(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(theClass.names[0], fontSize = 24.sp)
            if (altNames.isNotEmpty()){
                Text(text = altNames, fontSize = 12.sp, fontStyle = FontStyle.Italic)
            }
            Text(text = "Damage: ${ratings.damage}, Survival: ${ratings.survival}, Support: ${ratings.support}")
            Text(text = "Farming: ${ratings.farming}, PvP: ${ratings.pvp}, Ultras: ${ratings.ultras}")
        }
    }
}