package com.example.financialmanagement.network;

import com.example.financialmanagement.config.AppConfig;

/**
 * Network Configuration - Cấu hình mạng
 * Chứa các thông số cấu hình cho API calls
 */
public class NetworkConfig {
    
    // Base API URL - Sử dụng AppConfig để quản lý môi trường
    public static final String BASE_URL = AppConfig.getBaseUrl();
    
    // Alternative URLs for different environments
    public static final String LOCAL_URL = "http://192.168.1.25:8000/api/";
    public static final String NETWORK_URL = "http://192.168.1.25:3000/api/";
    
    /**
     * Get the appropriate base URL based on environment
     * @param useLocal true for local development, false for network
     * @return Base URL string
     */
    public static String getBaseUrl(boolean useLocal) {
        return useLocal ? LOCAL_URL : NETWORK_URL;
    }
    
    // Timeout configurations - Increased for production environment
    // Production server may be slower than local development
    public static final int CONNECT_TIMEOUT_SECONDS = 60;
    public static final int READ_TIMEOUT_SECONDS = 60;
    public static final int WRITE_TIMEOUT_SECONDS = 90;
    
    // API Endpoints
    public static class Endpoints {
        // Authentication
        public static final String LOGIN = "auth/login";
        public static final String REGISTER = "auth/signup";
        public static final String LOGOUT = "auth/logout";
        public static final String REFRESH_TOKEN = "auth/refresh";
        public static final String RESET_PASSWORD = "auth/reset-password";
        
        // Projects
        public static final String PROJECTS = "projects";
        public static final String PROJECT_DETAIL = "projects/{id}";
        public static final String PROJECT_SEARCH = "projects/search";
        
        // Expenses
        public static final String EXPENSES = "project-expenses";
        public static final String EXPENSE_DETAIL = "project-expenses/{id}";
        public static final String EXPENSE_OBJECTS = "expense-objects";
        public static final String EXPENSE_OBJECTS_PUBLIC = "expense-objects/public";
        
        // Customers
        public static final String CUSTOMERS = "customers";
        public static final String CUSTOMER_DETAIL = "customers/{id}";
        public static final String CUSTOMERS_PUBLIC = "customers/public-list";
        
        // Users
        public static final String USERS = "users";
        public static final String USER_DETAIL = "users/{id}";
        public static final String USER_PROFILE = "users/profile";
        
        // Employees
        public static final String EMPLOYEES = "employees";
        public static final String EMPLOYEE_DETAIL = "employees/{id}";
        public static final String EMPLOYEES_PUBLIC = "employees/public-list";
        public static final String DEPARTMENTS = "employees/departments";
        public static final String POSITIONS = "employees/positions";
        
        // Reports
        public static final String REPORTS_BALANCE_SHEET = "reports/balance-sheet";
        public static final String REPORTS_CASH_FLOW = "reports/cash-flow";
        public static final String REPORTS_PL = "reports/pl-report";
        public static final String REPORTS_GENERAL_LEDGER = "reports/general-ledger";
        public static final String REPORTS_EXPENSES_BY_VENDOR = "reports/expenses-by-vendor";
        public static final String REPORTS_SALES_BY_CUSTOMER = "reports/sales-by-customer";
        
        // Sales
        public static final String SALES_RECEIPTS = "sales/receipts";
        public static final String SALES_RECEIPT_DETAIL = "sales/receipts/{id}";
        
        // Quotes
        public static final String QUOTES = "sales/quotes";
        public static final String QUOTE_DETAIL = "sales/quotes/{id}";
        
        // Invoices
        public static final String INVOICES = "sales/invoices";
        public static final String INVOICE_DETAIL = "sales/invoices/{id}";
        
        // Cost Breakdown
        public static final String COST_BREAKDOWN = "cost-breakdown/cost-breakdowns";
        public static final String COST_RATIOS = "cost-breakdown/cost-ratios";
        public static final String COST_PARTIES = "cost-breakdown/cost-parties";
        public static final String COST_CATEGORIES = "cost-breakdown/cost-categories";
        
        // Tasks
        public static final String TASKS = "tasks";
        public static final String TASK_DETAIL = "tasks/{id}";
        
        // Products - Using web API endpoints directly
        public static final String PRODUCTS = "sales/products";
        public static final String PRODUCT_DETAIL = "sales/products/{id}";
        
        // Product Categories - Using web API endpoints directly
        public static final String PRODUCT_CATEGORIES = "sales/product-categories";
        public static final String PRODUCT_CATEGORY_DETAIL = "sales/product-categories/{id}";
        
        // Product Rules
        public static final String MATERIAL_ADJUSTMENT_RULES = "material-adjustment-rules";
        public static final String MATERIAL_ADJUSTMENT_RULE_DETAIL = "material-adjustment-rules/{id}";
    }
    
    // HTTP Status Codes
    public static class StatusCodes {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int NO_CONTENT = 204;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
    
    // Request Headers
    public static class Headers {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String AUTHORIZATION = "Authorization";
        public static final String ACCEPT = "Accept";
        public static final String USER_AGENT = "User-Agent";
    }
    
    // Content Types
    public static class ContentTypes {
        public static final String JSON = "application/json";
        public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
        public static final String MULTIPART = "multipart/form-data";
    }
    
    // Pagination
    public static class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
    }
    
    // Cache Configuration
    public static class Cache {
        public static final long CACHE_DURATION_SECONDS = 300; // 5 minutes
        public static final long MAX_CACHE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    }
}
