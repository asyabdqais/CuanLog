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
                    navController.navigate("mode_selection") {
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
        composable("mode_selection") {
            ModeSelectionScreen(
                viewModel = viewModel,
                onSelectFinancial = { navController.navigate("dashboard") },
                onSelectCashier = { navController.navigate("business_dashboard") }
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
                },
                onSwitchMode = {
                    navController.navigate("mode_selection") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        composable("business_dashboard") {
            BusinessDashboardScreen(
                viewModel = viewModel,
                onNavigateToInventory = { navController.navigate("inventory") },
                onNavigateToCashier = { navController.navigate("add_sale") },
                onNavigateToPurchases = { navController.navigate("add_purchase") },
                onNavigateToReport = { navController.navigate("business_history") },
                onNavigateToSuppliers = { navController.navigate("suppliers") },
                onSwitchMode = {
                    navController.navigate("mode_selection") {
                        popUpTo("business_dashboard") { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("business_dashboard") { inclusive = true }
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
        // Business Detail Screens
        composable("inventory") {
            InventoryScreen(viewModel = viewModel, onAddProduct = { navController.navigate("add_product") }, onBack = { navController.popBackStack() })
        }
        composable("add_purchase") {
            AddPurchaseScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("add_sale") {
            AddSaleScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("business_history") {
            BusinessHistoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("suppliers") {
            SupplierScreen(viewModel = viewModel, onAddSupplier = { navController.navigate("add_supplier") }, onBack = { navController.popBackStack() })
        }
        composable("add_supplier") {
            AddSupplierScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("add_product") {
            AddProductScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
