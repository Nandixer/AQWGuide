package ml.nandixer.aqwguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            ClassListItem(theClass, viewModel)
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListItem(theClass: CombatClass, viewModel: MainViewModel){
    val ratings = theClass.ratings
    val enh = theClass.enhancements[0]
    val altNames = theClass.names.drop(1).joinToString (" | ")
    val isExpanded = theClass.abbr == viewModel.chosenClass.value
    val dps = theClass.enhancements[0].dps

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    easing = LinearOutSlowInEasing
                )
            ),
        onClick = {
            viewModel.chooseClass(theClass.abbr)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(theClass.names[0], fontSize = 24.sp)
            if (altNames.isNotEmpty()){
                Text(text = altNames, fontSize = 12.sp, fontStyle = FontStyle.Italic)
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Damage: ${ratings.damage}, Survival: ${ratings.survival}, Support: ${ratings.support}")
                Text(text = "Farming: ${ratings.farming}, PvP: ${ratings.pvp}, Ultras: ${ratings.ultras}")

                Spacer(modifier = Modifier.height(16.dp))


                Enhancements(text = enh.weapon)
                Enhancements(text = enh.armor)
                Enhancements(text = enh.helm)
                Enhancements(text = enh.cape)

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.background(color = Color.White)
                ) {
                    // damage tests
                    if (dps.classhall != null) {
                        Row {
                            Text(text = "Classhall DPS:")
                            Spacer(modifier = Modifier.weight(1.0f))
                            Text(dps.classhall.toString())
                        }
                    }

                    if (dps.classhallNsod != null) {
                        Row {
                            Text(text = "Classhall DPS +51%:")
                            Spacer(modifier = Modifier.weight(1.0f))
                            if (dps.classhall != null) {
                                val percent =
                                    dps.classhallNsod!!.toFloat() / dps.classhall!!.toFloat()
                                val growth = (percent * 100 - 100).toInt()
                                Text(text = "+$growth% ", color = Color.Green)
                            }
                            Text(dps.classhallNsod.toString())
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (dps.revenant != null) {
                        Row {
                            Text(text = "Revenant KPM:")
                            Spacer(modifier = Modifier.weight(1.0f))
                            Text(dps.revenant.toString())
                        }
                    }

                    if (dps.revenantNsod != null) {
                        Row {
                            Text(text = "Revenant KPM +51%:")
                            Spacer(modifier = Modifier.weight(1.0f))
                            if (dps.revenant != null) {
                                val percent =
                                    dps.revenantNsod!!.toFloat() / dps.revenant!!.toFloat()
                                val growth = (percent * 100 - 100).toInt()
                                Text(text = "+$growth% ", color = Color.Green)
                            }
                            Text(dps.revenantNsod.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val regex = "^[\\d\\s]+$".toRegex()
                for (line in theClass.combo){
                    if (regex.matches(line)){
                        Text(line, fontSize = 24.sp)
                    }else{
                        Text(line)
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    theClass.tags.joinToString(", "),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )

            }
        }
    }
}

@Composable
fun Enhancements(text: String){
    Text(text = text,
        color = Color.White,
        modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .height(32.dp)
            .border(1.dp, Color.Gray)
            .padding(start = 8.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)

    )
}