package com.example.finrecapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.screens.*
import com.example.finrecapp.ui.theme.FinRecTheme

class MainActivity : AppCompatActivity() {
    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            FinRecTheme(darkTheme = isDarkMode) {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: TransactionViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onAddTransaction = { navController.navigate("add_transaction") },
                onSeeAllTransactions = { navController.navigate("transaction_history") },
                onNavigateToAnalysis = { navController.navigate("analysis") },
                onNavigateToExport = { navController.navigate("export") },
                onNavigateToCategories = { navController.navigate("categories") },
                onNavigateToSettings = { navController.navigate("settings") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        composable("transaction_history") {
            TransactionHistoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("add_transaction") {
            AddTransactionScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("analysis") {
            AnalysisScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("export") {
            ExportScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("categories") {
            CategoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("profile_detail") {
            ProfileDetailScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("change_password") {
            ChangePasswordScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
