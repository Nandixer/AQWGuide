package ml.nandixer.aqwguide.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ml.nandixer.aqwguide.R
import ml.nandixer.aqwguide.ui.theme.AQWGuideTheme

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val activeIcon: Int,
    val inactiveIcon: Int
){
    object Classes: BottomBarScreen(
        "classes",
        "Classes",
        R.drawable.ic_classes_active,
        R.drawable.ic_classes_inactive
    )
    object Ultras: BottomBarScreen(
        "ultras",
        "Ultras",
        R.drawable.ic_ultras_active,
        R.drawable.ic_ultras_inactive
    )
    object Farms: BottomBarScreen(
        "farms",
        "Farms",
        R.drawable.ic_farming_active,
        R.drawable.ic_farming_inactive
    )
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AQWGuideTheme {
                val viewModel: MainViewModel = hiltViewModel()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

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
                                            val selected = currentDestination?.route == it.route

                                            if (selected){
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(it.activeIcon),
                                                    contentDescription = it.title
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(it.inactiveIcon),
                                                    contentDescription = it.title
                                                )
                                            }

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
                        },
                    ) {paddingValues ->
                        BottomNavGraph(navController = navController, viewModel = viewModel, modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()))

                    }


                }

                SimplePopupNotification(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavGraph(navController: NavHostController, viewModel: MainViewModel, modifier: Modifier){

    val sortTypes = viewModel.sortTypes

    NavHost(navController = navController, startDestination = BottomBarScreen.Classes.route, modifier){
        composable(route = BottomBarScreen.Classes.route){
            Scaffold(
                topBar = {
                    var text by rememberSaveable{viewModel.classSearchText}
                    var isDropdownVisible by rememberSaveable{
                        mutableStateOf(false)
                    }

                    var isSortDropdownVisible by rememberSaveable{
                        mutableStateOf(false)
                    }
                    TextField(
                        value = text,
                        placeholder = {Text("Search by name, abbreviation, or tags")},
                        onValueChange = {
                            text = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            if (text.isNotEmpty()) {
                                IconButton(onClick = { text = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear text")
                                }
                            } else {
                                IconButton(onClick = {
                                    isDropdownVisible = true
                                }) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Open Search Menu")
                                }
                            }
                        },
                        leadingIcon = {
                            IconButton(onClick = { isSortDropdownVisible = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Sort classes by")
                            }
                        }
                    )
                    DropdownMenu(expanded = isDropdownVisible, onDismissRequest = { isDropdownVisible = false }) {
                        for (theTag in viewModel.classes.value.map { it -> it.tags }.flatten().distinct()) {
                            DropdownMenuItem(text = { Text(theTag) }, onClick = {
                                    text = theTag
                                    isDropdownVisible = false
                                }
                            )
                        }
                    }

                    DropdownMenu(expanded = isSortDropdownVisible, onDismissRequest = { isSortDropdownVisible = false }) {
                        for (sortMethod in sortTypes){
                            DropdownMenuItem(text = { Text("Sort by "+sortMethod) }, onClick = {
                                viewModel.setSortType(sortMethod)
                                isSortDropdownVisible = false
                            }
                            )
                        }

                    }
                }
            ) {paddingValues ->
                ClassesScreen(viewModel = viewModel, modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))
            }

        }
        composable(route = BottomBarScreen.Ultras.route){
            UltrasScreen(viewModel = viewModel)
        }
        composable(route = BottomBarScreen.Farms.route){
            FarmsScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun SimplePopupNotification(viewModel: MainViewModel) {
    val THIS_VERSION = 1002
    val openDialog = remember { mutableStateOf(true) }

    val newVersions = viewModel.newestVersion.value.filter { it.number > THIS_VERSION }

    val newestVersion = if (newVersions.isNotEmpty()) newVersions[0] else null
    val breaking = newVersions.any { it.breaking }



    if (newestVersion == null)
        return

    val major:Int = newestVersion.number/1000
    val minor:Int = newestVersion.number%1000

    if (openDialog.value && breaking) {

        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = {
                Text(text = "Version ${major}.${minor} Recommended")
            },
            text = { Text(newestVersion.changelog) },
            confirmButton = {
                Button(
                    onClick = { openDialog.value = false },
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (openDialog.value && !breaking){
        Toast.makeText(LocalContext.current, "Version ${major}.${minor} Available.", Toast.LENGTH_SHORT).show()
        openDialog.value = false
    }

}

