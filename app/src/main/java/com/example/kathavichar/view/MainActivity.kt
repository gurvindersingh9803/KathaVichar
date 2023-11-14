package com.example.kathavichar.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kathavichar.common.BottomNavigationBar
import com.example.kathavichar.repositories.FirebaseTestRepo
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var firebaseTestRepo: FirebaseTestRepo
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = MainViewModel()

        setContent {
            KathaVicharTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    BottomNavigationBar(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun RenderGridView(mainViewModel: MainViewModel) {
    // val categories by mainViewModel._categories.observeAsState()
    // Log.i("lkjhjkhkj", categories.toString())
   /* LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
        categories?.size?.let {

        }
    })*/
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun DefaultPreview() {
    KathaVicharTheme {
        Greeting("Android")
    }
}
