package ml.nandixer.aqwguide.presentation

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.presentation.ClassListItem
import ml.nandixer.aqwguide.presentation.ClassesScreen
import ml.nandixer.aqwguide.presentation.MainViewModel
import ml.nandixer.aqwguide.ui.theme.AQWGuideTheme

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Classes: BottomBarScreen(
        "classes",
        "Classes",
        Icons.Default.Face
    )
    object Ultras: BottomBarScreen(
        "ultras",
        "Ultras",
        Icons.Default.AddCircle
    )
    object Farms: BottomBarScreen(
        "farms",
        "Farms",
        Icons.Default.ShoppingCart
    )
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
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
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    Scaffold(
                        bottomBar = {
                            NavigationBar() {
                                for (it in listOf(BottomBarScreen.Classes, BottomBarScreen.Ultras, BottomBarScreen.Farms)) {
                                    NavigationBarItem(
                                        label = {
                                            Text(text = it.title)
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = it.icon,
                                                contentDescription = it.title
                                            )
                                        },
                                        selected = currentDestination?.hierarchy?.any { dest ->
                                            dest.route == it.route
                                        } == true,
                                        onClick = {
                                            navController.navigate(it.route)
                                        }
                                    )
                                }
                            }
                        }
                    ) {paddingValues ->
                        BottomNavGraph(navController = navController, viewModel = viewModel, modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()))

                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavGraph(navController: NavHostController, viewModel: MainViewModel, modifier: Modifier){
    NavHost(navController = navController, startDestination = BottomBarScreen.Classes.route, modifier){
        composable(route = BottomBarScreen.Classes.route){
            ClassesScreen(viewModel = viewModel)
        }
        composable(route = BottomBarScreen.Ultras.route){
            UltrasScreen(viewModel = viewModel)
        }
        composable(route = BottomBarScreen.Farms.route){
            FarmsScreen(viewModel = viewModel)
        }
    }
}
