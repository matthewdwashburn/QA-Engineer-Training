package com.revature.expensemanager;

import java.util.Arrays;

import com.revature.expensemanager.config.AppConfig;
import com.revature.expensemanager.controller.AuthController;
import com.revature.expensemanager.controller.ExpenseController;
import com.revature.expensemanager.controller.ReportController;
import com.revature.expensemanager.dao.ApprovalDAO;
import com.revature.expensemanager.dao.ExpenseDAO;
import com.revature.expensemanager.dao.JdbcApprovalDAO;
import com.revature.expensemanager.dao.JdbcExpenseDAO;
import com.revature.expensemanager.dao.JdbcReportDAO;
import com.revature.expensemanager.dao.JdbcUserDAO;
import com.revature.expensemanager.dao.ReportDAO;
import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.middleware.AuthMiddleware;
import com.revature.expensemanager.service.AuthService;
import com.revature.expensemanager.service.ExpenseService;
import com.revature.expensemanager.service.JwtService;
import com.revature.expensemanager.service.ReportExportService;
import com.revature.expensemanager.service.ReportService;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

/**
 * Builds the fully-wired Javalin application without starting it.
 *
 * <p>Production code calls {@link #build()} and then {@code .start(port)}.
 * Integration tests call {@link #build()} and start it on port {@code 0}
 * (a random free port) so REST Assured can drive real HTTP requests without
 * colliding with a running instance.
 */
public final class AppFactory {

    /** Environment variable holding a comma-separated list of allowed origins. */
    private static final String CORS_ORIGINS_ENV = "CORS_ORIGINS";

    /**
     * Origins allowed when {@code CORS_ORIGINS} is not set: the Vite dev server.
     * Both host spellings are listed because the browser treats localhost and
     * 127.0.0.1 as separate origins.
     */
    private static final String[] DEFAULT_CORS_ORIGINS = {
            "http://localhost:5173",
            "http://127.0.0.1:5173"
    };

    private AppFactory() {
    }

    /**
     * Resolves the allowed CORS origins from {@code CORS_ORIGINS}, falling back
     * to {@link #DEFAULT_CORS_ORIGINS}.
     *
     * <p>Configurable because the origin the browser sends depends on where the
     * frontend is served from, which changes between local development and a
     * container publishing a different port. A hardcoded origin silently blocks
     * every request from anywhere else.
     */
    private static String[] resolveCorsOrigins() {
        String value = System.getenv(CORS_ORIGINS_ENV);

        if (value == null || value.isBlank()) {
            return DEFAULT_CORS_ORIGINS;
        }

        String[] origins = Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        // A value of "," or " " would otherwise leave nothing to allow and
        // fail later inside allowHost with no indication of the cause.
        if (origins.length == 0) {
            throw new IllegalStateException(
                    CORS_ORIGINS_ENV + " was set but contained no origins.");
        }

        return origins;
    }

    /**
     * Constructs the dependency graph and wires all routes.
     *
     * @return an unstarted {@link Javalin} instance; the caller is responsible
     *         for calling {@code start(...)} and {@code stop()}.
     */
    public static Javalin build() {
        AppConfig appConfig = new AppConfig();

        UserDAO userDAO = new JdbcUserDAO();
        ExpenseDAO expenseDAO = new JdbcExpenseDAO();
        ApprovalDAO approvalDAO = new JdbcApprovalDAO();
        ReportDAO reportDAO = new JdbcReportDAO();

        AuthService authService = new AuthService(userDAO);
        ExpenseService expenseService = new ExpenseService(expenseDAO, approvalDAO);
        ReportService reportService = new ReportService(reportDAO, userDAO);
        ReportExportService reportExportService = new ReportExportService();
        JwtService jwtService = new JwtService(appConfig.getJwtSecret(), appConfig.getJwtExpirationHours());

        AuthMiddleware authMiddleware = new AuthMiddleware(jwtService);

        AuthController authController = new AuthController(authService, jwtService);
        ExpenseController expenseController = new ExpenseController(expenseService);
        ReportController reportController = new ReportController(reportService, reportExportService);

        String[] corsOrigins = resolveCorsOrigins();

        return Javalin.create(config -> {
            // Allow the frontend to call this API from a different origin.
            config.bundledPlugins.enableCors(cors -> cors.addRule(rule -> {
                rule.allowHost(
                        corsOrigins[0],
                        Arrays.copyOfRange(corsOrigins, 1, corsOrigins.length));
                rule.allowCredentials = true;
            }));

            config.routes.post("/login", authController::login);

            config.routes.before("/employees", authMiddleware::requireManager);
            config.routes.before("/expenses/*", authMiddleware::requireManager);
            config.routes.before("/reports/*", authMiddleware::requireManager);
            config.routes.before("/me", authMiddleware::requireManager);

            config.routes.get("/expenses/pending", expenseController::getPendingExpenses);
            config.routes.put("/expenses/{id}/review", expenseController::reviewExpense);

            config.routes.get("/reports/employee", reportController::getExpensesByEmployee);
            config.routes.get("/reports/category", reportController::getExpensesByCategory);
            config.routes.get("/reports/date", reportController::getExpensesByDateRange);
            config.routes.get("/employees", reportController::getEmployees);
            config.routes.get("/me", authController::me);

            config.routes.exception(RuntimeException.class, (e, ctx) -> {
                e.printStackTrace();
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .json(new ErrorResponse("Internal server error."));
            });
        });
    }
}
